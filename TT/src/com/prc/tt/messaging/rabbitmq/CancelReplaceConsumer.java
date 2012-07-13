/*
    $Id$
*/



package com.prc.tt.messaging.rabbitmq;


import quickfix.Message;
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



public class CancelReplaceConsumer extends OrderConsumer {
    private static final Log log = LogFactory.getLog(CancelReplaceConsumer.class);


    public CancelReplaceConsumer(Connection conn,String exchange, String routingkey, ExchangeType exype) throws IOException{
           super(  conn, exchange,  routingkey,  exype );

       }
    /*
    public CancelReplaceConsumer(String user, String passwd, String vhost, String ipaddress, int port
                         ,String exchange, String routingkey, ExchangeType exype) throws IOException{
        super(user, passwd, vhost,ipaddress, port, exchange,  routingkey,  exype );
    }
    */

    @Override
    public void consume() throws IOException{
        log.info("consume started");
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

            Order obj = jsonReader.fromJson(new String(delivery.getBody()), Order.class);
            om.add(obj);
            //System.out.println(obj2.getPrice());


            try {
                //Message msg = fixfactory.newCancelReplaceShares(obj.getorderID(),obj.getorigOrderID(),obj.getOrderQty());
                Message msg = fixfactory.newCancelReplace(obj);
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

}

