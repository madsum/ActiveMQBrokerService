package com.ma.home;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;

public class ActiveMQProducer {

    private static String activeMQconnectionUri = "tcp://localhost:61616";
    private static ActiveMQConnectionFactory connectionFactory;
    private BrokerService broker;
    private static Connection receiverConnection;
    private static Session receiverSession;
    private static String  username ="username";
    private static String  password ="password";


    public static void main(String[] args) {
        try {
            connectionFactory = new ActiveMQConnectionFactory(activeMQconnectionUri);
            connectionFactory.setUserName(username);
            connectionFactory.setPassword(password);
            receiverConnection = connectionFactory.createConnection();
            receiverSession = receiverConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            receiverConnection.start();

            Connection connection = connectionFactory.createConnection();
            connection.start();
            // Now create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("client.messages");
            //the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(queue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // Create a messages for the current climate
            String text = "group_msg testing 1";
            TextMessage message = session.createTextMessage(text);
            // Send the message to topic
            System.out.println("I am sending to the queue: " + text);
            producer.send(message);
            // Do the cleanup
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            System.out.println(e.initCause(e));
        }
    }
}
