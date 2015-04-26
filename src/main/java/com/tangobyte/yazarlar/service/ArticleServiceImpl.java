package com.tangobyte.yazarlar.service;

import java.util.List;

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
	private AuthorRepository authorRepository;
	
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
	        return articleRepository.saveAndFlush(article);
	    }
	    return null;
	}

	@Override
	public void deleteArticle(Article article) {
		articleRepository.delete(article);
	}

	@Override
	public List<Article> getAllArticlesByAuthorId(Long id) {
	    Author findOne = authorRepository.findOne(id);
		return articleRepository.getAllArticlesByAuthorId(findOne);
	}

}
