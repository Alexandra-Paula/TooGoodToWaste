package org.application.waste.service;

import jakarta.validation.Valid;
import org.application.waste.dto.BlogDto;
import org.application.waste.entity.Blog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {

    Blog findById(Long id);

    List<BlogDto> findAllBlogs();

    void saveBlog(@Valid BlogDto blogDto, String imageFile);

    void deleteBlog(Long id);
}
