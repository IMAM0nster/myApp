package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by fy on 2017/12/28.
 */
@Configuration
@EnableSwagger2
public class DemoSwagger {

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("相册作品操作")
                .description("对相册中的作品数据进行增删改查")
                .termsOfServiceUrl("swagger.io")
                .contact("方印源")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket createRESTapi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.service.restful"))
                .paths(PathSelectors.any())
                .build();
    }

}


