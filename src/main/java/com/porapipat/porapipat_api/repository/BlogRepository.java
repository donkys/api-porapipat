package com.porapipat.porapipat_api.repository;

import com.porapipat.porapipat_api.entity.BlogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogRepository extends MongoRepository<BlogEntity, String> {
    Page<BlogEntity> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
    Page<BlogEntity> findByAuthorId(String authorId, Pageable pageable);
}
