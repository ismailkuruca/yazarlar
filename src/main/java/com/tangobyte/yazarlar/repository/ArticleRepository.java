package com.tangobyte.yazarlar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;

public interface ArticleRepository extends JpaRepository<Article, Long>{
	
	@Query("SELECT m FROM Article m WHERE m.author = :id")
	public List<Article> getAllArticlesByAuthorId(@Param("id") Author id);
	
	@Query("SELECT m FROM Article m WHERE m.author = :author AND m.title = :title")
	public List<Article> sameAuthorAndTitleExists(@Param("title") String title, @Param("author") Author author);

}
