package com.generation.blogpessoal.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration // Indica que a classe é do tipo configuração, define uma classe como fonte de definições de Beans
public class SwaggerConfig {
	
	@Bean // Indica ao spring que ele deve invocar aquele método e gerenciar o objeto retornado por ele, agora esse objeto pode ser injetado em qualquer ponto da aplicação
	OpenAPI sprignBlogPessoalOpenAPI() { // Gera a documentação no Swagger utilizando a especificação OpenAPI
		return new OpenAPI().info(new Info().title("Projeto Blog Pessoal")
				.description("Projeto Blog Pessoal - Generation Brasil") // Insere as informações sobre a API 
				.version("v0.0.1")
				.license(new License() // Insere as informações referente a licença da API
						.name("Vinicius Lima")
						.url("https://github.com/vinysl"))
				.contact(new Contact() // Insere as informações de contato da pessoa Desenvolvedora
						.name("Vinicius Lima")
						.url("https://www.linkedin.com/in/vinicius-lima-7b3b23235/")
						.email("vinicius.slima99@icloud.com")))
				.externalDocs(new ExternalDocumentation() // Insere informações referentes a documentações externas
								.description("Github")
								.url("https://github.com/vinysl"));
	}
	
	@Bean
	OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
		
		return openApi -> { // Cria um objeto da classe OpenAPI que gera a documentação no Swagger utilizando a especificação OpenAPI
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations() // Cria um primeiro looping que fará a leitura de todos os recursos (Paths) através do Método getPaths(), que retorna o caminho de cada endpoint. Na sequência, cria um segundo looping que Identificará qual Método HTTP (Operations), está sendo executado em cada endpoint através do Método readOperations(). Para cada Método, todas as mensagens serão lidas e substituídas pelas novas mensagens 
					.forEach(operation -> {
						
						ApiResponses apiResponses = operation.getResponses(); // Cria um Objeto da Classe ApiResponses, que receberá as Respostas HTTP de cada endpoint (Paths) através do Método getResponses()
						
						apiResponses.addApiResponse("200", createApiResponse("Sucesso!")); // Adiciona as novas Respostas no endpoint, substituindo as atuais e acrescentando as demais, através do Método addApiResponse(), identificadas pelo HTTP Status Code (200, 201 e etc)
						apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
						apiResponses.addApiResponse("204", createApiResponse("Objeto Excluido!"));
						apiResponses.addApiResponse("400", createApiResponse("Erro na requisição!"));
						apiResponses.addApiResponse("401", createApiResponse("Acesso não autorizado!"));
						apiResponses.addApiResponse("403", createApiResponse("Acesso proibido!"));
						apiResponses.addApiResponse("404", createApiResponse("Objeto não encontrado!"));
						apiResponses.addApiResponse("500", createApiResponse("Erro na aplicação!"));
					}));
		};
	}
	
	private ApiResponse createApiResponse(String message) { // O Método createApiResponse() adiciona uma descrição (Mensagem), em cada Resposta HTTP
		
		return new ApiResponse().description(message);
	}

}
