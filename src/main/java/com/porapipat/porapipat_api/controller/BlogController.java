package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.blogs.request.*;
import com.porapipat.porapipat_api.dto.blogs.response.BlogResponse;
import com.porapipat.porapipat_api.dto.blogs.response.BlogSearchByIdResponse;
import com.porapipat.porapipat_api.dto.blogs.response.BlogSearchResponse;
import com.porapipat.porapipat_api.dto.errorhandle.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.userdetail.response.UserDetailOperationResponse;
import com.porapipat.porapipat_api.service.blogs.BlogService;
import com.porapipat.porapipat_api.service.security.SecurityService;
import com.porapipat.porapipat_api.service.util.PatternLogControllerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;
    private final PatternLogControllerService patternLogControllerService;
    private final SecurityService securityService;

    public BlogController(BlogService blogService,
                          PatternLogControllerService patternLogControllerService,
                          SecurityService securityService) {
        this.blogService = blogService;
        this.patternLogControllerService = patternLogControllerService;
        this.securityService = securityService;
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'READ')")
    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getBlogsByAuthorId(@PathVariable String authorId,
                                                                 Pageable pageable,
                                                                 HttpServletRequest httpRequest,
                                                                 Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            Page<BlogResponse> blogs = blogService.getBlogsByAuthorId(authorId, pageable);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(new BlogSearchByIdResponse(blogs.getContent(), blogs.getTotalPages(), blogs.getTotalElements(), blogs.getNumber()));
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.badRequest().body(new UserDetailOperationResponse(ex.getMessage(), false));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserDetailOperationResponse("An unexpected error occurred", false));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'CREATE')")
    @PostMapping
    public ResponseEntity<?> createBlog(@Valid @RequestBody CreateBlogRequest request,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            BlogResponse createdBlog = blogService.createBlog(request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable @NotNull String id,
                                        @Valid @RequestBody UpdateBlogRequest request,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            Optional<BlogResponse> updatedBlog = blogService.updateBlog(id, request);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return updatedBlog.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable @NotNull String id,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            String delete = blogService.deleteBlog(id);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok().body(delete);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable @NotNull String id,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            Optional<BlogResponse> blog = blogService.getBlogById(id);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return blog.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'READ')")
    @GetMapping
    public ResponseEntity<?> searchBlogs(BlogSearchCriteria criteria,
                                         Pageable pageable,
                                         HttpServletRequest httpRequest,
                                         Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            BlogSearchResponse searchResponse = blogService.searchBlogs(criteria, pageable);
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return ResponseEntity.ok(searchResponse);
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

    @PreAuthorize("@securityService.hasAccess(#auth, 'blog_api', 'CREATE')")
    @PostMapping("/{blogId}/comments")
    public ResponseEntity<?> addComment(@PathVariable @NotNull String blogId,
                                        @Valid @RequestBody AddCommentRequest request,
                                        HttpServletRequest httpRequest,
                                        Authentication auth) {
        try {
            patternLogControllerService.logInfo(httpRequest, auth, true);
            Optional<CommentDTO> addedComment = blogService.addComment(blogId, request, auth.getName());
            patternLogControllerService.logInfo(httpRequest, auth, false);
            return addedComment.map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }

}
