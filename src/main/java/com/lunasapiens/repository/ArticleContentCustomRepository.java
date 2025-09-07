package com.lunasapiens.repository;

import com.lunasapiens.entity.ArticleContent;
import org.springframework.data.domain.Page;

public interface ArticleContentCustomRepository {

    // lo uso perche a volte faccio il reset del database e la sequence riparte da zero
    void updateSequence();


}
