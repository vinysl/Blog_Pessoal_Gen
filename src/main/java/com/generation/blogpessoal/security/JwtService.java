package com.generation.blogpessoal.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component // Indica que é uma classe de componente que pode injetar e instanciar qualquer dependencia especificada na implementação da classe em qualquer outra classe sempre que necessário
public class JwtService { // Responsável por criar e validar o token JWT
	// Este atributo armazenara a chave de assinatura do token jwt(secret), o modificador final indica que esse valor será constante, nunca será modificado, o modificador static indica que o atributo deve estar associado apenas e exclusivamente a essa classe
	public static final String SECRET = "ac5ebcf4a08527498b6a44a274b04977efd0a34b5908928aed0532feb49b7462";

	private Key getSignKey() { // Responsável por codificar a secret em base 64(método para codificação de dados para transferências de conteudo na internet) e gerar a assinatura do token jwt codificado pelo algoritimo HMAC SHA256
		byte[] keyBytes = Decoders.BASE64.decode(SECRET); // Foi criado o vetor tipo byte para receber o resultado da codificação em base 64
		return Keys.hmacShaKeyFor(keyBytes); // Retornara a chave de assinatura do toke jwt codificada pelo algoritimo HMAC SHA256
	}

	private Claims extractAllClaims(String token) { // Retorna todas as claims(informações) inseridas no payload(corpo) do token jwt
		return Jwts.parserBuilder() // Será criada uma nova instância da interface jwt
				.setSigningKey(getSignKey()).build() // Verifica se a assinatura do token jwt é valida
				.parseClaimsJws(token).getBody(); // Extrai todas as claims do corpo do token e retorna todas as claims encontradas através do comando return
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // Retorna uma claim especifica inserida no payload do toke jwt
		final Claims claims = extractAllClaims(token); // Receberá a execução do método que retornara todas as claims que forem encontradas no corpo do token enviado no parametro 	do metodo
		return claimsResolver.apply(claims);
	}

	public String extractUsername(String token) { // Recupera os dados da claim sub onde se encontra o usuario(e-mail)
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) { // Recupera os dados da claim exp onde se encontra a data e o horário de expiração do token jwt
		return extractClaim(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) { // Recupera os dados da claim exp onde se encontra a data e o horario de expiração do token jwt
		return extractExpiration(token).before(new Date()); // Através do método before se o token está ou não expirado, se a data e hora for anterior a data e hora atual o token jwt está expirado
	}

	public Boolean validateToken(String token, UserDetails userDetails) { // Valida se o token jwt pertence ao usuario que enviou o token através do cabeçalho de uma requisição http na propriedade authorization
		final String username = extractUsername(token); // Recebera a execução do método extractUsername(String token) que retornará a claim sub que contem o usuario autenticado que foi inserido no corpo do token que foi enviado no parametro do metodo
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // O Método retornará true se o usuario que foi extraído do token (claim sub), for igual ao usuario autenticado (atributo username do Objeto da Classe UserDetails) e o token não estiver expirado (!isTokenExpired(token)).
	}

	private String createToken(Map<String, Object> claims, String userName) { // Cria o token jwt 
		return Jwts.builder() // Responsável por criar o token
					.setClaims(claims) // Responsável por inserir as claims personalizadas no payload do token jwt
					.setSubject(userName) // Responsável por inserir a claim sub preenchida com o usuario no payload do token jwt 
					.setIssuedAt(new Date(System.currentTimeMillis())) // Responsável por inserir a claim iat preenchida com a data e hora exata do momento da criação do token no payload do token jwt
					.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Responsável por inserir a claim exp preenchida com a data e hora exata do momento da criação do token somada ao tempo limite do token no payload do token jwt
					.signWith(getSignKey(), SignatureAlgorithm.HS256).compact(); // Responsável por inserir a assinatura do token e o algoritimo de encriptação do token jwt do token jwt. O Método .compact() finaliza a criação do Token JWT e o serializa em uma String compacta e segura para URL, de acordo com as regras do JWT.
	}

	public String generateToken(String userName) { // Responsável por gerar um novo token a partir do usuario que será recebido através do parametro username. Este Método será utilizado na Classe de Serviço do Usuario (UsuarioService), para criar um Token JWT sempre que o usuário se autenticar e o Token estiver expirado ou ainda não tiver sido gerado.
		Map<String, Object> claims = new HashMap<>(); // Cria uma collection map para enviar as claims personalizadas
		return createToken(claims, userName); // Retorna a execução do metodo que criara o token jwt
	}

}
