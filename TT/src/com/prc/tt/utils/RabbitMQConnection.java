/*
    $Id$
*/


package com.prc.tt.utils;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.Configuration;

import java.io.IOException;





public class RabbitMQConnection {
    private final static Log log = LogFactory.getLog(RabbitMQConnection.class);
    private static final RabbitMQConnection INSTANCE  = new RabbitMQConnection();
    private Connection conn;


    private RabbitMQConnection() {
        Configuration cfg = com.prc.tt.Configuration.getInstance();

        log.info("rabbitmq connection " + cfg.getMsgUser() + " " +cfg.getMsgVhost());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(cfg.getMsgUser());
        factory.setPassword(cfg.getMsgPassword());
        factory.setVirtualHost(cfg.getMsgVhost());
        factory.setHost(cfg.getMsgIPaddress());
        factory.setPort(cfg.getMsgPort());
        try {
            conn = factory.newConnection();
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return INSTANCE.conn;
    }
}
