package com.microservice.cas_gateway.cfg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "synpac.cas")
public class CasClientConfig {


    //CAS服务端地址
    public String casServiceUrl;

    //应用部署地址
    public String serviceUrl;

    //CAS 服务路径
    public String casContextPath="/cas";

    //应用部署路径
    public String clientContextPath;

    //CAS登录地址
    public String loginUrl="/login";

    //登出地址
    public String logoutUrl="/logout";


    //白名单的正则表达式的值
    public String whiteUrl="^.*(/logout/?)$";

    //白名单鉴权模式，现在只支持正则模式
    public String ignoreUrlPatternType="REGEX";

    //cookie保存登录信息方式默认本地存储
    public String cookieHolderPattern="com.microservice.cas_gateway.cfg.MapCookieHolder";

    //cookie 的key
    public String authKey="my_authKey";

    //代理设置配置参数，代理保存ptg和ptgiou的相对回调地址
    public String proxyReceptorUrl;

    //服务端缓存多长时间单位毫秒
    public String millisBetweenCleanUps="3600000";

    //如果是代理客户端填写true
    public Boolean acceptAnyProxy=false;

    //代理回调全路径，用于代理服务端保存pgt和pgtiou
    public String proxyCallbackUrl;


}
