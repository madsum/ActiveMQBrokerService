package com.ma.home;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.security.*;

import javax.jms.*;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;


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

            private BrokerService brokerService;

            static {
                messageBrokerUrl = "tcp://localhost:61616";
                messageQueueName = "client.messages";
                ackMode = Session.AUTO_ACKNOWLEDGE;
            }

            public void startBroker() throws Exception {
                brokerService = new BrokerService();
                brokerService.setPersistent(false);
                brokerService.setDeleteAllMessagesOnStartup(true);
                brokerService.setAdvisorySupport(false);
                brokerService.getManagementContext().setCreateConnector(false);
                brokerService.getManagementContext().setCreateMBeanServer(false);
                brokerService.addConnector(messageBrokerUrl);

                ArrayList<BrokerPlugin> plugins = new ArrayList<BrokerPlugin>();

                BrokerPlugin authenticationPlugin = configureAuthentication();
                if (authenticationPlugin != null) {
                    plugins.add(configureAuthorization());
                }

                BrokerPlugin authorizationPlugin = configureAuthorization();
                if (authorizationPlugin != null) {
                    plugins.add(configureAuthentication());
                }
                BrokerPlugin[] brokerPluginsArray = new BrokerPlugin[plugins.size()];  //(BrokerPlugin[])plugins.toArray();
                brokerPluginsArray = plugins.toArray(brokerPluginsArray);
                brokerService.setPlugins(brokerPluginsArray);
                brokerService.start();
                brokerService.waitUntilStarted();

            }

            protected BrokerPlugin configureAuthentication() throws Exception {
                List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
                users.add(new AuthenticationUser("system", "manager", "users,admins"));
                users.add(new AuthenticationUser("user", "password", "users"));
                users.add(new AuthenticationUser("guest", "password", "guests"));
                SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
                authenticationPlugin.setUsers(users);

                return authenticationPlugin;
            }

            protected BrokerPlugin configureAuthorization() throws Exception {

                @SuppressWarnings("rawtypes")
                List<DestinationMapEntry> authorizationEntries = new ArrayList<DestinationMapEntry>();

                AuthorizationEntry entry = new AuthorizationEntry();
                entry.setQueue(">");
                entry.setRead("admins");
                entry.setWrite("admins");
                entry.setAdmin("admins");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setQueue("USERS.>");
                entry.setRead("users");
                entry.setWrite("users");
                entry.setAdmin("users");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setQueue("GUEST.>");
                entry.setRead("guests");
                entry.setWrite("guests,users");
                entry.setAdmin("guests,users");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setTopic(">");
                entry.setRead("admins");
                entry.setWrite("admins");
                entry.setAdmin("admins");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setTopic("USERS.>");
                entry.setRead("users");
                entry.setWrite("users");
                entry.setAdmin("users");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setTopic("GUEST.>");
                entry.setRead("guests");
                entry.setWrite("guests,users");
                entry.setAdmin("guests,users");
                authorizationEntries.add(entry);
                entry = new AuthorizationEntry();
                entry.setTopic("ActiveMQ.Advisory.>");
                entry.setRead("guests,users");
                entry.setWrite("guests,users");
                entry.setAdmin("guests,users");
                authorizationEntries.add(entry);

                TempDestinationAuthorizationEntry tempEntry = new TempDestinationAuthorizationEntry();
                tempEntry.setRead("admins");
                tempEntry.setWrite("admins");
                tempEntry.setAdmin("admins");

                DefaultAuthorizationMap authorizationMap = new DefaultAuthorizationMap(authorizationEntries);
                authorizationMap.setTempDestinationAuthorizationEntry(tempEntry);
                AuthorizationPlugin authorizationPlugin = new AuthorizationPlugin(authorizationMap);

                return authorizationPlugin;
            }


            public Server() {
                try {
                    startBroker();
                    //AuthorizationMap
                    //AuthorizationPlugin authorizationPlugin
                    //This message broker is embedded
        /*
                    BrokerService broker = new BrokerService();
                    broker.setPersistent(false);
                    broker.setUseJmx(false);
                    broker.addConnector(messageBrokerUrl);
                    MyAuthenticationPlugin[] myAuthenticationPlugin = new MyAuthenticationPlugin[1];
                    myAuthenticationPlugin[0] = new MyAuthenticationPlugin();
                    broker.setPlugins(myAuthenticationPlugin);
                    broker.start();
        */
                } catch (Exception e) {
                    System.out.println("Exception: "+e.getMessage());
                }
                this.setupMessageQueueConsumer();
            }

            private void setupMessageQueueConsumer() {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
                connectionFactory.setUserName("system");
                connectionFactory.setPassword("manager");
                connectionFactory.setExclusiveConsumer(true);


                Connection connection;
                try {
                    //connection = connectionFactory.createConnection(username, password);
                    connection = connectionFactory.createConnection();
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
                    System.out.println(e.getStackTrace().toString());
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