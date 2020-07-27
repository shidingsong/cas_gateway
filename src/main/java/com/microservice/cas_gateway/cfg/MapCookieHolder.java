package com.microservice.cas_gateway.cfg;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MapCookieHolder implements CookieHolder {

    private  ConcurrentHashMap<String ,HashMap<String,Object>> holder=new ConcurrentHashMap<>();

    //过期时间
    private long expireTime;





    public MapCookieHolder(Long expireTime) {
        if (expireTime==null){
            throw new RuntimeException("过期时间不能为空");
        }
        this.expireTime = expireTime;
    }

    @Override
    public Object getAttr(String userKey,String key) {
        if(holder.get(userKey)==null){
            return null;
        }
        return holder.get(userKey).get(key);
    }

    @Override
    public void setAttr(String userKey,String key, Object attr) {

        HashMap<String,Object> userHolder=holder.get(userKey);
        if(userHolder!=null){
            userHolder.put(key,attr);
        }else {
            userHolder=new HashMap<>();
            userHolder.put(key,attr);
            userHolder.put("expireTime",expireTime);
            holder.put(userKey,userHolder);
        }

    }
}
