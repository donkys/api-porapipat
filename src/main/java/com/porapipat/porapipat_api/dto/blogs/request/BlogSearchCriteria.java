package com.porapipat.porapipat_api.dto.blogs.request;

import lombok.Data;

import java.util.List;

@Data
public class BlogSearchCriteria {
    private String title;
    private String authorId;
    private List<String> tags;
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}
