package com.ma.home;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.TransactionBroker;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.*;

import java.security.Principal;
import java.util.*;

            public class MyAuthenticationPlugin extends SimpleAuthenticationPlugin {
                private String  username ="username";
                private String  password ="password";
                private String  groups = "groups";

                Map<String, String> userPasswords = new HashMap<String, String>();
                List<AuthenticationUser> authenticationUserList = new ArrayList();

                public MyAuthenticationPlugin(){
                    secureME();
                }
                public void secureME(){
                    userPasswords.put(username, password);
                    authenticationUserList.add(new AuthenticationUser(username,password, groups));
                    this.setUserPasswords(userPasswords);
                    this.setUsers(authenticationUserList);
                }
    /*
   // @Override
    public Broker installPlugin(Broker broker) {
        AuthorizationPlugin authorizationPlugin = new AuthorizationPlugin();
        List<DestinationMapEntry> entries = new ArrayList<DestinationMapEntry>();
        try {
            entries.add(makeTopicAuthorization("groupA.topic", "groupA", "groupA", "groupA"));
            entries.add(makeQueueAuthorization("groupA.queue", "groupA", "groupA", "groupA"));
            entries.add(makeQueueAuthorization("groupB.queue", "groupB", "groupB", "groupB"));
            entries.add(makeTopicAuthorization("ActiveMQ.Advisory.>", "all", "all", "all"));
            AuthorizationMap authMap = new DefaultAuthorizationMap(entries);
            return new AuthorizationBroker(broker, authMap);
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return new AuthorizationBroker(broker, null);
    }
    */

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
