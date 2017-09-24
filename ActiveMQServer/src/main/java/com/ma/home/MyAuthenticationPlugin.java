package com.ma.home;

import org.apache.activemq.security.SimpleAuthenticationPlugin;

import java.util.HashMap;
import java.util.Map;

public class MyAuthenticationPlugin extends SimpleAuthenticationPlugin {
    private String  username ="username";
    private String  password ="password";

    public MyAuthenticationPlugin(){
        secureME();
    }
    public void secureME(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(username, password);
        this.setUserPasswords(map);
    }
}
