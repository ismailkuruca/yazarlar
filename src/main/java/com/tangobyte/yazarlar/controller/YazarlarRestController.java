package com.tangobyte.yazarlar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.model.Article;
import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.service.NewspaperService;
import com.tangobyte.yazarlar.service.ArticleService;
import com.tangobyte.yazarlar.service.AuthorService;


@Controller
public class YazarlarRestController {
	
	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private NewspaperService newspaperService;
	
	@Autowired
	private ArticleService articleService;
	
	
	@RequestMapping(value = "/getNewspapers", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public @ResponseBody List<Newspaper> getNewspapers() {
		List<Newspaper> allNewspapers = newspaperService.getAllNewspapers();
		return allNewspapers;
	}
	
	@RequestMapping(value = "/getAuthorsByNewspaperId", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public @ResponseBody List<Author> getAuthorsByNewspaperId(@RequestParam("id") Long id) {
		List<Author> allAuthorsByNewspaperId = authorService.getAllAuthorsByNewspaperId(id);
		return allAuthorsByNewspaperId;
	}
	
	@RequestMapping(value = "/getArticlesByAuthorId", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public @ResponseBody List<Article> getArticlesByAuthorId(@RequestParam("id") Long id) {
		List<Article> allArticlesByAuthorId = articleService.getAllArticlesByAuthorId(id);
		return allArticlesByAuthorId;
	}
	
}
