



package com.prc.tt.messaging.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Quote;
import com.prc.tt.messaging.Trade;
import com.prc.tt.messaging.Execution;
import com.prc.tt.messaging.Message;
import com.prc.tt.messaging.OrderRoutes;
import com.prc.tt.messaging.MessageType;
import com.prc.tt.messaging.MessageListener;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.io.IOException;


public class Consumer {
    private static final Log log = LogFactory.getLog(Consumer.class);

    protected Map<MessageType, Set<MessageListener<?>>> listeners = new HashMap<MessageType, Set<MessageListener<?>>>();
    final private String exchangeName;
    final private String routingKey;
    final protected Connection conn;
    final protected Channel channel;
    final protected String queueName;
    final protected Gson jsonReader;
    final protected boolean noAck = false; //autoacknowloedge messages
    final protected OrderRoutes om = OrderRoutes.getInstance();

    public Consumer(Connection connection,String exchange, String routingkey, ExchangeType exype) throws IOException{
        conn = connection;
        channel = conn.createChannel();
        exchangeName = exchange;
        routingKey = routingkey;
        boolean durable = false;
        channel.exchangeDeclare(exchangeName,exype.getValue(), durable);
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.basicQos(100);

        jsonReader=new Gson();
    }


    public Consumer() throws IOException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        conn = factory.newConnection();
        channel = conn.createChannel();
        exchangeName = "crackoil";
        queueName = "";
        routingKey = "testRoute";
        boolean durable = false;
        channel.exchangeDeclare(exchangeName, "fanout", durable);
        channel.queueDeclare(queueName, durable,false,false,null);
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.basicQos(100);

        jsonReader=new Gson();
    }


    //consumes Execution
    public void consume() throws IOException{
        log.info("Consumer consuming");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);

        boolean runInfinite = true;

        while( runInfinite ) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            }
            catch( InterruptedException ie ) {
                continue;
            }

            Execution obj2 = jsonReader.fromJson(new String(delivery.getBody()), Execution.class);

            //log.info("Message received" + new String(delivery.getBody()));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            notify(MessageType.EXECUTION,obj2);
        }
        channel.close();
        conn.close();
    }

    //consumes Quote
    public void consumeQuotes() throws IOException{
        log.info("Consumer consuming");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);

        boolean runInfinite = true;

        while( runInfinite ) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            }
            catch( InterruptedException ie ) {
                continue;
            }

            Quote obj2 = jsonReader.fromJson(new String(delivery.getBody()), Quote.class);

            //log.info("Message received" + new String(delivery.getBody()));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            notify(MessageType.QUOTE,obj2);
        }
        channel.close();
        conn.close();
    }



    //consumes Trades
    public void consumeTrades() throws IOException{
        log.info("Consumer consuming");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        //channel.basicConsume(queueName, noAck, consumer);
        channel.basicConsume(queueName, true, consumer);

        boolean runInfinite = true;

        while( runInfinite ) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            }
            catch( InterruptedException ie ) {
                continue;
            }

            Trade obj2 = jsonReader.fromJson(new String(delivery.getBody()), Trade.class);

            //log.info("Message received" + new String(delivery.getBody()));
            //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            notify(MessageType.TRADE,obj2);
        }
        channel.close();
        conn.close();
    }


    protected void notify(MessageType msgType, Message message ) {
        Set<MessageListener<?>> listenerSet = listeners.get(msgType);

        if( listenerSet != null && !listenerSet.isEmpty() ) {
            for( MessageListener messageListener : listenerSet ) {
                try {
                    messageListener.onMessage(msgType, message);
                }
                catch( Exception eee ) {
                    eee.printStackTrace();
                }
            }

            //log.info(toString() + " notified " + listenerSet.size() + " listeners");
        }
       // else {
            //log.info(toString() + " has no listeners for message type " + msgType);
       // }
    }


    /*
    protected void notify(MessageType msgType, Quote message ) {
        Set<MessageListener<?>> listenerSet = listeners.get(msgType);

        if ( listenerSet != null && !listenerSet.isEmpty() ) {
            for ( MessageListener messageListener : listenerSet ) {
                try {
                    messageListener.onMessage(msgType, message);
                }
                catch ( Exception eee ) {
                    eee.printStackTrace();
                }
            }

            if ( log.isDebugEnabled() )
                log.debug(toString() + " notified " + listenerSet.size() + " listeners");
        }
        else {
            if ( log.isDebugEnabled() )
                log.debug(toString() + " has no listeners for message type " + msgType);
        }
    }
*/

    public void addListener(MessageListener<?> listener, MessageType type) {
        Set<MessageListener<?>> listenerSet = listeners.get(type);
        if( listenerSet == null ) {
            listenerSet = new HashSet<MessageListener<?>>();

            listeners.put(type, listenerSet);
        }

        listenerSet.add(listener);
    }


    public void removeListener(MessageListener<?> listener, MessageType type) {
        Set<MessageListener<?>> listenerSet = listeners.get(type);
        if( listenerSet != null ) {
            listenerSet.remove(listener);
        }
    }


}

