package com.tangobyte.yazarlar.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
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
	
	private static ConcurrentHashMap<Long, ConcurrentHashMap<Long, Article>> cache = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, Article>>();
	
    @PostConstruct
    void init() {
        List<Author> allAuthors = authorService.getAllAuthors();
        for(Author a : allAuthors) {
            List<Article> allArticlesByAuthorId = articleRepository.getAllArticlesByAuthorId(a);
            ConcurrentHashMap<Long, Article> map = new ConcurrentHashMap<Long, Article>();
            for(Article art : allArticlesByAuthorId) {
                map.put(art.getId(), art);
            }
            cache.put(a.getId(), map);
        }
    }
    
    @Scheduled(initialDelay = 5 * 60 * 1000, fixedDelay = 30 * 60 * 1000)
    @Async
    void updateViewCount() {
        Set<Long> keySet = cache.keySet();
        for(Long i : keySet) {
            articleRepository.save(CollectionUtils.arrayToList(cache.get(i).values().toArray()));
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
	        article.setViewCount(Long.valueOf(0));
	        Article saveAndFlush = articleRepository.saveAndFlush(article);
	        if(saveAndFlush != null) {
	            if(!cache.contains(saveAndFlush.getAuthor().getId())) {
	                cache.put(saveAndFlush.getAuthor().getId(), new ConcurrentHashMap<Long, Article>());
	            }
	            cache.get(saveAndFlush.getAuthor().getId()).put(saveAndFlush.getId(), saveAndFlush);
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
	        List<Article> allArticlesByAuthorId = articleRepository.getAllArticlesByAuthorId(authorById);
	        ConcurrentHashMap<Long, Article> map = new ConcurrentHashMap<Long, Article>();
	        for(Article a : allArticlesByAuthorId) {
	            map.put(a.getId(), a);
	        }
	        cache.put(id, map);
	    }
	    
	    return (List<Article>) CollectionUtils.arrayToList(cache.get(id).values().toArray());
	}

    @Override
    public void increaseViewCount(Long aid, Long id) {
        cache.get(aid).get(id).increaseViewCount();
    }

    @Override
    public List<Article> getMostPopularArticles() {
        PageRequest pr = new PageRequest(0, 10, Direction.DESC, "viewCount");
        Page<Article> findAll = articleRepository.findAll(pr);
        List<Article> resultSet = new ArrayList<Article>();
        if(findAll != null) {
            resultSet.addAll(findAll.getContent());
        }
        return resultSet;
    }

    @Override
    public List<Article> getMostRecentArticles() {
        PageRequest pr = new PageRequest(0, 10, Direction.DESC, "publishDate");
        Page<Article> findAll = articleRepository.findAll(pr);
        List<Article> resultSet = new ArrayList<Article>();
        if(findAll != null) {
            resultSet.addAll(findAll.getContent());
        }
        return resultSet;
    }


}
