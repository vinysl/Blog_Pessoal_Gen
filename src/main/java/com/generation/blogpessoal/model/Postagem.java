package com.generation.blogpessoal.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity // Define que essa entidade será uma tabela
@Table(name = "tb_postagens") // Dá um nome a tabela
public class Postagem {

	 @Id // Primary Key
		@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_Increment
		private Long id;
		
		@NotBlank(message = "O atributo título é Obrigatório!") // Proibe a ausência de qualquer coisa e também campos somente com espaços em branco
		@Size(min = 5, max = 100, message = "O atributo título deve conter no mínimo 05 e no máximo 100 caracteres") // Tamanho mínimo e máximo de caractéres
		@Column(length = 100) // Sobrescrevendo a quantidade padrão de caractéres máximos
		private String titulo;
		
		@NotBlank(message = "O atributo texto é Obrigatório!")
		@Size(min = 10, max = 1000, message = "O atributo texto deve conter no mínimo 10 e no máximo 1000 caracteres")
		@Column(length = 1000)
		private String texto;
		
		@UpdateTimestamp // A data automaticamente é cadastrada e atualizada pelo BD
		private LocalDateTime data;
		
		@ManyToOne // Anotação de n...1 onde varias postagens podem ter apenas um único tema
		@JsonIgnoreProperties("postagem") // Evita que gere um loop na aplicação
		private Tema tema;
		
		@ManyToOne
		@JsonIgnoreProperties("postagem")
		private Usuario usuario;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}

		public String getTexto() {
			return texto;
		}

		public void setTexto(String texto) {
			this.texto = texto;
		}

		public LocalDateTime getData() {
			return data;
		}

		public void setData(LocalDateTime data) {
			this.data = data;
		}

		public Tema getTema() {
			return tema;
		}

		public void setTema(Tema tema) {
			this.tema = tema;
		}

		public Usuario getUsuario() {
			return usuario;
		}

		public void setUsuario(Usuario usuario) {
			this.usuario = usuario;
		}	
		
}