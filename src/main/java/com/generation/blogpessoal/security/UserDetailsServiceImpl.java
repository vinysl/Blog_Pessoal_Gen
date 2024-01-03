package com.generation.blogpessoal.security;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;

@Service // Indica que é uma classe de serviço, classe de serviço é responsável por implementar as regras de negócios e as tratativas de dados de uma parte ou recurso do sistema
public class UserDetailsServiceImpl implements UserDetailsService { // É uma implementação da interface UserDetailsService, a interface UserDetailsService permite autenticar um usuário baseando-se na sua existência no bd
	
	@Autowired // Injeção de dependência, cria um ponto de injeção da interface UsuarioRepository permitindo acessar os métodos de interação com os objetos da classe Usuario, persistidos no bd da aplicação
	private UsuarioRepository usuarioRepository;

	@Override
	// Implementa o método que receberá o usuário através da tela de login do sistema
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// Cria um objeto da classe Optional do tipo Usuario que receberá o retorno da Query Method findByUsuario(String usuario) implementada na interface UsuarioRepository para checar se o usuário digitado está persistindo no bd
		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userName);

		if (usuario.isPresent())
			return new UserDetailsImpl(usuario.get()); // Caso o objeto usuario esteja presente no bd executa o método construtor da classe UserDetailsImpl passando o objeto usuario como parametro
		else
			throw new ResponseStatusException(HttpStatus.FORBIDDEN); // Caso o usuario não seja encontrado será devolvido o HTTP Status 403 - FORBIDDEN (Acesso Proibido - você está tentando alcançar um endereço ou um site ao qual está proibido de acessar).
			
	}
}
