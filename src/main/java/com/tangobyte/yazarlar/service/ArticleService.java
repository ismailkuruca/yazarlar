package com.tangobyte.yazarlar.service;

import java.util.List;

import com.tangobyte.yazarlar.model.Article;

public interface ArticleService {

	public Article getArticleById(Long id);

	public List<Article> getAllArticles();

	public Article saveOrUpdateArticle(Article author);

	public void deleteArticle(Article author);
	
	public List<Article> getAllArticlesByAuthorId(Long id);
}
