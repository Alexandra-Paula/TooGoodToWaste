package org.application.waste.controller;

import jakarta.validation.Valid;
import org.application.waste.dto.BlogDto;
import org.application.waste.service.BlogService;
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
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping("/blog")
    public String showCreateBlogForm(Model model) {
        model.addAttribute("blogDto", new BlogDto());
        return "createBlog";
    }

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

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

        String uploadDir = "src/main/resources/static/images/uploads/";

        Path path = Paths.get(uploadDir, fileName);

        Files.createDirectories(path.getParent());

        Files.write(path, imageFile.getBytes());

        String relativePath = "/images/uploads/" + fileName;

        blogService.saveBlog(blogDto, relativePath);

        return "redirect:/blog?success";
    }

    @GetMapping("/blog/list")
    public String listBlogs(Model model) {
        model.addAttribute("blogs", blogService.findAllBlogs());
        model.addAttribute("page", "blogs");
        return "blog-list";
    }

    @GetMapping("/blog/{id}")
    public String viewBlog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.findById(id));
        return "single-blog";
    }

    @GetMapping("/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return "redirect:/blog?deleted";
    }
}