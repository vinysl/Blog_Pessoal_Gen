package com.generation.blogpessoal.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.generation.blogpessoal.model.Usuario;

public class UserDetailsImpl implements UserDetails { // Implementa a interface UserDetails que tem como principal funcionalidade fornecer as iformaçõs basicas do usuario para o Spring Security(Usuario, senha, direitos de acesso e as restrições da conta)

	private static final long serialVersionUID = 1L; // Identificador da versão da classe utilizado para serializar e desserializar um objeto de uma classe que implementa a interface serializable, usamos para lembrar as versões de uma classe que implementa a interface serializable com o objetivo de verificar se uma classe carregada e o objeto serializado são compativeis ou seja, se o objeto foi gerado pela mesma versão da classe

	private String userName; // Recebera o atributo usuario (e-mail)
	private String password; // Recebera o atributo senha
	private List<GrantedAuthority> authorities; // Responsável por receber os direitos de acesso do usuário(autorizações ou roles) que são objetos de uma classe que herdará a interface GrantedAuthority

	public UserDetailsImpl(Usuario user) { // Método construtor da classe
		this.userName = user.getUsuario();
		this.password = user.getSenha();
	}

	public UserDetailsImpl() {	}

	@Override // Polimorfismo de sobreescrita
	public Collection<? extends GrantedAuthority> getAuthorities() { // Responsável por retornar os direitos de acesso do usuário(autorizações ou roles), o sinal "?" significa que o método pode receber um objeto de qualquer classe 

		return authorities;
	}

	@Override
	public String getPassword() { // Retornará o o valor do atributo password

		return password;
	}

	@Override
	public String getUsername() { // Retornará o valor do atributo username

		return userName;
	}

	@Override
	public boolean isAccountNonExpired() { // Indica se o acesso do usuário expirou, uma conta expirada não pode ser autenticada
		return true;
	}

	@Override
	public boolean isAccountNonLocked() { // Indica se o usuario está bloqueado ou desbloqueado, um usuario bloqueado não pode ser autenticado
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() { // Indica se as credencias do usuario expiraram, senha expirada impede a autenticação
		return true;
	}

	@Override
	public boolean isEnabled() { // Indica se o usuario está habilitado ou desabilitado, um usuario desabilitado não pode ser autenticado
		return true;
	}

}
