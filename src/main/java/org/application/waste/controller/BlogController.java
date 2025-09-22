package org.application.waste.controller;

import jakarta.validation.Valid;
import org.application.waste.dto.BlogDto;
import org.application.waste.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class BlogController {

    @Autowired
    private BlogService blogService;

    // Afișează formularul pentru un blog nou
    @GetMapping("/blog")
    public String showCreateBlogForm(Model model) {
        model.addAttribute("blogDto", new BlogDto());
        return "createBlog";
    }

    // Salvează un blog nou
    @PostMapping("/blog/save")
    public String saveBlog(
            @Valid @ModelAttribute("blogDto") BlogDto blogDto,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("blogDto", blogDto);
            return "createBlog";
        }

        // Generezi un nume unic pentru fișier
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        // Stabilești directorul unde salvezi
        String uploadDir = "uploads/";

        // Creezi path-ul complet
        Path path = Paths.get(uploadDir + fileName);

        // Salvezi pe disc
        Files.createDirectories(path.getParent());
        Files.write(path, imageFile.getBytes());

        // Calea relativă pe care o pui în entitate
        String relativePath = "src/main/resources/static/images/uploads/" + fileName;

        blogService.saveBlog(blogDto, relativePath);

        return "redirect:/blog?success";
    }

    /*
    // Listă bloguri
    @GetMapping("/blog/list")
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.findAllBlogs());
        return "blog-list";
    }

    // Afișează un blog după ID
    @GetMapping("/blog/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.findById(id));
        return "viewBlog";
    }

    // Șterge un blog
    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/blog?deleted";
    }
    */
}
