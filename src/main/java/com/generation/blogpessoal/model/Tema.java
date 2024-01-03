package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity // Define que a classe é uma entidade
@Table(name = "tb_temas") // Dá nome a tabela
public class Tema {
	
	@Id // Chave primária
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_Increment
	private long id;
	
	@NotNull(message = "O atributo descrição é obrigatório!") // Indica que a descrição não pode ser nula
	private String descricao;
	
	// Fetch define a estratégia de busca e carregamento dos dados das entidades relacionadas durante uma busca, a busca e carregamento de dados pode ser classificadas em dois tipos, Eager(Ansiosa) e Lazy(Preguiçosa)
	// FetchType.LAZY Não carregara os dados do tema associado a cada postagem até que os dados sejam solicitados
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tema", cascade = CascadeType.REMOVE) // Indica que o relacionamento é 1...n e o Cascade serve para quando executarmos alguma ação na entidade de destino (tema) a mesma ação será aplicada a entidade associada (postagem)
	@JsonIgnoreProperties // Para evitar looping em nossa aplicação utilizamos essa anotação para exibir o objeto da classe postagem apenas uma vez, interrompendo a repetição
	private List<Postagem> postagem;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Postagem> getPostagem() {
		return postagem;
	}

	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	
	

}
