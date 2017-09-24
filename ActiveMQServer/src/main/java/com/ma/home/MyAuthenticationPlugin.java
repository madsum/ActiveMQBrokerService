package com.ma.home;

import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

import java.security.Principal;
import java.util.*;

public class MyAuthenticationPlugin extends SimpleAuthenticationPlugin {
    private String  username ="username";
    private String  password ="password";
    private String  groups = "groups";

    Map<String, String> userPasswords = new HashMap<String, String>();
    private Map<String, Set<Principal>> userGroups;
    List<AuthenticationUser> authenticationUserList = new ArrayList();

    public MyAuthenticationPlugin(){
        secureME();
    }
    public void secureME(){
        //userPasswords.put(username, password);
        addAuthenticationUser(new AuthenticationUser(username,password, groups));
        //this.setUserPasswords(userPasswords);
        this.setUsers(authenticationUserList);
    }

    public void addAuthenticationUser(AuthenticationUser authenticationUser){
        authenticationUserList.add(authenticationUser);
    }

/*
        public Map<String, Set<Principal>> setmyUsers(List<?> users) {
        userPasswords = new HashMap<String, String>();
        userGroups = new HashMap<String, Set<Principal>>();
        for (Iterator<?> it = users.iterator(); it.hasNext();) {
            AuthenticationUser user = (AuthenticationUser)it.next();
            userPasswords.put(user.getUsername(), user.getPassword());
            Set<Principal> groups = new HashSet<Principal>();
            StringTokenizer iter = new StringTokenizer(user.getGroups(), ",");
            while (iter.hasMoreTokens()) {
                String name = iter.nextToken().trim();
                groups.add(new GroupPrincipal(name));
            }
            userGroups.put(user.getUsername(), groups);
        }
        return userGroups;
    }*/
}
