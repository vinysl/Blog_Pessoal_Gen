package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Postagem;

// JPA = Interface que contém os métodos de manipulação do BD
// PostagemRepository = Interface que herda os métodos da JPA
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
	
	// Permite criar consultas especificas com qualquer atributo da classe associada a interface Repository
	public List<Postagem> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);

}
