
/*
    $Id$
*/


package com.prc.tt.messaging.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;
import com.google.gson.Gson;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Quote;
import com.prc.tt.messaging.Trade;
import com.prc.tt.messaging.Execution;
import com.prc.tt.messaging.Order;


public class Publisher {
    ConnectionFactory factory;
    final String exchangeName;
    final String routingKey;
    Connection conn;
    final Channel channel;
    final private Gson jsonWriter;


    public Publisher(Connection conn
                     ,String exchange, String notused, ExchangeType exype) throws IOException{

        jsonWriter=new Gson();
        conn = conn;
        channel = conn.createChannel();

        exchangeName = exchange;
        routingKey = notused;
       // channel.exchangeDelete("QUOTES");
        channel.exchangeDeclare(exchangeName, exype.getValue() );
    }


    public Publisher(String user, String passwd, String vhost, String ipaddress, int port
                     ,String exchange, String routingkey, ExchangeType exype) throws IOException{

        jsonWriter=new Gson();
        factory = new ConnectionFactory();
        factory.setUsername(user);
        factory.setPassword(passwd);
        factory.setVirtualHost(vhost);
        factory.setHost(ipaddress);
        factory.setPort(port);


        conn = factory.newConnection();
        channel = conn.createChannel();

        exchangeName = exchange;
        routingKey = routingkey;

        channel.exchangeDeclare(exchangeName, exype.getValue() );
    }


    public void publish(String message) throws IOException {
        byte[] messageBodyBytes = message.getBytes();
        channel.basicPublish(exchangeName, "" ,MessageProperties.BASIC, messageBodyBytes) ;
    }

    public void publish(String message,String rKey) throws IOException {
        byte[] messageBodyBytes = message.getBytes();
        channel.basicPublish(exchangeName, rKey ,MessageProperties.BASIC, messageBodyBytes) ;
    }

    public void publish(Quote quote) throws IOException{
        publish(jsonWriter.toJson(quote));
    }
    public void publish(Quote quote,String rkey) throws IOException{
        publish(jsonWriter.toJson(quote) ,rkey);
    }

    public void publish(Trade trade,String rkey) throws IOException{
        publish(jsonWriter.toJson(trade) ,rkey);
    }


    public void publish(Execution execution,String rKey) throws IOException{
        publish(jsonWriter.toJson(execution),rKey);
    }

    public void publish(Order order) throws IOException{
        publish(jsonWriter.toJson(order));
    }


    public void close() {
        try {
            channel.close();
            conn.close();
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
}
