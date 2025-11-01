package com.lunasapiens.manualjobs.ArticleSEO;

import com.lunasapiens.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.service.HuggingfaceTextEmbedding_E5LargeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class ArticleSeoUtilsManualJobs {


    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    HuggingfaceTextEmbedding_E5LargeService textEmbeddingHuggingfaceService;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;



    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void aggiornaEmbedded() throws Exception {
        List<ArticleContent> articles = articleContentRepository.findAll();

        for (ArticleContent article : articles) {

            // aggiorno la colonna embedding
            //Float[] embedding = textEmbeddingService.computeCleanEmbedding( articleSave.getContent() );
            Float[] embedding = textEmbeddingHuggingfaceService.embedDocument( Utils.cleanHtmlText(article.getContent()) );

            System.out.println("Dimensione embedding: " + embedding.length);
            ArticleContent articleContentRefresh = articleContentCustomRepository.updateArticleEmbeddingJdbc(article.getId(), embedding);

            System.out.println("=== ARTICOLO ID: " + article.getId() + " AGGIORNATO");
            System.out.println();
        }

    }







    @Test
    @Disabled("Disabilitato temporaneamente per debug")
    public void testSeoGeneration() {
        List<ArticleContent> articles = articleContentRepository.findAll();

        for (ArticleContent article : articles) {
            String content = article.getContent();
            String title = ArticleSeoUtils.generateTitle(content, article.getId());
            String description = ArticleSeoUtils.generateDescription(content, article.getId());
            String slug = ArticleSeoUtils.generateSlug(content, article.getId());

            System.out.println("=== ARTICOLO ID: " + article.getId() + " ===");
            System.out.println("Title      : " + title);
            System.out.println("Description: " + description);
            System.out.println("Slug       : " + slug);
            System.out.println("URL        : https://www.lunasapiens.com/blog/" + slug);
            System.out.println();
        }

    }


}

