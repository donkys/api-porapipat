package com.porapipat.porapipat_api.service.blogs;

import com.porapipat.porapipat_api.dto.blogs.request.*;
import com.porapipat.porapipat_api.dto.blogs.response.BlogResponse;
import com.porapipat.porapipat_api.dto.blogs.response.BlogSearchResponse;
import com.porapipat.porapipat_api.entity.BlogEntity;
import com.porapipat.porapipat_api.repository.BlogRepository;
import com.porapipat.porapipat_api.service.aws.S3Service;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class BlogService {
    private final BlogRepository blogRepository;
    private final S3Service s3Service;

    public BlogService(BlogRepository blogRepository, S3Service s3Service) {
        this.blogRepository = blogRepository;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    public Page<BlogResponse> getBlogsByAuthorId(String authorId, Pageable pageable) {
        log.info("Fetching blogs for authorId: {}", authorId);

        if (authorId == null || authorId.trim().isEmpty()) {
            log.warn("Invalid authorId provided: {}", authorId);
            throw new IllegalArgumentException("Author ID must not be null or empty");
        }

        if (pageable == null) {
            log.warn("Pageable is null, using default pagination settings");
            pageable = Pageable.unpaged();
        }

        try {
            Page<BlogEntity> blogPage = blogRepository.findByAuthorId(authorId, pageable);
            log.info("Found {} blogs for authorId: {}", blogPage.getTotalElements(), authorId);

            return blogPage.map(this::mapToResponse);
        } catch (Exception e) {
            log.error("Error fetching blogs for authorId: {}", authorId, e);
            throw e;
        }
    }

    @Transactional
    public BlogResponse createBlog(CreateBlogRequest request, String authorId) {
        String fileName = generateFileName(request.getTitle());
        String mdName = s3Service.uploadMarkdownFile(fileName, request.getMarkdownContent());
        log.info("Uploaded Markdown file name: {}", mdName);

        BlogEntity blog = new BlogEntity();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setMarkdownFile(fileName);
        blog.setAuthorId(authorId);
        blog.setTags(request.getTags());
        blog.setCreatedAt(Instant.now());
        blog.setUpdatedAt(Instant.now());
        blog.setComments(new ArrayList<>());

        BlogEntity savedBlog = blogRepository.save(blog);
        return mapToResponse(savedBlog);
    }

    @Transactional
    public Optional<BlogResponse> updateBlog(String id, UpdateBlogRequest request) {
        Optional<BlogEntity> blogOptional = blogRepository.findById(id);
        if (blogOptional.isPresent()) {
            BlogEntity blog = blogOptional.get();

            if (request.getMarkdownContent() != null) {
                String fileName = generateFileName(blog.getTitle());
                String mdName = s3Service.uploadMarkdownFile(fileName, request.getMarkdownContent());
                log.info("Uploaded Markdown file name: {}", mdName);
                blog.setMarkdownFile(fileName);
            }

            blog.setTitle(request.getTitle() != null ? request.getTitle() : blog.getTitle());
            blog.setContent(request.getContent() != null ? request.getContent() : blog.getContent());
            blog.setTags(request.getTags() != null ? request.getTags() : blog.getTags());
            blog.setUpdatedAt(Instant.now());

            BlogEntity updatedBlog = blogRepository.save(blog);
            return Optional.of(mapToResponse(updatedBlog));
        }
        return Optional.empty();
    }

    public String deleteBlog(String id) {
        blogRepository.deleteById(id);
        return "Deleted id: " + id;
    }

    public Optional<BlogResponse> getBlogById(String id) {
        return blogRepository.findById(id).map(this::mapToResponse);
    }

    public BlogSearchResponse searchBlogs(BlogSearchCriteria criteria, Pageable pageable) {
        Page<BlogEntity> blogPage;

        if (criteria.getTitle() != null && !criteria.getTitle().isEmpty()) {
            blogPage = blogRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(criteria.getTitle(), criteria.getTitle(), pageable);
        } else {
            blogPage = blogRepository.findAll(pageable);
        }

        List<BlogResponse> blogResponses = blogPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new BlogSearchResponse(
                blogResponses,
                blogPage.getTotalPages(),
                blogPage.getTotalElements(),
                blogPage.getNumber()
        );
    }

    public Optional<CommentDTO> addComment(String blogId, AddCommentRequest request, String nameUser) {
        Optional<BlogEntity> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent()) {
            BlogEntity blog = blogOptional.get();

            BlogEntity.Comment comment = new BlogEntity.Comment();
            comment.setUserId(nameUser);
            comment.setComment(request.getComment());
            comment.setCreatedAt(Instant.now());

            blog.getComments().add(comment);
            blogRepository.save(blog);

            return Optional.of(mapToCommentDTO(comment));
        }
        return Optional.empty();
    }

    private String generateFileName(String title) {
        return title.toLowerCase().replaceAll("\\s+", "-") + "-" + System.currentTimeMillis() + ".md";
    }

    private BlogResponse mapToResponse(BlogEntity blog) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setContent(blog.getContent());
        response.setMarkdownFile(s3Service.getMarkdownPresignedUrl(blog.getMarkdownFile()));
        response.setAuthorId(blog.getAuthorId());
        response.setCreatedAt(blog.getCreatedAt());
        response.setUpdatedAt(blog.getUpdatedAt());
        response.setTags(blog.getTags());
        List<CommentDTO> commentDTOs = (blog.getComments() != null ?
                blog.getComments().stream().map(this::mapToCommentDTO).collect(Collectors.toList()) :
                new ArrayList<>());

        response.setComments(commentDTOs);
        return response;
    }

    private BlogDTO mapToDTO(BlogEntity blog) {
        BlogDTO dto = new BlogDTO();
        dto.setId(blog.getId());
        dto.setTitle(blog.getTitle());
        dto.setContent(blog.getContent());
        dto.setMarkdownFile(s3Service.getMarkdownPresignedUrl(blog.getMarkdownFile())); // Generate pre-signed URL
        dto.setAuthorId(blog.getAuthorId());
        dto.setCreatedAt(blog.getCreatedAt());
        dto.setUpdatedAt(blog.getUpdatedAt());
        dto.setTags(blog.getTags());
        dto.setComments(blog.getComments().stream().map(this::mapToCommentDTO).collect(Collectors.toList()));
        return dto;
    }

    private CommentDTO mapToCommentDTO(BlogEntity.Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setUserId(comment.getUserId());
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}