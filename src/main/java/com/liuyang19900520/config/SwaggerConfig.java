package com.liuyang19900520.config;

import com.google.common.collect.Lists;
import com.liuyang19900520.domain.SysUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurerAdapter {

    @Value("${swagger.host}")
    private String swaggerHost;


    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("IOTPage_account")
                .description("Apis of information about users")
                .termsOfServiceUrl("")
                .version("0.1.0")
                .contact(new Contact("LiuYang", "", "liuyang19900520@hotmail.com"))
                .build();
    }

    @Bean
    public Docket createRestApi() {

        List<ResponseMessage> responseMessageList = Lists.newArrayList();
        responseMessageList.add(new ResponseMessageBuilder().code(400).message("请求出错").responseModel(new ModelRef("ResultVo")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(401).message("未通过认证").responseModel(new ModelRef("ResultVo")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(403).message("无权限").responseModel(new ModelRef("ResultVo")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(404).message("未找到资源").responseModel(new ModelRef("ResultVo")).build());
        responseMessageList.add(new ResponseMessageBuilder().code(500).message("服务器出错").responseModel(new ModelRef("ResultVo")).build());


        ParameterBuilder builder = new ParameterBuilder();
        Parameter parameterToken = builder
                // 从cookie中获取token
                .parameterType("header") //参数类型支持header, cookie, body, query etc
                .name("token") //参数名
                .defaultValue("") //默认值
                .description("令牌")
                .modelRef(new ModelRef("string")) //指定参数值的类型
                .required(false).build(); //非必需，这里是全局配置，然而在登陆的时候是不用验证的

        Parameter parameterAuth = builder
                // 从cookie中获取token
                .parameterType("header") //参数类型支持header, cookie, body, query etc
                .name("Authorization") //参数名
                .defaultValue("") //默认值
                .description("验证")
                .modelRef(new ModelRef("string")) //指定参数值的类型
                .required(false).build(); //非必需，这里是全局配置，然而在登陆的时候是不用验证的

        Parameter parameterDate = builder
                // 从cookie中获取token
                .parameterType("header") //参数类型支持header, cookie, body, query etc
                .name("Date") //参数名
                .defaultValue("") //默认值
                .description("时间戳")
                .modelRef(new ModelRef("string")) //指定参数值的类型
                .required(false).build(); //非必需，这里是全局配置，然而在登陆的时候是不用验证的


        List<Parameter> parameters = Lists.newArrayList(parameterToken, parameterAuth, parameterDate);
        return new Docket(DocumentationType.SWAGGER_2)
                .host(this.swaggerHost)
                .apiInfo(this.apiInfo())
                .select()
                // 指定controller存放的目录路径
                .apis(RequestHandlerSelectors.basePackage("com.liuyang19900520"))
                .paths(PathSelectors.any())
                .build()
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalOperationParameters(parameters);
    }

    /**
     * swagger ui资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * swagger-ui.html路径映射，浏览器中使用/api-docs访问
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api-docs", "/swagger-ui.html");
    }
}