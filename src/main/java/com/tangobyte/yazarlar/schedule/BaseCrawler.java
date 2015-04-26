package com.tangobyte.yazarlar.schedule;

import org.springframework.beans.factory.annotation.Autowired;

import com.tangobyte.yazarlar.service.NewspaperService;
import com.tangobyte.yazarlar.service.ArticleService;
import com.tangobyte.yazarlar.service.AuthorService;

public class BaseCrawler {
    
    public static final int SCHEDULER_DELAY = 30 * 60 * 1000;
	
	@Autowired
	NewspaperService newspaperService;
	
	@Autowired
	AuthorService authorService;
	
	@Autowired
	ArticleService articleService;
	
}
