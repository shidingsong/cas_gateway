package com.microservice.cas_gateway.cfg;

public interface CookieHolder {

    Object getAttr(String userKey, String key);

    void setAttr(String userKey, String key, Object attr);



}
