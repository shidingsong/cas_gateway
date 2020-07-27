package com.microservice.cas_gateway.cfg;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.validation.*;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class MyCas30ProxyTicketValidationFilter implements GlobalFilter, Ordered {

    protected Cas20ServiceTicketValidator defaultServiceTicketValidator;
    protected Cas20ProxyTicketValidator defaultProxyTicketValidator;

    protected TicketValidator ticketValidator;

    protected Protocol protocol;

    protected CasClientConfig casClientConfig;

    //cookie存储器
    private CookieHolder cookieHolder;



    public MyCas30ProxyTicketValidationFilter(CasClientConfig casClientConfig,CookieHolder cookieHolder) {
        this.casClientConfig=casClientConfig;
        this.protocol=Protocol.CAS3;
        this.defaultServiceTicketValidator = new Cas30ServiceTicketValidator(casClientConfig.casServiceUrl);
        this.defaultProxyTicketValidator = new Cas30ProxyTicketValidator(casClientConfig.casServiceUrl);
        this.casClientConfig=casClientConfig;
        this.cookieHolder=cookieHolder;
        ticketValidatorInit();
    }





    //初始化ticket验证器
    protected  TicketValidator ticketValidatorInit(){
        if(!casClientConfig.getAcceptAnyProxy()){
            this.ticketValidator=new Cas30ServiceTicketValidator(casClientConfig.casServiceUrl+casClientConfig.casContextPath);
        }else {
            this.ticketValidator=new Cas30ProxyTicketValidator(casClientConfig.casServiceUrl+casClientConfig.casContextPath);

        }

        return ticketValidator;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
        ServerHttpResponse response=exchange.getResponse();
        if(preFilter(request,response)){
            //从参数中获取ticket参数
            String ticket=GatewayCommonUtils.safeGetParameter(request, this.protocol.getArtifactParameterName());
            //如果ticket参数为空则跳过ticket验证器，进入到认证拦截器，由认证拦截器去跳转到登录页面进行登录
            if(StringUtils.isEmpty(ticket)){
                return chain.filter(exchange);
            }else {
                try {
                    Assertion assertion=ticketValidator.validate(ticket, constructServiceUrl(request));
                    Object authId = request.getCookies().get(casClientConfig.authKey);
                    if(authId==null){
                        //cookie为空跳转到认证服务器去认证
                        return chain.filter(exchange);
                    }
                    cookieHolder.setAttr(authId.toString(),"_const_cas_assertion_",assertion);
                    //跳转回原来访问的地址（去掉url中的ticket参数,使浏览器访问的地址和原来的一样）
                    return GatewayCommonUtils.redirect(exchange, constructServiceUrl(request));

                } catch (TicketValidationException e) {
                    e.printStackTrace();
                }

            }


        }else {
            return chain.filter(exchange);
        }
        return null;
    }



    //将访问的地址编码进行URLEncode后返回
    protected final String constructServiceUrl(ServerHttpRequest request) {
        return GatewayCommonUtils.constructServiceUrl(request, this.protocol.getServiceParameterName(), this.protocol.getArtifactParameterName(), true,true);
    }


    public boolean preFilter(ServerHttpRequest request,ServerHttpResponse response){
        String requestUri=request.getURI().toString();
        if(!StringUtils.isEmpty(casClientConfig.proxyReceptorUrl) && requestUri.endsWith(casClientConfig.proxyReceptorUrl)){
            return false;
        }
        return true;
    }



    @Override
    public int getOrder() {
        return -100;
    }
}
