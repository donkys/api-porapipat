package com.porapipat.porapipat_api.dto.blogs.response;

import com.porapipat.porapipat_api.dto.blogs.request.BlogDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BlogSearchByIdResponse {
    private List<BlogResponse> blogs;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}

