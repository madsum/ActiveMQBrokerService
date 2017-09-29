package com.ma.home;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.security.*;

import java.util.ArrayList;
import java.util.List;

public class TestAuthorizationPlugin extends AuthorizationPlugin {

    public Broker installPlugin(Broker broker) {
        //SimpleAuthorizationMap simpleAuthorizationMap = new SimpleAuthorizationMap();
        List<DestinationMapEntry> entries = new ArrayList<DestinationMapEntry>();
        try {
            entries.add(makeAdminEntry());
            entries.add(makeUserEntry());
            entries.add(makeGuestEntry());

            //entries.add(makeTopicAuthorization("groupA.topic", "groupA", "groupA", "groupA"));
            //entries.add(makeQueueAuthorization("groupA.queue", "groupA", "groupA", "groupA"));
            //entries.add(makeQueueAuthorization("groupB.queue", "groupB", "groupB", "groupB"));
            //entries.add(makeTopicAuthorization("ActiveMQ.Advisory.>", "all", "all", "all"));
            AuthorizationMap authMap = new DefaultAuthorizationMap(entries);
            return new AuthorizationBroker(broker, authMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //setMap(createAuthorizationMap());

        return new AuthorizationBroker(broker, null);
    }

    private AuthorizationEntry makeAdminEntry(){
        AuthorizationEntry entry = new AuthorizationEntry();
        entry.setQueue(">");
        try {
            entry.setRead("*");
            entry.setWrite("*");
            entry.setAdmin("admins");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return  entry;
    }

    private AuthorizationEntry makeUserEntry(){
        AuthorizationEntry entry = new AuthorizationEntry();
        entry.setQueue("USERS.>");
        try {
            entry.setRead("guests");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return  entry;
    }

    private AuthorizationEntry makeGuestEntry(){
        AuthorizationEntry entry = new AuthorizationEntry();
        entry.setQueue("GUEST.>");
        try {
            entry.setRead("guest");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return  entry;
    }


    private static   String ADMINS ="ADMINS";
    private static   String USERS ="USERS";
    private static   String GUESTS ="GUESTS";

    public  AuthorizationMap createAuthorizationMap(){
        DestinationMap readAccess=new DefaultAuthorizationMap();
        readAccess.put(new ActiveMQQueue(">"),ADMINS);
        readAccess.put(new ActiveMQQueue("USERS.>"),USERS);
        readAccess.put(new ActiveMQQueue("GUEST.>"),GUESTS);
        readAccess.put(new ActiveMQTopic(">"),ADMINS);
        readAccess.put(new ActiveMQTopic("USERS.>"),USERS);
        readAccess.put(new ActiveMQTopic("GUEST.>"),GUESTS);
        DestinationMap writeAccess=new DefaultAuthorizationMap();
        writeAccess.put(new ActiveMQQueue(">"),ADMINS);
        writeAccess.put(new ActiveMQQueue("USERS.>"),USERS);
        writeAccess.put(new ActiveMQQueue("GUEST.>"),USERS);
        writeAccess.put(new ActiveMQQueue("GUEST.>"),GUESTS);
        writeAccess.put(new ActiveMQTopic(">"),ADMINS);
        writeAccess.put(new ActiveMQTopic("USERS.>"),USERS);
        writeAccess.put(new ActiveMQTopic("GUEST.>"),USERS);
        writeAccess.put(new ActiveMQTopic("GUEST.>"),GUESTS);
        readAccess.put(new ActiveMQTopic("ActiveMQ.Advisory.>"),GUESTS);
        readAccess.put(new ActiveMQTopic("ActiveMQ.Advisory.>"),USERS);
        writeAccess.put(new ActiveMQTopic("ActiveMQ.Advisory.>"),GUESTS);
        writeAccess.put(new ActiveMQTopic("ActiveMQ.Advisory.>"),USERS);
        DestinationMap adminAccess=new DefaultAuthorizationMap();
        adminAccess.put(new ActiveMQTopic(">"),ADMINS);
        adminAccess.put(new ActiveMQTopic(">"),USERS);
        adminAccess.put(new ActiveMQTopic(">"),GUESTS);
        adminAccess.put(new ActiveMQQueue(">"),ADMINS);
        adminAccess.put(new ActiveMQQueue(">"),USERS);
        adminAccess.put(new ActiveMQQueue(">"),GUESTS);
        return new SimpleAuthorizationMap(writeAccess,readAccess,adminAccess);
    }

}
