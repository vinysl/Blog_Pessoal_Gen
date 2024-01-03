package com.generation.blogpessoal.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Indica que a classe é uma classe de componente que pode injetar e instanciar qualquer dependencia especificada na implementação da classe em qualquer outra classe sempre que necessário
public class JwtAuthFilter extends OncePerRequestFilter { // A Classe JwtAuthFilter intercepta e valida o token jwt em toda e qualquer requisição http, a classe OncePerRequestFilter garante que um filtro seja executado apenas uma vez por requisição http

    @Autowired // Injeção de dependencia
    private JwtService jwtService;

    @Autowired // Injeção de dependenciarhr
    private UserDetailsServiceImpl userDetailsService;

    @Override // Polimorfismo de sobreescrita
    // Responsável por implementar um filtro de sevlet personalizado 
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // Recebera o token jwt presente no cabeçalho da requisição http na propriedade authorization
        String token = null; // Recebera o token jwt presente no cabeçalho da requisição http sem a palavra bearer
        String username = null; // Recebera o usuario(email) presente no payload do token jwt
    
        try{ // Inicializado para capturar as exceptions do processo de validação do token jwt
            if (authHeader != null && authHeader.startsWith("Bearer ")) { // Verifica se o token jwt foi encontrado no cabeçalho da requisição e se ele inicia com a palavra bearer
                token = authHeader.substring(7); // Caso seja verdadeiro a variavel token recebera o token jwt sem a palavra bearer, ira ignorar os 7 primeiros caracteres do token ou seja a palavra bearer + o espaço em branco
                username = jwtService.extractUsername(token); // Recebera o usario (email)
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Verifica se a variavel username é diferente de nulo ou seja se foi encontrado o usuario no payload do token jwt
                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Caso a condição acima seja verdadeira inicia-se o processo de construção do objeto da classe UserDetails que armazenara as informações do usuario autenticado através da classe UserDetailsImpl que checara se o usuario existe no bd  
                    
                if (jwtService.validateToken(token, userDetails)) { // Valida o toke jwt
                	// Se o token jwt for validado será construido um novo objeto da classe UsernamePasswordAuthenticationToken chamado authToken que será responsável por autenticar um usuário na Spring Security e definir um Objeto Authentication, que será itilizado para autenticar o Usuário na Security Context, com o objetivo de manter o usuário conectado na Security Context até o Token JWT expirar
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); 
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Adiciona a requisição http dentro do objeto authToken Antes de adicionarmos a Requisição HTTP, precisamos converter o Objeto request da Classe HttpServletRequest em uma instância da Classe WebAuthenticationDetailsSource, que é uma Classe interna do Spring Security, usada pelos Filtros de Servlet para tratar Requisições HTTP
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Com o Objeto authToken configurado, ele será utilizado para autenticar o usuário na Security Context, através do Método setAuthentication()
                }
            
            }
            filterChain.doFilter(request, response); // Chamamos o próximo Filtro de Servlet através do método doFilter().
            // Caso o processo de validação do token jwt falhar uma das 5 exceptions abaixo será lançada
        }catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException // ExpiredJwtException: O token jwt expirou // UnsupportedJwtException: O Token não está no formato JWT // MalformedJwtException: A construção do Token está errada e ele deve ser rejeitado
                | SignatureException | ResponseStatusException e){ // SignatureException: A assinatura do Token JWT não confere // ResponseStatusException: Retorna um HTTP Status em conjunto com uma Exception
            response.setStatus(HttpStatus.FORBIDDEN.value()); // Linha 66: O Filtro Retornará o HTTP Status 403 🡪 FORBIDDEN. Este Status indica que o Token é inválido e por isso o acesso não foi permitido. Neste caso, o usuário deve se autenticar novamente no sistema e gerar um novo Token
            return; // Linha 67: A palavra return foi inserida sozinha, sem nenhum valor, indicando que após retornar o HTTP Status 403 🡪 FORBIDDEN, a execução do Filtro de Servlet JwtAuthFilter deve ser finalizada imediatamente
        }
    }
}
