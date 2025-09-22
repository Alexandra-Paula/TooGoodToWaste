package org.application.waste.service;

import org.apache.poi.ss.usermodel.*;
import org.application.waste.entity.*;
import org.application.waste.enums.Availability;
import org.application.waste.enums.RecommendationStatus;
import org.application.waste.exceptions.InvalidFileException;
import org.application.waste.exceptions.NotFoundException;
import org.application.waste.repository.ProductLinkRepository;
import org.application.waste.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductLinkServiceImpl implements ProductLinkService {
    private final ProductLinkRepository productLinkRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final DiscountService discountService;
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";

    public ProductLinkServiceImpl(ProductLinkRepository productLinkRepository, UserRepository userRepository, ProductService productService, CategoryService categoryService, DiscountService discountService) {
        this.productLinkRepository = productLinkRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.categoryService = categoryService;
        this.discountService = discountService;
    }

    @Override
    public void saveProductsFromLink(String fileUrl, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Nu s-a putut găsi acest utilizator"));

        if (user.getProductLink() != null) {
            throw new InvalidFileException("Utilizatorul poate avea un singur link salvat, acesta există deja");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path targetLocation = null;

        try {
            if (fileUrl.contains("docs.google.com/spreadsheets")) {
                String fileId = fileUrl.split("/d/")[1].split("/")[0];
                fileUrl = "https://docs.google.com/spreadsheets/d/" + fileId + "/export?format=xlsx";
            } else if (fileUrl.contains("drive.google.com")) {
                String fileId = fileUrl.split("/d/")[1].split("/")[0];
                fileUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
            }

            Files.createDirectories(uploadPath);

            URL url = new URL(fileUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            InputStream in = conn.getInputStream();

            String fileName = "downloaded_file";
            String disposition = conn.getHeaderField("Content-Disposition");

            if (disposition != null) {
                if (disposition.contains("filename*=")) {
                    String encodedName = disposition.split("filename\\*=UTF-8''")[1];
                    fileName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
                } else if (disposition.contains("filename=")) {
                    fileName = disposition.split("filename=")[1].replace("\"", "");
                }
            }

            fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");

            targetLocation = uploadPath.resolve(fileName);
            Files.copy(in, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            String lowerFileName = fileName.toLowerCase();
            if (!(lowerFileName.endsWith(".csv") || lowerFileName.endsWith(".xls") || lowerFileName.endsWith(".xlsx"))) {
                Files.deleteIfExists(targetLocation);
                throw new InvalidFileException("Sunt acceptate doar fișiere CSV sau Excel (.csv, .xls, .xlsx)");
            }

            ProductLink productLink = new ProductLink();
            productLink.setOriginalLink(fileUrl);
            productLink.setAddedDate(LocalDateTime.now());
            productLink.setUser(user);

            productLinkRepository.save(productLink);

            List<Product> existingProducts = productService.findAllByProductLink(productLink);
            Set<String> remainingCodes = existingProducts.stream()
                    .map(Product::getCode)
                    .collect(Collectors.toSet());

            try (FileInputStream fis = new FileInputStream(targetLocation.toFile());
                 Workbook workbook = WorkbookFactory.create(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                boolean firstRow = true;

                for (Row row : sheet) {
                    if (firstRow) {
                        firstRow = false;
                        continue;
                    }

                    String categoryName = getCellAsString(row.getCell(0)) != null && !getCellAsString(row.getCell(0)).isEmpty() ? getCellAsString(row.getCell(0)) : "";
                    int discountPercent = getCellAsString(row.getCell(1)) != null && !getCellAsString(row.getCell(1)).isEmpty() ? (int) Double.parseDouble(getCellAsString(row.getCell(1))) : 0;
                    LocalDateTime expiryDate = getCellAsString(row.getCell(2)) != null && !getCellAsString(row.getCell(2)).isEmpty() ? LocalDate.parse(getCellAsString(row.getCell(2))).atStartOfDay() : LocalDateTime.now();
                    String productName = getCellAsString(row.getCell(3)) != null && !getCellAsString(row.getCell(3)).isEmpty() ? getCellAsString(row.getCell(3)) : "";
                    String pickupAddress = getCellAsString(row.getCell(4)) != null && !getCellAsString(row.getCell(4)).isEmpty() ? getCellAsString(row.getCell(4)) : "";
                    int quantity = getCellAsString(row.getCell(5)) != null && !getCellAsString(row.getCell(5)).isEmpty() ? (int) Double.parseDouble(getCellAsString(row.getCell(5))) : 0;
                    String quality = getCellAsString(row.getCell(6)) != null && !getCellAsString(row.getCell(6)).isEmpty() ? getCellAsString(row.getCell(6)) : "";
                    double initialPrice = getCellAsString(row.getCell(7)) != null && !getCellAsString(row.getCell(7)).isEmpty() ? Double.parseDouble(getCellAsString(row.getCell(7))) : 0.0;
                    double finalPrice = getCellAsString(row.getCell(8)) != null && !getCellAsString(row.getCell(8)).isEmpty() ? Double.parseDouble(getCellAsString(row.getCell(8))) : 0.0;
                    String description = getCellAsString(row.getCell(9)) != null && !getCellAsString(row.getCell(9)).isEmpty() ? getCellAsString(row.getCell(9)) : "";
                    String productCode = getCellAsString(row.getCell(10)) != null && !getCellAsString(row.getCell(10)).isEmpty() ? getCellAsString(row.getCell(10)) : "";
                    String productUnit = getCellAsString(row.getCell(11)) != null && !getCellAsString(row.getCell(11)).isEmpty() ? getCellAsString(row.getCell(11)) : "";
                    String productImagePath = getCellAsString(row.getCell(12)) != null && !getCellAsString(row.getCell(12)).isEmpty() ? getCellAsString(row.getCell(12)) : "";
                    String companyImagePath = getCellAsString(row.getCell(13)) != null && !getCellAsString(row.getCell(13)).isEmpty() ? getCellAsString(row.getCell(13)) : "";

                    Product product = productService.findByCode(productCode).orElse(new Product());
                    product.setProductLink(productLink);
                    boolean updated = false;

                    remainingCodes.remove(productCode);

                    if (categoryName != null && !categoryName.isEmpty()) {
                        Category category = categoryService.findByName(categoryName);
                        if (category == null) {
                            category = new Category();
                            category.setCategoryName(categoryName);
                            categoryService.saveCategory(category);
                            updated = true;
                        }
                        if (product.getCategory() == null || !product.getCategory().getId().equals(category.getId())) {
                            product.setCategory(category);
                            updated = true;
                        }
                    }

                    if (discountPercent > 0) {
                        Discount discount = new Discount();
                        discount.setPercent(discountPercent);
                        discount.setExpiryDate(expiryDate);

                        if (product.getProductDiscount() == null ||
                                product.getProductDiscount().getPercent() != discountPercent ||
                                !product.getProductDiscount().getExpiryDate().equals(expiryDate)) {
                            discountService.saveDiscount(discount);
                            product.setProductDiscount(discount);
                            updated = true;
                        }
                    }

                    if (!Objects.equals(product.getName(), productName)) {
                        product.setName(productName);
                        updated = true;
                    }

                    if (!Objects.equals(product.getPickupAddress(), pickupAddress)) {
                        product.setPickupAddress(pickupAddress);
                        updated = true;
                    }

                    if (product.getQuantity() != quantity) {
                        product.setQuantity(quantity);
                        updated = true;
                    }

                    if (!Objects.equals(product.getQuality(), quality)) {
                        product.setQuality(quality);
                        updated = true;
                    }

                    if (Double.compare(product.getInitialPrice(), initialPrice) != 0) {
                        product.setInitialPrice(initialPrice);
                        updated = true;
                    }

                    if (Double.compare(product.getFinalPrice(), finalPrice) != 0) {
                        product.setFinalPrice(finalPrice);
                        updated = true;
                    }

                    if (!Objects.equals(product.getDescription(), description)) {
                        product.setDescription(description);
                        updated = true;
                    }

                    if (!Objects.equals(product.getUnit(), productUnit)) {
                        product.setUnit(productUnit);
                        updated = true;
                    }

                    if (!Objects.equals(product.getCode(), productCode)) {
                        product.setCode(productCode);
                        updated = true;
                    }

                    if (!Objects.equals(product.getProductImage(), productImagePath)) {
                        product.setProductImage(productImagePath);
                        updated = true;
                    }

                    if (!Objects.equals(product.getCompanyImage(), companyImagePath)) {
                        product.setCompanyImage(companyImagePath);
                        updated = true;
                    }

                    if (!Objects.equals(product.getAvailability(), Availability.DISPONIBIL)) {
                        product.setAvailability(Availability.DISPONIBIL);
                        updated = true;
                    }

                    if (!Objects.equals(product.getRecommendationStatus(), RecommendationStatus.PREFERAT)) {
                        product.setRecommendationStatus(RecommendationStatus.PREFERAT);
                        updated = true;
                    }

                    if (product.getDatePosted() == null) {
                        product.setDatePosted(LocalDateTime.now());
                        updated = true;
                    }

                    if (updated && productCode != null && !productCode.isEmpty()) {
                        productService.saveProduct(product);
                    }
                }
            }

            if (!remainingCodes.isEmpty()) {
                productService.deleteByCodes(remainingCodes);
            }
        } catch (IOException e) {
            throw new RuntimeException("Nu s-a putut descărca sau citi fișierul Excel");
        } finally {
            if (targetLocation != null && Files.exists(targetLocation)) {
                try {
                    Files.delete(targetLocation);
                } catch (IOException ex) {
                    System.err.println("Nu s-a putut șterge acest fișier");
                }
            }

            try {
                if (Files.exists(uploadPath) && Files.list(uploadPath).count() == 0) {
                    Files.delete(uploadPath);
                }
            } catch (IOException ex) {
                System.err.println("Nu s-a putut șterge acest dosar");
            }
        }
    }

    private String getCellAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue()).trim();
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();

            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue()).trim();
                }

            default:
                return "";
        }
    }
}