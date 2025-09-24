package com.lunasapiens.repository;


public interface ArticleContentCustomRepository {

    // lo uso perche a volte faccio il reset del database e la sequence riparte da zero
    void updateSequence();


}
