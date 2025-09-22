package org.application.waste.service;

import org.application.waste.dto.BlogDto;
import org.application.waste.entity.Blog;
import org.application.waste.repository.BlogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public Blog findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog-ul cu ID-ul " + id + " nu a fost găsit"));
    }

    @Override
    public List<Blog> findAllBlogs() {
        return blogRepository.findAll();
    }

    @Override
    public void saveBlog(BlogDto blogDto, String imageFile) {
        Blog blog = new Blog();
        blog.setTitle(blogDto.getTitle());
        blog.setContent(blogDto.getContent());
        blog.setImageUrl(imageFile);
        blog.setCreatedAt(LocalDateTime.now());


        blogRepository.save(blog);
    }

    @Override
    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new IllegalArgumentException("Blog-ul cu ID-ul " + id + " nu există");
        }
        blogRepository.deleteById(id);
    }
}
