package com.lunasapiens.manualjobs.ArticleSEO;

import com.lunasapiens.utils.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.aiModels.huggngface.HuggingfaceLLaMAGenerateSEOTextArticleService;
import com.lunasapiens.aiModels.huggngface.HuggingfaceTextEmbedding_E5LargeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class UtilsArticleSeoManualJobs {


    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Autowired
    HuggingfaceTextEmbedding_E5LargeService textEmbeddingHuggingfaceService;

    @Autowired
    private ArticleContentCustomRepositoryImpl articleContentCustomRepository;

    @Autowired
    private HuggingfaceLLaMAGenerateSEOTextArticleService huggingfaceLLaMAGenerateSEOTextArticleService;




    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void testSeoGeneration() {
        List<ArticleContent> articles = articleContentRepository.findAll();

        int count = 0;

        for (ArticleContent article : articles) {
            if (count >= 3) break; // ferma dopo i primi 3 articoli
            String content = article.getContent();
            String title = huggingfaceLLaMAGenerateSEOTextArticleService.generateTitle(content, article.getId());

            System.out.println("=== ARTICOLO ID: " + article.getId() + " ===");
            System.out.println("Title      : " + title);
            System.out.println("Description: " + "");
            System.out.println("Slug       : " + "");
            System.out.println("URL        : https://www.lunasapiens.com/blog/" + "");
            System.out.println();

            count++;
        }

    }










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

}

