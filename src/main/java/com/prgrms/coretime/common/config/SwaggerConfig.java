package com.prgrms.coretime.common.config;

import com.fasterxml.classmate.TypeResolver;
import com.prgrms.coretime.common.entity.swagger.MyPageable;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

  private final TypeResolver typeResolver = new TypeResolver();

  @Bean
  public Docket apiV1() {
    return new Docket(DocumentationType.SWAGGER_2)
//        .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
//            typeResolver.resolve(MyPageable.class)))
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.ant("/api/v1/**"))
        .build()
        .apiInfo(apiInfo())
        .securitySchemes(List.of(apiKey()))
        .securityContexts(List.of(securityContext()));
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Coretime API Docs")
        .description("Descriptions of Coretime API")
        .version("1.0")
        .build();
  }

  private ApiKey apiKey() {
    return new ApiKey("JWT", "accessToken", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth()).build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return List.of(new SecurityReference("JWT", authorizationScopes));
  }
}
