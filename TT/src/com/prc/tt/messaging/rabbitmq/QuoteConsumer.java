/*
    $Id$
*/



package com.prc.tt.messaging.rabbitmq;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Quote;
import com.prc.tt.Configuration;
import com.prc.tt.Instrument;
import com.prc.tt.MarketWindow;

import java.io.IOException;



public class QuoteConsumer extends Consumer {
    private static final Log log = LogFactory.getLog(QuoteConsumer.class);
    protected com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
    private final MarketWindow marketwindow = MarketWindow.getInstance();

    public QuoteConsumer(Connection conn,String exchange, String routingkey, ExchangeType exype) throws IOException{
        super(  conn, exchange,  routingkey,  exype );
    }




    //consumes quotes
    @Override
    public void consume() throws IOException{
        log.info("QuoteConsumer consuming");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);

        boolean runInfinite = true;

        while ( runInfinite ) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            }
            catch ( InterruptedException ie ) {
                continue;
            }

            Quote obj2 = jsonReader.fromJson(new String(delivery.getBody()), Quote.class);
            marketwindow.addQuote(obj2);

            //log.info("deserialezed message " + obj2.getBid() + "  "+ obj2.getSecurityID() );
            //log.info("Message received" + new String(delivery.getBody()));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        channel.close();
        conn.close();
    }

}

