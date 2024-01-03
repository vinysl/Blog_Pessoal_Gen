package com.generation.blogpessoal.security;

import static org.springframework.security.config.Customizer.withDefaults;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Indica que a classe é do tipo configuração, define como fonte de definições de beans, essencial ao utilizar uma configuração baseada em java
@EnableWebSecurity // Habilita a segurança de forma global e sobrescreve os métodos que irão redefinir as regras de segurança da aplicação
public class BasicSecurityConfig { // Responsável por sobreescrever a configuração padrão da Spring Security e definir como ela irá funcionar, vamos definir quais serão as formas de autenticação, quais endpoints serão protegidos pelo token jwt, entre outras configurações

    @Autowired // Injeção de dependencia para ter acesso ao filtro de servlet jwt
    private JwtAuthFilter authFilter; 

    @Bean // um Bean é um objeto que é instanciado, montado e gerenciado pelo Spring
    UserDetailsService userDetailsService() { // Valida se o usuario que está tentando se autenticar existe no bd da aplicação

        return new UserDetailsServiceImpl();
    }

    @Bean
    PasswordEncoder passwordEncoder() { // Usamos esse método para criptografar e validar a senha do usuario
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider() { // Informa o método de autenticação que será utilizado
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); // Utilizada para autenticar um objeto da classe usuario(email) e a senha, validando os dados no bd da aplicação
        authenticationProvider.setUserDetailsService(userDetailsService()); // Utilizado para validar o usuario(email) do objeto da classe usuario
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // Utilizado para validar a senha do usuario
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) // Implementa a configuração de autenticação
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // Estamos informando ao Spring que a configuração padrão da Spring Security será substituida por uma nova configuração, nesta iremos customizar a autenticação da aplicação desabilitando o formulario de login e habilitando a autenticação via http

    	http // Vamos configurar a segurança baseada na web para todas as requisições http enviadas para esta aplicação
	        .sessionManagement(management -> management // Definiremos que o nosso sistema não guardara sessões para o cliente
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        		.csrf(csrf -> csrf.disable()) // Iremos desabilitar a proteção que vem ativa contra ataques do tipo CSRF (Cross-Site-Request-Forgery) que seria uma interceptação dos dados de autenticação antes da requisição chegar ao servidor
	        		.cors(withDefaults()); // Vamos liberar acesso de outras origens (Requisições de outros servidores HTTP) desta forma nossa aplicação poderá ser acessada por outros dominios ou seja de outros endereços além do endereço onde a aplicação está hospedada

    	http
	        .authorizeHttpRequests((auth) -> auth
	                .requestMatchers("/usuarios/logar").permitAll()
	                .requestMatchers("/usuarios/cadastrar").permitAll()
	                .requestMatchers("/error/**").permitAll()
	                .requestMatchers(HttpMethod.OPTIONS).permitAll()
	                .anyRequest().authenticated())
	        .authenticationProvider(authenticationProvider())
	        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	        .httpBasic(withDefaults());

		return http.build();

    }

}