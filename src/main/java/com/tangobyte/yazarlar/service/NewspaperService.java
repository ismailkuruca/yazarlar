package com.tangobyte.yazarlar.service;

import java.util.List;

import com.tangobyte.yazarlar.model.Newspaper;

public interface NewspaperService {
	
	public Newspaper getNewspaperById(Long id);
	
	public Newspaper getNewspaperByTitle(String title);

	public List<Newspaper> getAllNewspapers();

	public Newspaper saveOrUpdateNewspaper(Newspaper newspaper);

	public void deleteNewspaper(Newspaper newspaper);

}
