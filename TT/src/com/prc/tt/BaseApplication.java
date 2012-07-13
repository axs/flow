




package com.prc.tt;


import quickfix.*;
import quickfix.Session;
import quickfix.field.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.io.FileInputStream;

import com.prc.tt.utils.fix.MessagePrinter;

import quickfix.field.RawData;



public class BaseApplication  extends quickfix.fix42.MessageCracker implements quickfix.Application {
    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(BaseApplication.class);

    final MessagePrinter msgprinter;
    DataDictionary dd=null;
    DataDictionary ddfourtwo=null;
    final Initiator initiator;
    SessionID tradingsessionid=null;
    SessionID quotesessionid=null;
    final SessionSettings settings;
    private String passwd;


    public BaseApplication(String filename) throws Exception{

        settings = new SessionSettings(new FileInputStream(filename));
        initiator= initiate();

        //sessionid = createSession(filename);
        msgprinter=new MessagePrinter();


        try {
            ddfourtwo = new DataDictionary("C:/work/TT/conf/fix/TTFIX42.xml");
        }
        catch ( Exception ert ) {
            ert.printStackTrace();
        }
    }


    private Initiator initiate() throws Exception{
        //SessionSettings settings = new SessionSettings(new FileInputStream(fileName));
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        quickfix.LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        return  new SocketInitiator(this, storeFactory, settings, logFactory, messageFactory);
    }


    public boolean isLoggedOn() {
        return initiator.isLoggedOn();
    }

    public void start() throws RuntimeError, ConfigError{
        initiator.start();
    }

    public void stop() {
        initiator.stop();
    }

    public SessionID getTradingSessionID() {
        return tradingsessionid;
    }

    public SessionID getQuoteSessionID() {
        return quotesessionid;
    }


    public void send(Message message, SessionID s) throws SessionNotFound, FieldNotFound {
        // String msgType = message.getHeader().getString(MsgType.FIELD);
        //MsgType msgType = message.identifyType( message.toString() );


        /*
                     || msgType.equals(MsgType.ORDER_MASS_CANCEL_REQUEST)
             || msgType.equals(MsgType.ORDER_CANCEL_REPLACE_REQUEST)
             || msgType.equals(MsgType.ORDER_CANCEL_REQUEST)
             || msgType.equals(MsgType.MULTILEG_ORDER_CANCEL_REPLACE)

        */

        /*
        if ( msgType.equals(MsgType.ORDER_SINGLE)
             || msgType.equals(MsgType.NEW_ORDER_MULTILEG) ) {

            System.out.println( "add to ordermanager" );
        }
        */

        Session.sendToTarget(message, s );
    }


    public  void onCreate(SessionID aSessionID) {
        log.info("session created");
        try {
            if ( settings.getBool(aSessionID, "TradingSession") ) {
                tradingsessionid = aSessionID;
                log.info("TRADINGSESSION");
            }
            else if ( settings.getBool(aSessionID, "QuoteSession") ) {
                quotesessionid = aSessionID;
                log.info("QUOTESESSION");
            }

        }
        catch ( ConfigError configError ) {
            configError.printStackTrace();
        }
        catch ( FieldConvertError fieldConvertError ) {
            fieldConvertError.printStackTrace();
        }
    }


    public void onLogon(SessionID sessionId) {
        log.info("onLogon");
    }


    public void onLogout(SessionID sessionId) {
        log.info("OnLogout");
    }


    public void toAdmin(Message message, SessionID sessionId) {
        try {
            msgprinter.print(ddfourtwo,message);
        }
        catch ( Exception ert ) {
            ert.printStackTrace();
        }


        if ( isMessageOfType(message, MsgType.LOGON) ) {
            addLogonField(message);
        }
    }



    public void setPassword(String pass){
        this.passwd = pass;
    }


    public void toApp(Message message, SessionID sessionId)
    throws DoNotSend{
    }


    public void fromAdmin(Message message, SessionID sessionId)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon{
        try {
            crack(message, sessionId);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

    }


    public void fromApp(Message message, SessionID sessionId)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType{
        crack(message, sessionId);
    }

    private void addLogonField(Message message) {
        message.getHeader().setField(new RawData(this.passwd));
    }


    private boolean isMessageOfType(Message message, String type) {
        try {
            return type.equals(message.getHeader().getField(new MsgType()).getValue());
        }
        catch ( FieldNotFound e ) {
            log.info("isMessageOfType  error");
            //logErrorToSessionLog(message, e);
            return false;
        }
    }



    public void onMessage( quickfix.fix42.SequenceReset message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("onMessae SequenceReset");
        msgprinter.print(ddfourtwo,message);

    }



    public void onMessage( quickfix.fix42.MarketDataRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("onMessae MarketDataRequest");
        msgprinter.print(ddfourtwo,message);

    }

    public void onMessage( quickfix.fix42.MarketDataRequestReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);

    }



    public void onMessage( quickfix.fix42.MarketDataIncrementalRefresh message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }



    public void onMessage( quickfix.fix42.MarketDataSnapshotFullRefresh message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }


    public void onMessage( quickfix.fix42.SecurityDefinitionRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }

    public void onMessage( quickfix.fix42.SecurityDefinition message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }

    public void onMessage( quickfix.fix42.SecurityStatusRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }

    public void onMessage( quickfix.fix42.SecurityStatus message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }



    public void onMessage( quickfix.fix42.ExecutionReport execution, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,execution);

        //XXX make thread
        //om.handleExecution(execution);
    }


    public void onMessage( quickfix.fix42.NewOrderSingle order, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        //log.info( "add to ordermanager" );
        //om.add(order );
        msgprinter.print(ddfourtwo,order);
    }


    public void onMessage( quickfix.fix42.OrderCancelReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }

    public void onMessage( quickfix.fix42.OrderCancelReplaceRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }


    public void onMessage( quickfix.fix42.OrderCancelRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }

    public void onMessage( quickfix.fix42.OrderStatusRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        msgprinter.print(ddfourtwo,message);
    }


    public void onMessage( quickfix.fix42.BusinessMessageReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        log.info("message rej " + message.getBusinessRejectReason().toString() );
    }




}
