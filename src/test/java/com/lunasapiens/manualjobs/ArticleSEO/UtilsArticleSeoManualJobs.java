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


    /**
     * 1- Il Titolo
     * Deve essere unico, descrittivo e contenere la parola chiave principale.
     * Un solo H1 e nel Tag <title> contenente il titolo.
     * Lunghezza ideale: 50–60 caratteri.
     *
     *
     * 2- Meta description
     * <meta name="description" content="Descrizione chiara dell’articolo con parole chiave e invito al clic.">
     * Dovrebbe invogliare l’utente al click.
     * Lunghezza ideale: 140–160 caratteri.
     *
     *
     * 3- URL parlante (SEO-friendly)
     * Es.:
     * /come-ottimizzare-pagina-html-seo
     * Evita caratteri strani o numeri inutili.
     * Ideale: 50–60 caratteri max 100
     *
     */



    @Test
    //@Disabled("Disabilitato temporaneamente per debug")
    public void testSeoGeneration() {
        List<ArticleContent> articles = articleContentRepository.findAll();

        int count = 0;

        for (ArticleContent article : articles) {
            if (count >= 2) break; // ferma dopo i primi 3 articoli
            String content = article.getContent();
            System.out.println("=== ARTICOLO ID: " + article.getId() + " ===");
            System.out.println("content: " + UtilsArticleSeo.cleanText(content) );


            /**
             * 1- Il Titolo
             * Deve essere unico, descrittivo e contenere la parola chiave principale.
             * Un solo H1 e nel Tag <title> contenente il titolo.
             * Lunghezza ideale: 50–60 caratteri.
             */
            if (article.getTitle() == null || article.getTitle().isEmpty()) {
                String title = huggingfaceLLaMAGenerateSEOTextArticleService.generateTitle(content, article.getId());
                article.setTitle( UtilsArticleSeo.cleanGeneratedText(title) );
                try {
                    articleContentRepository.save(article);
                } catch (DataIntegrityViolationException e) {
                    article.setTitle(title + " " + article.getId());
                    articleContentRepository.save(article);
                }
                System.out.println("Title      : " + article.getTitle() );

            }


            /**
             * 2- Meta description
             * <meta name="description" content="Descrizione chiara dell’articolo con parole chiave e invito al clic.">
             * Dovrebbe invogliare l’utente al click.
             * Lunghezza ideale: 140–160 caratteri.
             */
            if (article.getMetaDescription() == null || article.getMetaDescription().isEmpty()) {
                String metaDescription = huggingfaceLLaMAGenerateSEOTextArticleService.generateMetaDescription(content, article.getId());
                article.setMetaDescription( UtilsArticleSeo.cleanGeneratedText(metaDescription) );
                articleContentRepository.save(article);
                System.out.println("MetaDescription: " + article.getMetaDescription());
            }








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

