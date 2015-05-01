package com.tangobyte.yazarlar.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.tangobyte.yazarlar.model.Newspaper;
import com.tangobyte.yazarlar.repository.NewspaperRepository;


@Service
public class NewspaperServiceImpl implements NewspaperService{
	
	@Resource
	private NewspaperRepository newspaperRepository;
	
	private static List<Newspaper> cache = new ArrayList<Newspaper>();

	@PostConstruct
	void init() {
	    cache = newspaperRepository.findAll();;
	}
	
	@Override
	public Newspaper getNewspaperById(Long id) {
		return newspaperRepository.findOne(id);
	}

	@Override
	public List<Newspaper> getAllNewspapers() {
		return cache;
	}

	@Override
	public Newspaper saveOrUpdateNewspaper(Newspaper newspaper) {
		return newspaperRepository.saveAndFlush(newspaper);
	}

	@Override
	public void deleteNewspaper(Newspaper newspaper) {
		newspaperRepository.delete(newspaper);
	}

	@Override
	public Newspaper getNewspaperByTitle(String title) {
		return newspaperRepository.getNewspaperByTitle(title);
	}
	

}
