package com.tangobyte.yazarlar.service;

import java.util.List;

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
	private NewspaperRepository newspaperRepository;

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
		return authorRepository.saveAndFlush(author);
	}

	@Override
	public void deleteAuthor(Author author) {
		authorRepository.delete(author);
	}

	@Override
	public List<Author> getAllAuthorsByNewspaperId(Long id) {
	    Newspaper findOne = newspaperRepository.findOne(id);
		return authorRepository.getAllAuthorsByNewspaperId(findOne);
	}

	@Override
	public Author getAuthorByName(String name) {
		return authorRepository.getAuthorByName(name);
	}
	
	

}
