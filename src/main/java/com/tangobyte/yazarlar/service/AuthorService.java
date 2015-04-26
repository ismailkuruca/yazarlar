package com.tangobyte.yazarlar.service;

import java.util.List;

import com.tangobyte.yazarlar.model.Author;

public interface AuthorService {

	public Author getAuthorById(Long id);
	
	public Author getAuthorByName(String name);

	public List<Author> getAllAuthors();

	public Author saveOrUpdateAuthor(Author author);

	public void deleteAuthor(Author author);
	
	public List<Author> getAllAuthorsByNewspaperId(Long id);
}
