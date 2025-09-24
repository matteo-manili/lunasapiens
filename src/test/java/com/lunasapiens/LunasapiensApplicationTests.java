package com.lunasapiens;

import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.service.ArticleSemanticService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;




@SpringBootTest
class LunasapiensApplicationTests {

    @Autowired
    private ArticleSemanticService articleSemanticService;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;


    @Test
    void contextLoads() {
    }




}
