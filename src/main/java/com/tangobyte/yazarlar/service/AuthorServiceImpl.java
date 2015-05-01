package com.tangobyte.yazarlar.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.repository.AuthorRepository;
import com.tangobyte.yazarlar.repository.NewspaperRepository;

@Service
public class AuthorServiceImpl implements AuthorService {
	
	@Resource
	private AuthorRepository authorRepository;
	
	@Resource
	private NewspaperService newspaperService;
	
	private static ConcurrentHashMap<Long, List<Author>> cache = new ConcurrentHashMap<Long, List<Author>>();
	
	@PostConstruct
	void init() {
	    List<Newspaper> allNewspapers = newspaperService.getAllNewspapers();
	    for(Newspaper n : allNewspapers) {
	        cache.put(n.getId(), authorRepository.getAllAuthorsByNewspaperId(n));
	    }
	}

	@Override
	public Author getAuthorById(Long id) {
		return authorRepository.findOne(id);
	}

	@Override
	public List<Author> getAllAuthors() {
		return authorRepository.findAll();
	}

	@Override
	public Author saveOrUpdateAuthor(Author author) {
		Author saveAndFlush = authorRepository.saveAndFlush(author);
		if(saveAndFlush != null) {
		    cache.get(author.getNewspaper().getId()).add(saveAndFlush);
		}
		return saveAndFlush;
	}

	@Override
	public void deleteAuthor(Author author) {
		authorRepository.delete(author);
	}

	@Override
	public List<Author> getAllAuthorsByNewspaperId(Long id) {
	    return cache.get(id);
	}

	@Override
	public Author getAuthorByName(String name) {
		return authorRepository.getAuthorByName(name);
	}
	
	

}
