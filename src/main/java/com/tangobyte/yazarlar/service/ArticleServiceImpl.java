package com.tangobyte.yazarlar.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.repository.ArticleRepository;
import com.tangobyte.yazarlar.repository.AuthorRepository;

@Service
public class ArticleServiceImpl implements ArticleService{

	@Resource
	private ArticleRepository articleRepository;
	
	@Resource
	private AuthorService authorService;
	
	private static ConcurrentHashMap<Long, List<Article>> cache = new ConcurrentHashMap<Long, List<Article>>();
	
    @PostConstruct
    void init() {
        List<Author> allAuthors = authorService.getAllAuthors();
        for(Author a : allAuthors) {
            cache.put(a.getId(), articleRepository.getAllArticlesByAuthorId(a));
        }
    }
	
	@Override
	public Article getArticleById(Long id) {
		return articleRepository.findOne(id);
	}

	@Override
	public List<Article> getAllArticles() {
		return articleRepository.findAll();
	}

	@Override
	public Article saveOrUpdateArticle(Article article) {
	    List<Article> sameAuthorAndTitleExists = articleRepository.sameAuthorAndTitleExists(article.getTitle(), article.getAuthor());
	    if(sameAuthorAndTitleExists.size() == 0) {
	        article.setContent(article.getContent().replaceAll("&#39;", "'"));
	        article.setContent(article.getContent().replaceAll("&quot;", "\""));
	        article.setTitle(article.getTitle().replaceAll("&#39;", "'"));
	        article.setTitle(article.getTitle().replaceAll("&quot;", "\""));
	        Article saveAndFlush = articleRepository.saveAndFlush(article);
	        if(saveAndFlush != null) {
	            cache.get(saveAndFlush.getAuthor().getId()).add(saveAndFlush);
	            return saveAndFlush;
	        }
	    }
	    return null;
	}

	@Override
	public void deleteArticle(Article article) {
		articleRepository.delete(article);
	}

	@Override
	public List<Article> getAllArticlesByAuthorId(Long id) {
	    if(!cache.contains(id)) {
	        Author authorById = authorService.getAuthorById(id);
	        cache.put(id, articleRepository.getAllArticlesByAuthorId(authorById));
	    }
	    
	    return cache.get(id);
	}

}
