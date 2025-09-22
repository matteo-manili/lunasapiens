package com.lunasapiens.manualjobs;

import com.lunasapiens.SeoUtils;
import com.lunasapiens.entity.ArticleContent;
import com.lunasapiens.repository.ArticleContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class SeoUtilsManualJobs {


    @Autowired
    private ArticleContentRepository articleContentRepository;

    @Test
    public void testSeoGeneration() {
        List<ArticleContent> articles = articleContentRepository.findAll();

        for (ArticleContent article : articles) {
            String content = article.getContent();
            String title = SeoUtils.generateTitle(content, article.getId());
            String description = SeoUtils.generateDescription(content, article.getId());
            String slug = SeoUtils.generateSlug(content, article.getId());

            System.out.println("=== ARTICOLO ID: " + article.getId() + " ===");
            System.out.println("Title      : " + title);
            System.out.println("Description: " + description);
            System.out.println("Slug       : " + slug);
            System.out.println("URL        : https://www.lunasapiens.com/blog/" + slug);
            System.out.println();
        }

    }


}

