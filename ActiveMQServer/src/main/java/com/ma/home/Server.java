package com.ma.home;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import javax.jms.*;


public class Server implements  MessageListener {
    private static int ackMode;
    private static String messageQueueName;
    private static String messageBrokerUrl;

    private Session session;
    private boolean transacted = false;
    private MessageProducer replyProducer;
    private String  username ="username";
    private String  password ="password";
    private  SimpleAuthenticationPlugin simpleAuthenticationPlugin;

    static {
        messageBrokerUrl = "tcp://localhost:61616";
        messageQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    public Server() {
        try {
            //This message broker is embedded
            BrokerService broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(messageBrokerUrl);
            MyAuthenticationPlugin[] myAuthenticationPlugin = new MyAuthenticationPlugin[1];
            myAuthenticationPlugin[0] = new MyAuthenticationPlugin();
            broker.setPlugins(myAuthenticationPlugin);
            broker.start();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        this.setupMessageQueueConsumer();
    }

    private void setupMessageQueueConsumer() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);

        Connection connection;
        try {
            connection = connectionFactory.createConnection(username, password);
            connection.start(); // This line thows exception
            this.session = connection.createSession(this.transacted, ackMode);
            Destination adminQueue = this.session.createQueue(messageQueueName);

            //Setup a message producer to respond to messages from clients, we will get the destination
            //to send to from the JMSReplyTo header field from a Message
            //this.replyProducer = this.session.createProducer(null);
            //this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            //Set up a consumer to consume messages off of the admin queue
            MessageConsumer consumer = this.session.createConsumer(adminQueue);
            consumer.setMessageListener(this);

          //  new BufferedReader(new InputStreamReader(System.in)).readLine();

        } catch (JMSException e) {
            System.out.println("Exception: "+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message message) {
        System.out.println("I got msg: "+message);
    }

    public static void main(String[] args) {
        new Server();
        System.out.println("I'm done. END");
    }
}