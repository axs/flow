

package com.prc.tt;


import quickfix.Session;
import java.util.ArrayList;
import quickfix.field.Symbol;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.Side;
import quickfix.field.OrdStatus;
import quickfix.FieldNotFound;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.IncorrectTagValue;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.NewOrderSingle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import com.prc.tt.BaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Instrument;
import com.prc.tt.utils.RabbitMQConnection;
import com.prc.tt.messaging.rabbitmq.Publisher;
import com.prc.tt.messaging.rabbitmq.CancelReplaceConsumer;
import com.prc.tt.messaging.rabbitmq.OrderConsumer;
import com.prc.tt.messaging.rabbitmq.ExchangeType;
import com.prc.tt.messaging.Execution;
import com.prc.tt.messaging.OrderRoutes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;




public class Gateway extends BaseApplication {
    private final static Log log = LogFactory.getLog(Gateway.class);

    private com.prc.tt.Configuration cfg;
    private final FIXMessageFactory fixfactory;
    private Publisher publisher;
    private final static ExecutorService exec =Executors.newCachedThreadPool();
    private final Connection conn;

    final private OrderRoutes om = OrderRoutes.getInstance();

    public Gateway(String f) throws Exception{
        super(f);

        fixfactory = new FIXMessageFactory();


        conn =RabbitMQConnection.getConnection();

        publisher = new Publisher(conn,"EXECUTIONS", "notused",ExchangeType.DIRECT);
    }



    @Override
    public void onMessage( quickfix.fix42.ExecutionReport exreport, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        Execution execution = new Execution(exreport);
        String wheretosend = om.getSourceID( execution.getClOrdID() );
        /*
        if( execution.getOrdStatus() == OrdStatus.FILLED ||
            execution.getOrdStatus() == OrdStatus.REPLACED ||
            execution.getOrdStatus() == OrdStatus.CANCELED ){
            om.remove( execution.getClOrdID() );
        }
        */

        if( execution.getOrdStatus() == OrdStatus.FILLED ||
            execution.getOrdStatus() == OrdStatus.PARTIALLY_FILLED ||
            execution.getOrdStatus() == OrdStatus.REPLACED ||
            execution.getOrdStatus() == OrdStatus.CANCELED ||
            execution.getOrdStatus() == OrdStatus.REJECTED ||
            execution.getOrdStatus() == OrdStatus.PENDING_CANCEL
            ){
            try {
                log.info("publishing execution " + execution.getClOrdID()  +" to " + wheretosend);
                //routingkey is to client who sent order
                publisher.publish(execution,wheretosend);
            }
            catch ( IOException ioexe ) {
                ioexe.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage( quickfix.fix42.OrderCancelReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        Execution execution = new Execution(message);
        String wheretosend = om.getSourceID( execution.getClOrdID() );

        if( execution.getOrdStatus() == OrdStatus.FILLED ||
            execution.getOrdStatus() == OrdStatus.PARTIALLY_FILLED ||
            execution.getOrdStatus() == OrdStatus.REPLACED ||
            execution.getOrdStatus() == OrdStatus.CANCELED ||
            execution.getOrdStatus() == OrdStatus.REJECTED ||
            execution.getOrdStatus() == OrdStatus.PENDING_CANCEL
            ){
            try {
                log.info("publishing OrderCancelReject " + execution.getClOrdID()  +" to " + wheretosend);
                //routingkey is to client who sent order
                publisher.publish(execution,wheretosend);
            }
            catch ( IOException ioexe ) {
                ioexe.printStackTrace();
            }
        }
    }



    private void pause() {
        try {
            Thread.sleep(5000);
        }
        catch ( Exception sdf ) {
            sdf.printStackTrace();
        }
    }



    public void startConsumer() {
        try {
            OrderConsumer oconsumer= new OrderConsumer(conn,"ORDERS", "ORDERS", ExchangeType.FANOUT);
            exec.submit( new ConsumersThread(oconsumer,this) );
        }
        catch ( Exception ert ) {
            ert.printStackTrace();
        }


        try {
            CancelReplaceConsumer cxlconsumer= new CancelReplaceConsumer(conn,"CXL", "CXL", ExchangeType.FANOUT);
            exec.submit( new ConsumersThread(cxlconsumer,this) );
        }
        catch ( Exception pwi ) {
            pwi.printStackTrace();
        }
    }

    class ConsumersThread implements Runnable {
        OrderConsumer consumer;
        Gateway tt;

        public ConsumersThread(OrderConsumer c,Gateway tt) {
            this.consumer = c;
            this.tt=tt;
        }

        public void run() {
            consumer.setTradingApp(tt);
            try {
                consumer.consume();
            }
            catch ( IOException err ) {
            }
        }
    }









}
