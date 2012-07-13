


/*
1m0z2n9x
*/



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

import com.prc.tt.BaseApplication;
import com.prc.tt.utils.fix.FIXMessageFactory;
import com.prc.tt.IDGenerator;
import com.prc.tt.Instrument;
import com.prc.tt.utils.RabbitMQConnection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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


public class TTtestOrdergetSend extends BaseApplication {

    private com.prc.tt.Configuration cfg;
    private SessionID msees;
    private SessionID osees;
    private final FIXMessageFactory fixfactory;
    private Publisher publisher;
    private final static ExecutorService exec =Executors.newCachedThreadPool();
    private final Connection conn;

    final private OrderRoutes om = OrderRoutes.getInstance();

    public TTtestOrdergetSend(String f) throws Exception{
        super(f);

        fixfactory = new FIXMessageFactory();

        /*

        cfg = com.prc.tt.Configuration.getInstance();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(cfg.getMsgUser());
        factory.setPassword(cfg.getMsgPassword());
        factory.setVirtualHost(cfg.getMsgVhost());
        factory.setHost(cfg.getMsgIPaddress());
        factory.setPort(cfg.getMsgPort());
        conn = factory.newConnection();
        */

        conn =RabbitMQConnection.getConnection();

        publisher = new Publisher(conn,"EXECUTIONS", "notused",ExchangeType.DIRECT);
    }


    @Override
    public void onMessage( quickfix.fix42.ExecutionReport exreport, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {


        Execution execution = new Execution(exreport);
        String wheretosend = om.getSourceID( execution.getClOrdID() );
        if( execution.getOrdStatus() == OrdStatus.FILLED ||
            execution.getOrdStatus() == OrdStatus.REPLACED ||
            execution.getOrdStatus() == OrdStatus.CANCELED ){
            om.remove( execution.getClOrdID() );
        }

        try {
            //routingkey is to client who sent order
            publisher.publish(execution,wheretosend);
        }
        catch ( IOException ioexe ) {
            ioexe.printStackTrace();
        }
    }


    public void sendOrder() {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = null;
        try {

            mdr = fixfactory.newLMTworder42(IDGenerator.getID(getTradingSessionID())
                                            ,instru.get(0),10232,1,new Side(Side.BUY) );
        }
        catch ( FieldNotFound ert ) {
            ert.printStackTrace();
        }

        try {
            send(mdr, getTradingSessionID());
        }
        catch ( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch ( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }


        //cancelMessage(mdr);
        //cancelReplacePrice((NewOrderSingle)mdr);
        cancelReplaceQty( (NewOrderSingle)mdr );
    }


    public void cancelMessage(Message mdr) {
        pause();

        Message cxlmsg=null;
        try {
            cxlmsg = fixfactory.newCancelFromMessage(IDGenerator.getID(getTradingSessionID()),mdr);
        }
        catch ( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(cxlmsg, getTradingSessionID());
        }
        catch ( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch ( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
        //=================   cxl here after 5 sec
    }



    private void pause() {
        try {
            Thread.sleep(5000);
        }
        catch ( Exception sdf ) {
            sdf.printStackTrace();
        }
    }



    public void cancelReplaceQty(NewOrderSingle mdr) {
        pause();

        Message crpmsg=null;
        try {

            crpmsg = fixfactory.newCancelReplaceQty( IDGenerator.getID(getTradingSessionID())
                                                     ,mdr
                                                     ,2 );
        }
        catch ( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch ( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch ( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }



    public void cancelReplacePrice(NewOrderSingle mdr) {
        pause();

        Message crpmsg=null;
        try {
            crpmsg = fixfactory.newCancelReplacePrice( IDGenerator.getID(getTradingSessionID())
                                                       ,mdr
                                                       ,mdr.getPrice().getValue() -13 );
        }
        catch ( Exception rtr ) {
            rtr.printStackTrace();
        }

        try {
            send(crpmsg, getTradingSessionID());
        }
        catch ( SessionNotFound  e ) {
            e.printStackTrace();
        }
        catch ( FieldNotFound ferr ) {
            ferr.printStackTrace();
        }
    }





    public void getMktData() {
        ArrayList<Instrument> instru =cfg.getInstruments();

        Message mdr = fixfactory.newMarketStreamRequest(IDGenerator.getID(getQuoteSessionID()),instru );

        try {
            send(mdr, getQuoteSessionID());
        }
        catch ( SessionNotFound e ) {
            e.printStackTrace();
        }
        catch ( FieldNotFound ferr ) {
            ferr.printStackTrace();
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
        TTtestOrdergetSend tt;
        public ConsumersThread(OrderConsumer c,TTtestOrdergetSend tt) {
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



    public static void main(String args[]) throws Exception {
        com.prc.tt.Configuration cf =com.prc.tt.Configuration.getInstance() ;
        cf.subcribeReader(args[0]);


        TTtestOrdergetSend test = new TTtestOrdergetSend( cf.getFIXconf() );
        test.start();


        while( ! test.isLoggedOn() ){
            try {
                System.out.println("Waiting for logon");
                Thread.sleep(5000);
            }
            catch ( Exception ere ) {
                ere.printStackTrace();
            }
        }


        test.startConsumer();



        System.out.println("here");

        // test.getMktData();
        //test.sendOrder();

        System.in.read();
    }





}
