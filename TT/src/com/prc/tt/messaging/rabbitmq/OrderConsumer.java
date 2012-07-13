

/*
    $Id$
*/



package com.prc.tt.messaging.rabbitmq;


import quickfix.fix42.NewOrderSingle;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Order;
import com.prc.tt.BaseApplication;
import com.prc.tt.Configuration;
import com.prc.tt.Instrument;
import com.prc.tt.utils.fix.FIXMessageFactory;

import java.io.IOException;



public class OrderConsumer extends Consumer {
    private static final Log log = LogFactory.getLog(OrderConsumer.class);
    protected BaseApplication app;
    protected com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
    protected final FIXMessageFactory fixfactory;


    public OrderConsumer(Connection conn,String exchange, String routingkey, ExchangeType exype) throws IOException{
           super(  conn, exchange,  routingkey,  exype );

           fixfactory = new FIXMessageFactory();
       }


    /*
    public OrderConsumer(String user, String passwd, String vhost, String ipaddress, int port
                         ,String exchange, String routingkey, ExchangeType exype) throws IOException{
        super(  user,  passwd, vhost,ipaddress, port
                , exchange,  routingkey,  exype );

        fixfactory = new FIXMessageFactory();
    }
      */

    @Override
    public void consume() throws IOException{
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

            Order obj2 = jsonReader.fromJson(new String(delivery.getBody()), Order.class);
            //System.out.println(obj2.getPrice());
            om.add(obj2);

            try {
                NewOrderSingle msg = fixfactory.newLMTworder42(obj2);
                app.send(msg,app.getTradingSessionID());
            }
            catch ( Exception ert ) {
                ert.printStackTrace();
            }

            //log.info("Message received" + new String(delivery.getBody()));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        channel.close();
        conn.close();
    }

    public void setTradingApp(BaseApplication app) {
        this.app =app;
    }
}

