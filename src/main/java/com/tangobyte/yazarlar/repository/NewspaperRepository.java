package com.tangobyte.yazarlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tangobyte.yazarlar.model.Newspaper;

public interface NewspaperRepository extends JpaRepository<Newspaper, Long> {
	
	@Query("SELECT g FROM Newspaper g WHERE g.title = ?")
	public Newspaper getNewspaperByTitle(String title);

}
