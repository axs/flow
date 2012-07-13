

package com.prc.tt;


import quickfix.*;
import quickfix.Session;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

import java.util.ArrayList;
import java.io.FileInputStream;

//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;

import com.prc.tt.utils.fix.MessagePrinter;

import quickfix.field.RawData;



public class TestBaseApplication  extends quickfix.MessageCracker implements quickfix.Application {
    final MessagePrinter msgprinter;
    DataDictionary dd=null;
    DataDictionary ddfourtwo=null;
    final Initiator initiator;
    SessionID tradingsessionid=null;
    SessionID quotesessionid=null;
    final SessionSettings settings;



    public TestBaseApplication(String filename) throws Exception{
        settings = new SessionSettings(new FileInputStream(filename));
        initiator= initiate();        

        //sessionid = createSession(filename);
        msgprinter=new MessagePrinter();
        try {
            dd = new DataDictionary("FIX44.xml");
        }
        catch ( Exception ert ) {
            ert.printStackTrace();
        }


        try {
            ddfourtwo = new DataDictionary("FIX42.xml");
        }
        catch ( Exception ert ) {
            ert.printStackTrace();
        }

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
        /*
        Iterator sectionIterator = settings.sectionIterator();
        while ( sectionIterator.hasNext() ) {
            SessionID id = sectionIterator.next();   
            System.out.println( id.toString() );
        }
         */

        /*
        if (sessionid ==null) {
            ArrayList<SessionID> sessions = initiator.getSessions(); 
            sessionid = sessions.get(0);
        }
         */
        return tradingsessionid;   
    }

    public SessionID getQuoteSessionID() {
        return quotesessionid;   
    }


    public void send(Message message, SessionID s) throws SessionNotFound, FieldNotFound {
        String msgType = message.getHeader().getString(MsgType.FIELD);
        //MsgType msgType = message.identifyType( message.toString() );


        /*
                     || msgType.equals(MsgType.ORDER_MASS_CANCEL_REQUEST)
             || msgType.equals(MsgType.ORDER_CANCEL_REPLACE_REQUEST)
             || msgType.equals(MsgType.ORDER_CANCEL_REQUEST)
             || msgType.equals(MsgType.MULTILEG_ORDER_CANCEL_REPLACE)

        */

        if ( msgType.equals(MsgType.ORDER_SINGLE)
             || msgType.equals(MsgType.NEW_ORDER_MULTILEG) ) {

            System.out.println( "add to ordermanager" );
        }

        Session.sendToTarget(message, s ); 
    }


    private Initiator initiate() throws Exception{
        //SessionSettings settings = new SessionSettings(new FileInputStream(fileName));        
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        return  new SocketInitiator(this, storeFactory, settings, logFactory, messageFactory);        
    }




    public  void onCreate(SessionID aSessionID) {
        System.out.println("session created");
        try {
            if ( settings.getBool(aSessionID, "TradingSession") ) {
                tradingsessionid = aSessionID;
                System.out.println("TRADINGSESSION");
            }
            else if ( settings.getBool(aSessionID, "QuoteSession") ) {
                quotesessionid = aSessionID;
                System.out.println("QUOTESESSION");
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
        System.out.println("onLogon");
    }


    public void onLogout(SessionID sessionId) {
        System.out.println("OnLogout");
    }


    public void toAdmin(Message message, SessionID sessionId) {
             }


    public void toApp(Message message, SessionID sessionId)
    throws DoNotSend{
          
       
    }


    public void fromAdmin(Message message, SessionID sessionId)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon{
        // System.out.println("fromADmin "  +  sessionId.toString() );
        try {
            crack(message, sessionId);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        // System.out.println( message.toString() );
    }


    public void fromApp(Message message, SessionID sessionId)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType{
        // System.out.println("fromApp "  +  sessionId.toString());

        crack(message, sessionId);
    }

     

    public void onMessage( quickfix.fix44.SecurityListRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae SecurityListRequest");
        msgprinter.print(dd,message);
    }


    public void onMessage( quickfix.fix44.BusinessMessageReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae BusinessMessageReject");
        msgprinter.print(dd,message);
    }


    public void onMessage( quickfix.fix44.QuoteResponse message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae qt res");
        msgprinter.print(dd,message);

    }


    public void onMessage( quickfix.fix44.QuoteRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae qtreq");
        msgprinter.print(dd,message);

    }


    public void onMessage( quickfix.fix44.Quote message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae qt");
        System.out.println( message.getQuoteID() );
        msgprinter.print(dd,message);

    }


    public void onMessage( quickfix.fix44.MarketDataRequest message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae MarketDataRequest");
        msgprinter.print(dd,message);

    }


    public void onMessage( quickfix.fix44.BidResponse message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae bidres");
    }

    public void onMessage( quickfix.fix44.MarketDataIncrementalRefresh message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae MarketDataIncrementalRefresh  called");
        //System.out.println( message.toString() );

        msgprinter.print(dd,message);
    }



    public void onMessage( quickfix.fix44.MarketDataSnapshotFullRefresh message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("onMessae MarketDataSnapshotFullRefresh");


        msgprinter.print(dd,message);

        /* XXX

        //NoMDEntries noMDEntries = new NoMDEntries();
        //message.get(noMDEntries);
        NoMDEntries nmd=  message.getNoMDEntries();


        quickfix.fix44.MarketDataSnapshotFullRefresh.NoMDEntries group =
        new quickfix.fix44.MarketDataSnapshotFullRefresh.NoMDEntries();

        MDEntryType MDEntryType = new MDEntryType();
        MDEntryPx MDEntryPx     = new MDEntryPx();
        MDEntrySize MDEntrySize = new MDEntrySize();

        for ( int k =1;k<=nmd.getValue();k++ ) {
            message.getGroup(k, group);            
            System.out.println( group.get(MDEntryPx).toString() + "~"+ group.get(MDEntrySize).toString());
        }

        // System.out.println(group.getValue() );
        message.getGroup(1, group);
        System.out.print("BID ");           

        //System.out.print(group.get(MDEntryType).getValue() );
        System.out.println( group.get(MDEntryPx).toString() );
        group.get(MDEntrySize);


        message.getGroup(2, group);
        System.out.print("ASK ");
//System.out.print(group.get(MDEntryType).toString() );
        System.out.println( group.get(MDEntryPx).toString() );
        group.get(MDEntrySize);


        message.getGroup(3, group);
        System.out.print("LAST ");
//System.out.print(group.get(MDEntryType).toString() );
        System.out.println( group.get(MDEntryPx).toString() );
        group.get(MDEntrySize);

        XXX */
    }


    public void onMessage( quickfix.fix42.ExecutionReport execution, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        System.out.println( "add to ordermanager" );

        msgprinter.print(ddfourtwo,execution);
    }


    public void onMessage( quickfix.fix42.BusinessMessageReject message, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        System.out.println("message rej " + message.getBusinessRejectReason().toString() );        
    }


    public void onMessage( quickfix.fix42.NewOrderSingle order, SessionID sessionID )
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        msgprinter.print(ddfourtwo,order);

        /*
        System.out.println("onMessage NewOrderSingle called");


        Symbol symbol = new Symbol();
        Side side = new Side();
        OrdType ordType = new OrdType();
        OrderQty orderQty = new OrderQty();
        Price price = new Price();
        ClOrdID clOrdID = new ClOrdID();
        Account account = new Account();

        order.get(ordType);

        if ( ordType.getValue() != OrdType.LIMIT )
            throw new IncorrectTagValue(ordType.getField());

        order.get(symbol);
        order.get(side);
        order.get(orderQty);
        order.get(price);
        order.get(clOrdID);

        quickfix.fix42.ExecutionReport executionReport = new quickfix.fix42.ExecutionReport
                                                         ( genOrderID(),
                                                           genExecID(),
                                                           new ExecTransType( ExecTransType.NEW ),
                                                           new ExecType     ( ExecType.FILL ),
                                                           new OrdStatus    ( OrdStatus.FILLED ),
                                                           symbol,
                                                           side,
                                                           new LeavesQty    ( 0 ),
                                                           new CumQty       ( orderQty.getValue() ),
                                                           new AvgPx        ( price.getValue() ));

        executionReport.set(clOrdID);
        executionReport.set(orderQty);
        executionReport.set(new LastShares(orderQty.getValue()));
        executionReport.set(new LastPx(price.getValue()));

        if ( executionReport.isSet(account) )
            executionReport.setField( executionReport.get(account) );

        System.out.println(executionReport.toString());


        try {
            Session.sendToTarget(executionReport, sessionID);
        }
        catch ( SessionNotFound e ) {
        }
        */
    }



}
