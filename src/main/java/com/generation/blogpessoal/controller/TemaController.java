package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController // Indica para a classe que é uma controladora
@RequestMapping("/temas") // Indica os endpoints da classe
@CrossOrigin(origins = "*", allowedHeaders = "*") // Diz quais origens e quais cabeçalhos http pode ter acesso a nossa API, indica que a Classe controladora permitirá o recebimento de requisições realizadas de fora do domínio (localhost e futuramente da nuvem quando o Deploy da aplicação for efetivado) ao qual ela pertence.
public class TemaController {
	
	@Autowired // Traz a repository para dentro da controller para termos acesso aos métodos de pesquisa do JPA
	private TemaRepository temaRepository;
	
	@GetMapping
	public ResponseEntity<List<Tema>> getAll() { // O método getAll retorna todos os temas contido no bd
		return ResponseEntity.ok(temaRepository.findAll());
	}
	
	@GetMapping("/{id}") // Mapeia todas as requisições HTTP GET dentro do recurso Tema para um método especifico que respondera as requisições
	public ResponseEntity<Tema> getById(@PathVariable Long id) { // Indica o caminho do Id da tabela
		return temaRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta)).orElse(ResponseEntity.notFound().build());
		//Retornara um objeto da classe Tema persistindo no bd caso ele seja encontrado, caso contrário será retornado um objeto nulo
	}
	
	@GetMapping("/descricao/{descricao}")
	public ResponseEntity<List<Tema>> getByTitle(@PathVariable String descricao) { // Será retornado um objeto da classe List contendo todos os objetos da classe Tema persistindo no bd cujo atributo contenha a String enviada como parâmetro do método
		return ResponseEntity.ok(temaRepository.findAllByDescricaoContainingIgnoreCase(descricao));
	}
	
	@PostMapping // Responderá todas as requisições do tipo HTTP POST, o método POST cria um novo tema no bd
	public ResponseEntity<Tema> post(@Valid @RequestBody Tema tema) { // @Valid valida o objeto Tema enviado no cordo da requisição @RequestBody, conforme as regras definidas na Tema
		return ResponseEntity.status(HttpStatus.CREATED).body(temaRepository.save(tema)); // Salva o objeto no bd e retorna 201 CREATED se o objeto foi salvo no bd
	}
	
	@PutMapping // Respondera todas as requisições do tipo HTTP PUT, o método PUT atualiza um objeto da classe tema existente no bd
	public ResponseEntity<Tema> put(@Valid @RequestBody Tema tema) {
		return temaRepository.findById(tema.getId()).map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(temaRepository.save(tema))) // Caso o tema se encontre no bd ele retorna 201 OK que foi atualizado
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // Caso não exista retornará 404 NOT FOUND informando que o id não foi encontrado
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT) // Indica que o método não terá um retorno com conteúdo
	@DeleteMapping("/{id}") // Responderá a todas as requisições do tipo HTTP DELETE enviadas no endereço localhost
	public void delete(@PathVariable Long id) { // Foi definido como void por que ao deletar um objeto da classe Tema no bd ele deixa de existir, logo não existe um objeto para ser retornado
		Optional<Tema> tema = temaRepository.findById(id); // Verifica se o tema existe antes de apaga-lo
		
			if(tema.isEmpty())
				throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Caso não exista retorna o erro 404 NOT FOUND
			temaRepository.deleteById(id); // Se o id for encontrado no bd ele conclui deletando o objeto
	}
	
	

}
