package com.lunasapiens.manualjobs.ArticleSEO;

import com.lunasapiens.utils.Utils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentCustomRepositoryImpl;
import com.lunasapiens.repository.ArticleContentRepository;
import com.lunasapiens.aiModels.huggngface.HuggingfaceLLaMAGenerateSEOTextArticleService;
import com.lunasapiens.aiModels.huggngface.HuggingfaceTextEmbedding_E5LargeService;
import com.lunasapiens.utils.UtilsArticleSeo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
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
    public void testTrimFields() {
        List<ArticleContent> articles = articleContentRepository.findAll();
        List<Long> modifiedArticles = new ArrayList<>();

        for (ArticleContent article : articles) {
            boolean modified = false;

            // Controlla e pulisci title
            if (article.getTitle() != null) {
                String trimmedTitle = article.getTitle().trim();
                if (!trimmedTitle.equals(article.getTitle())) {
                    System.out.println("Articolo ID " + article.getId() + " aveva spazi nel title.");
                    article.setTitle(trimmedTitle);
                    modified = true;
                }
            }

            // Controlla e pulisci content
            if (article.getContent() != null) {
                String trimmedContent = article.getContent().trim();
                if (!trimmedContent.equals(article.getContent())) {
                    System.out.println("Articolo ID " + article.getId() + " aveva spazi nel content.");
                    article.setContent(trimmedContent);
                    modified = true;
                }
            }

            // Controlla e pulisci seoUrl
            if (article.getSeoUrl() != null) {
                String trimmedSeoUrl = article.getSeoUrl().trim();
                if (!trimmedSeoUrl.equals(article.getSeoUrl())) {
                    System.out.println("Articolo ID " + article.getId() + " aveva spazi nel seoUrl.");
                    article.setSeoUrl(trimmedSeoUrl);
                    modified = true;
                }
            }

            // Salva solo se ci sono modifiche
            if (modified) {
                articleContentRepository.save(article);
                modifiedArticles.add(article.getId());
            }
        }

        System.out.println("Totale articoli modificati: " + modifiedArticles.size());
        if (!modifiedArticles.isEmpty()) {
            System.out.println("ID articoli modificati: " + modifiedArticles);
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

