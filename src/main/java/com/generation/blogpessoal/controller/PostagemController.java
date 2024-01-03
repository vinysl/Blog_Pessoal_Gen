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

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController // Indica para a classe que é uma controladora
@RequestMapping("/postagens") // Indica os endpoins da classe
@CrossOrigin(origins = "*", allowedHeaders = "*") // Diz quais origens e quais cabeçalhos http pode ter acesso a nossa API, indica que a Classe controladora permitirá o recebimento de requisições realizadas de fora do domínio (localhost e futuramente da nuvem quando o Deploy da aplicação for efetivado) ao qual ela pertence.
public class PostagemController {
	
	@Autowired // Anotação responsável por fazer a injeção de dependencia do Spring, trazendo todos os métodos criados na PostagemRepository, na postagem não utilizamos o método construtor então a injeção de dependencia define quais Classes serão instanciadas e em quais lugares serão Injetadas quando houver necessidade.
	private PostagemRepository postagemRepository; // Através da injeção de dep, o spring cria uma instância da interface PostagemRepository, onde podemos utilizar os seus métodos sem a necessidade de criar um objeto
	
	@Autowired
	private TemaRepository temaRepository; // Com a injeção de dependência da interface TemaRepository teremos acesso ao recurso Tema dentro da classe PostagemController. Dessa forma podemos consultar se o objeto da classe Tema existe, antes de efetuarmos a persistência dos dados no bd
	
	@GetMapping // Execute esse método caso o verbo http seja GET
	public ResponseEntity<List<Postagem>> getAll() { // Responsável por listar todas as postagens que teremos em nosso BD
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
	@GetMapping("/{id}") // Mapeia todas as requisições HTTP GET dentro do recurso postagem para um método especifico que respondera as requisições
	public ResponseEntity<Postagem> getById(@PathVariable Long id) { // Indica o caminho do ID da tabela
		return postagemRepository.findById(id) // Retornara um objeto da classe Postagem persistindo no banco de dados caso ele seja encontrado, caso contrário será retornado um objeto nulo
				.map(resposta -> ResponseEntity.ok(resposta)) // Mapeia a resposta e retorna ok se existir o ID
				.orElse(ResponseEntity.notFound().build()); // Caso não encontre o id retornara uma resposta de erro not found
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) { // Será retornado um objeto da classe List contendo todos os objetos da classe postagem persistindo no banco de dados cujo atributo contenha a String enviada como parâmetro do método 
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	@PostMapping // Responderá a todas as requisições do tipo HTTP POST
	public ResponseEntity<Postagem> post (@Valid @RequestBody Postagem postagem) { // @Valid valida o objeto Postagem enviado no corpo da requisição @RequestBody, conforme as regras definidas na Postagem
		if(temaRepository.existsById(postagem.getTema().getId())) // Checa se o id do objeto tema da classe tema inserido no objeto postagem da classe postagem existe
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem)); // Se o objeto tema existir salva o objeto no banco de dados e retorna 201 CREATED se o objeto foi salvo no bd
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null); // Caso não encontre o objeto tema será retornado o HTTP Status BAD REQUEST -> 400
	}
	
	@PutMapping // Responderá a todas as requisições do tipo HTTP PUT, o método PUT atualiza um objeto da classe Postagem existente no banco de dados
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
		if(postagemRepository.existsById(postagem.getId())) { // Checa se o id passado no objeto postagem, da classe postagem, existe. Caso não exista não é possível atualizar
			if(temaRepository.existsById(postagem.getTema().getId())) // Checa se o id passado no objeto tema da classe tema inserido no objeto postagem da classe postagem existe
				return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)); // Se o objeto tema existir salva e retorna o HTTP Status OK -> 200 se o objeto foi atualizado no bd
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null); // Caso o objeto não exista será retornado o HTTP STATUS BAD REQUEST -> 400
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();	// Se o objeto postagem não for encontrado será retornado o HTTP Status NOT FOUND -> 404 indicando que a postagem não existe
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT) // Indica que o método delete, terá um status HTTP específico quando a requisição for bem sucedida, ou seja, será retornado o HTTP Status NO_CONTENT 204, ao invés do HTTP Status OK 200 como resposta padrão do Método
	@DeleteMapping("/{id}") // Responderá a todas as requisições do tipo HTTP DELETE enviadas no endereço localhost.
	public void delete(@PathVariable Long id) { // Foi definido como void por que ao deletar um objeto da classe Postagem do banco de dados ela deixa de existit, logo não existe um objeto para ser retornado
		Optional<Postagem> postagem = postagemRepository.findById(id); // Verifica se o id informado se encontra no BD
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Se o id não for encontrado no bd será lançada a Exception com o código 404
		postagemRepository.deleteById(id); // Se for encontrado conclui o método com a exclusão do objeto
	}

}
