package com.tangobyte.yazarlar.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
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

    @RequestMapping(value = "/getNewspapers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Newspaper> getNewspapers() {
        List<Newspaper> allNewspapers = newspaperService.getAllNewspapers();
        return allNewspapers;
    }

    @RequestMapping(value = "/getAuthorsByNewspaperId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Author> getAuthorsByNewspaperId(@RequestParam("id") Long id) {
        List<Author> allAuthorsByNewspaperId = authorService.getAllAuthorsByNewspaperId(id);
        return allAuthorsByNewspaperId;
    }

    @RequestMapping(value = "/getArticlesByAuthorId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Article> getArticlesByAuthorId(@RequestParam("id") Long id) {
        List<Article> allArticlesByAuthorId = articleService.getAllArticlesByAuthorId(id);
        Collections.sort(allArticlesByAuthorId, new Comparator<Article>() {

            @Override
            public int compare(Article o1, Article o2) {
                return o2.getPublishDate().compareTo(o1.getPublishDate());
            }
        });
        return allArticlesByAuthorId;
    }

    @RequestMapping(value = "/getArticleById", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void getArticleById(@RequestParam("aid") Long aid, @RequestParam("id") Long id) {
        articleService.increaseViewCount(aid, id);
    }
    
    @RequestMapping(value = "/getMostPopular", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Article> getMostPopular() {
        List<Article> mostPopular = articleService.getMostPopularArticles();
        return mostPopular;
    }
    
    @RequestMapping(value = "/getMostRecent", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Article> getMostRecent() {
        List<Article> mostRecent = articleService.getMostRecentArticles();
        return mostRecent;
    }

}
