 package com.tangobyte.yazarlar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tangobyte.yazarlar.model.Author;
import com.tangobyte.yazarlar.model.Newspaper;

public interface AuthorRepository extends JpaRepository<Author, Long>{
	
	@Query("SELECT y FROM Author y WHERE y.newspaper = :newspaper")
	public List<Author> getAllAuthorsByNewspaperId(@Param("newspaper") Newspaper newspaper);

	@Query("SELECT y FROM Author y WHERE y.name = :name")
	public Author getAuthorByName(@Param("name") String name);

}
