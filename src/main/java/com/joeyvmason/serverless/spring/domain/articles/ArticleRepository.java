package com.joeyvmason.serverless.spring.domain.articles;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ArticleRepository {

    private Map<String, Article> articles = new HashMap<>();

    public Article save(Article article) {
        if (article.getId() == null) {
            article.setId(UUID.randomUUID().toString());
        }

        articles.put(article.getId(), article);
        return article;
    }

    public Collection<Article> findAll() {
    	List<Article> list = new ArrayList<Article>();
    	list.add(new Article("Madhu","This is my new book"));
    	list.add(new Article("Madhavi","This is my madhavi book"));
        return list;
    }

    public Article findOne(String id) {
    	return new Article("Madhavi","This is my madhavi book");
        //return articles.get(id);
    }

    public void deleteAll() {
        articles.clear();
    }

}
