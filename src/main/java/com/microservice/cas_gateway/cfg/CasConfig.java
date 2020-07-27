package com.microservice.cas_gateway.cfg;

import org.jasig.cas.client.util.ReflectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CasClientConfig.class)
@AutoConfigureAfter(CasClientConfig.class )
public class CasConfig {

    @Autowired
    private CasClientConfig casClientConfig;

    @Bean
    public CookieHolder cookieHolder(){

        Long[] args = new Long[1];
        args[0] = Long.valueOf(casClientConfig.millisBetweenCleanUps);
        return  ReflectUtils.newInstance(casClientConfig.getCookieHolderPattern(), args);
    }

    @Bean
    public AuthenticationGatewayFilter authenticationGatewayFilter(){


        return new AuthenticationGatewayFilter(casClientConfig,cookieHolder());
    }

    @Bean
    public MyCas30ProxyTicketValidationFilter myCas30ProxyTicketValidationFilter(){

        return new MyCas30ProxyTicketValidationFilter(casClientConfig,cookieHolder());
    }






}
