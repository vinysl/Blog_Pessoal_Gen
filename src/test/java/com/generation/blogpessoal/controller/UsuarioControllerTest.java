package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // Indica que a classe é uma classe Spring Boot Testing, a Opção environment indica que caso a porta principal (8080 para uso local) esteja ocupada, o Spring irá atribuir uma outra porta automaticamente.
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Indica que o ciclo de vida da classe de teste será por classe
public class UsuarioControllerTest {

	@Autowired // Injeção/Inversão de dependencia
	private TestRestTemplate testRestTemplate; // Envia as requisições para nossa aplicação

	@Autowired
	private UsuarioService usuarioService; // Persiste os objetos no bd de testes com a senha criptografada

	@Autowired
	private UsuarioRepository usuarioRepository; // Limpa o banco de dados de testes

	@BeforeAll
	void start(){ // o Método start(), anotado com a anotação @BeforeAll, apaga todos os dados da tabela e cria o usuário root@root.com para testar os Métodos protegidos por autenticação.

		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, 
			"Root", "root@root.com", "rootroot", "-"));

	}
	
	@Test // Indica que este metodo executara um teste
	@DisplayName("Cadastrar Um Usuário") // Configura uma mensagem que será exibida ao invés do nome do método
	public void deveCriarUmUsuario() {

		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, // Equivalente ao que o Insomnia faz em uma requisição do tipo POST, 
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-")); // transforma os atributos num objeto da classe Usuario que será enviado no corpo da requisição(Request Body) 

		ResponseEntity<Usuario> corpoResposta = testRestTemplate // a Requisição HTTP será enviada através do Método exchange() da Classe TestRestTemplate e a Resposta da Requisição (Response) será recebida pelo objeto corpoResposta do tipo ResponseEntity
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class); // Para enviar a requisição, o será necessário passar 4 parâmetros: A URI: Endereço do endpoint (/usuarios/cadastrar) O Método HTTP: Neste exemplo o Método POST, O Objeto HttpEntity: Neste exemplo o objeto requisicao, que contém o objeto da Classe Usuario, O conteúdo esperado no Corpo da Resposta (Response Body): Neste exemplo será do tipo Usuario (Usuario.class).

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode()); // Checaremos se a resposta da requisição é a resposta esperada (201 -> CREATED), para obter o status da resposta vamos utilizar o método getStatusCode() da classe Response Entity
	
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com", "13465278", "-")); // Foi persistido um objeto da classe Usuario no bd (Maria da Silva)
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva", "maria_silva@email.com", "13465278", "-")); // Recebe um objeto da classe Usuario contendo os mesmos dados do objeto persistido na linha 65
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class); // a Requisição HTTP será enviada através do Método exchange() da Classe TestRestTemplate e a Resposta da Requisição (Response) será recebida pelo objeto corpoResposta do tipo ResponseEntity. Para enviar a requisição, o será necessário passar 4 parâmetros: A URI: Endereço do endpoint (/usuarios/cadastrar) O Método HTTP: Neste exemplo o Método POST, O Objeto HttpEntity: Neste exemplo o objeto requisicao, que contém o objeto da Classe Usuario, O conteúdo esperado no Corpo da Resposta (Response Body): Neste exemplo será do tipo Usuario (Usuario.class).
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode()); // Checaremos se a resposta da requisição é a resposta esperada (BAD_REQUEST 🡪 400), para obter o status da resposta vamos utilizar o Método getStatusCode() da Classe ResponseEntity.
	}
	
	@Test
	@DisplayName("Atualizar um usuário")
	public void deveAtualizarUmUsuario() {
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-")); // Armazena o resultado da persistência de um objeto da classe Usuario no bd através do método cadastrarUsuario() da classe UsuarioService
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrew Ramos", "juliana_ramos@email.com", "juliana123", "-"); // Utilizado para atualizar os dados persistidos no objeto usuarioCreate
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate); // O processo é equivalente ao que o Insomnia faz em uma requisição do tipo PUT, transforma os atributos num objeto da classe Usuario que será enviado no corpo da requisição
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot") // a Requisição HTTP será enviada através do Método exchange() da Classe TestRestTemplate e a Resposta da Requisição (Response) será recebida pelo objeto corpoResposta do tipo ResponseEntity
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class); // Para enviar a requisição, o será necessário passar 4 parâmetros: A URI: Endereço do endpoint (/usuarios/atualizar) O Método HTTP: Neste exemplo o Método PUT, O Objeto HttpEntity: Neste exemplo o objeto requisicao, que contém o objeto da Classe Usuario, O conteúdo esperado no Corpo da Resposta (Response Body): Neste exemplo será do tipo Usuario (Usuario.class).
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode()); // checaremos se a resposta da requisição (Response), é a resposta esperada (OK 🡪 200). Para obter o status da resposta vamos utilizar o Método getStatusCode() da Classe ResponseEntity
	}
	
	@Test
	@DisplayName("Listar todos os usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-")); // Foi persistido dois objetos da classe Usuario no bd  através do método cadastrarUsuario() da Classe UsuarioService
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot") // a Requisição HTTP será enviada através do Método exchange() da Classe TestRestTemplate e a Resposta da Requisição (Response) será recebida pelo objeto corpoResposta do tipo ResponseEntity
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class); // // Para enviar a requisição, o será necessário passar 4 parâmetros: A URI: Endereço do endpoint (/usuarios/all) O Método HTTP: Neste exemplo o Método GET, O Objeto HttpEntity: Neste exemplo o objeto será nulo, requisições do tipo GET não enviam objeto no corpo da requisição, O conteúdo esperado no Corpo da Resposta (Response Body): Neste exemplo como o objeto da requisição é nulo, a resposta esperada será do tipo String (String.class)
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode()); // checaremos se a resposta da requisição (Response), é a resposta esperada (OK 🡪 200). Para obter o status da resposta vamos utilizar o Método getStatusCode() da Classe ResponseEntity.
	}
}
