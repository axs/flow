


/*
  $Id$

  file:///C:/code/marketcetera/docs/html/_f_i_x_message_factory_8java_source.html#l00253
*/


package com.prc.tt.utils.fix;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
//import java.lang.IllegalArgumentException;

import quickfix.field.AggregatedBook;
import quickfix.field.ApplQueueAction;
import quickfix.field.Account;
import quickfix.field.AccountType;
import quickfix.field.ApplQueueMax;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.MarketDepth;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MDEntryType;
import quickfix.field.MDImplicitDelete;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.NoTradingSessions;
import quickfix.field.OpenCloseSettlFlag ;
import quickfix.field.OrderQty;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.Scope;
import quickfix.field.SecondaryClOrdID;
import quickfix.field.SecurityExchange;
import quickfix.field.SecurityID;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StopPx;
import quickfix.StringField;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.FieldNotFound;
import quickfix.fix42.NewOrderSingle;
import quickfix.field.SendingTime;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MessageFactory;
import quickfix.Group;
import quickfix.Message;
import quickfix.SessionID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.prc.tt.utils.fix.extension.TTUsername;
import com.prc.tt.utils.fix.extension.TTAccountType;
import com.prc.tt.utils.fix.CustomField;
import com.prc.tt.messaging.Order;
import com.prc.tt.Instrument;
import com.prc.tt.Configuration;
import com.prc.tt.IDGenerator;





public class FIXMessageFactory {
    private static final Log log = LogFactory.getLog(FIXMessageFactory.class);

    private MessageFactory msgFactory;
    private final String beginString;
    private static final char SOH_CHAR = '\001';
    private final com.prc.tt.Configuration cfg;

    public FIXMessageFactory() {
        this.beginString = "FIX.4.2";


        cfg = com.prc.tt.Configuration.getInstance();
        msgFactory = new MessageFactory();
    }


    public FIXMessageFactory(final SessionID sessid) {
        this.beginString=sessid.getBeginString();


        cfg = com.prc.tt.Configuration.getInstance();
        msgFactory = new MessageFactory();
    }




    public Message newCancelReplace( Order order) {
        log.info("newCancelReplace called");
        Message aMessage = msgFactory.create(beginString,order.getMsgType() );
        // addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(order.getClOrdID()));
        aMessage.setField(new OrigClOrdID( order.getOrigClOrdID() ));
        aMessage.setField(new Price(order.getPrice()));
        aMessage.setField(new Symbol(order.getSymbol()));
        aMessage.setField(new SecurityID(order.getSecurityID()));
        aMessage.setField(new SecurityExchange(order.getSecurityExchange()));
        aMessage.setField(new OrderQty(order.getOrderQty()));
        aMessage.setField(new OrdType(order.getOrdType()));
        aMessage.setField(new Side(order.getSide()));

        aMessage.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );

        addHandlingInst(aMessage);
        return aMessage;
    }



    public Message newOrderCancelReject() {
        Message msg = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REJECT);

        return msg;
    }

    public Message newCancelReplaceShares(String orderID, String origOrderID, double quantity) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        //addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField(new OrigClOrdID(origOrderID));
        aMessage.setField(new OrderQty(quantity));
        addHandlingInst(aMessage);
        return aMessage;
    }

    protected void addHandlingInst(Message inMessage) {
        inMessage.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
    }


    public Message newCancelReplaceQty(
                                      String orderID,
                                      NewOrderSingle oldorder,
                                      int newqty
                                      ) throws FieldNotFound{
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        // addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField( new OrigClOrdID( oldorder.getClOrdID().getValue() ));
        aMessage.setField(oldorder.getPrice());

        aMessage.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );
        aMessage.setField( oldorder.getSymbol() );
        aMessage.setField( oldorder.getSecurityID() );
        aMessage.setField( oldorder.getSecurityExchange() );
        aMessage.setField( new OrderQty(newqty) );
        aMessage.setField( oldorder.getSide() );
        aMessage.setField( oldorder.getOrdType() );

        addHandlingInst(aMessage);
        return aMessage;
    }



    public Message newCancelReplacePrice(
                                        String orderID,
                                        NewOrderSingle oldorder,
                                        double price
                                        ) throws FieldNotFound{
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        // addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField( new OrigClOrdID( oldorder.getClOrdID().getValue() ));
        aMessage.setField(new Price(price));

        aMessage.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );
        aMessage.setField( oldorder.getSymbol() );
        aMessage.setField( oldorder.getSecurityID() );
        aMessage.setField( oldorder.getSecurityExchange() );
        aMessage.setField( oldorder.getOrderQty() );
        aMessage.setField( oldorder.getSide() );
        aMessage.setField( oldorder.getOrdType() );

        addHandlingInst(aMessage);
        return aMessage;
    }



    public Message newCancelReplacePrice(
                                        String orderID,
                                        String origOrderID,
                                        double price
                                        ) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        // addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField(new OrigClOrdID(origOrderID));
        aMessage.setField(new Price(price));

        aMessage.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );

        addHandlingInst(aMessage);
        return aMessage;
    }




    public Message newCancelFromMessage(String reqId, Message oldMessage) throws FieldNotFound {
        return newCancelHelper(reqId,MsgType.ORDER_CANCEL_REQUEST, oldMessage, false);
    }

    public Message newCancelReplaceFromMessage(Message oldMessage) throws FieldNotFound {
        Message cancelMessage = newCancelHelper(MsgType.ORDER_CANCEL_REPLACE_REQUEST, oldMessage, false);
        if( oldMessage.isSetField(Price.FIELD) ) {
            cancelMessage.setField(oldMessage.getField(new Price()));
        }
        addHandlingInst(cancelMessage);
        return cancelMessage;
    }


    public Message newCancelHelper(String reqid, String msgType, Message oldMessage, boolean onlyCopyRequiredFields) throws FieldNotFound {
        Message cancelMessage = msgFactory.create(beginString, msgType);
        cancelMessage.setField(new OrigClOrdID(oldMessage.getString(ClOrdID.FIELD)));
        fillFieldsFromExistingMessage(oldMessage, onlyCopyRequiredFields, cancelMessage);
        if( oldMessage.isSetField(OrderQty.FIELD) ) {
            cancelMessage.setField(oldMessage.getField(new OrderQty()));
        }
        //addTransactionTimeIfNeeded(cancelMessage);
        addSendingTime(cancelMessage);
        cancelMessage.setField(new ClOrdID(reqid));
        return cancelMessage;
    }


    public Message newCancelHelper(String msgType, Message oldMessage, boolean onlyCopyRequiredFields) throws FieldNotFound {
        Message cancelMessage = msgFactory.create(beginString, msgType);
        cancelMessage.setField(new OrigClOrdID(oldMessage.getString(ClOrdID.FIELD)));
        fillFieldsFromExistingMessage(oldMessage, onlyCopyRequiredFields, cancelMessage);
        if( oldMessage.isSetField(OrderQty.FIELD) ) {
            cancelMessage.setField(oldMessage.getField(new OrderQty()));
        }
        //addTransactionTimeIfNeeded(cancelMessage);
        addSendingTime(cancelMessage);
        return cancelMessage;
    }

    protected void addSendingTime(Message inCancelMessage) {
        inCancelMessage.getHeader().setField(new SendingTime(new Date())); //non-i18n
    }


    protected void fillFieldsFromExistingMessage(Message oldMessage,
                                                 boolean onlyCopyRequiredFields,
                                                 Message inCancelMessage) {
        FIXMessageUtil.fillFieldsFromExistingMessage(inCancelMessage,
                                                     oldMessage, onlyCopyRequiredFields);
    }



    private final int TOP_OF_BOOK_DEPTH = 1;
    public Message newMarketDataRequest(String reqID,
                                        List<Instrument> symbols,
                                        String inExchange) {
        Message request = msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST);
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new AggregatedBook(true) );
        request.setField(new MDReqID(reqID));
        request.setChar(SubscriptionRequestType.FIELD, SubscriptionRequestType.SNAPSHOT);
        Group entryTypeGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoMDEntryTypes.FIELD);

        entryTypeGroup.setField(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        int numSymbols = symbols.size();
        if( numSymbols == 0 ) {
            request.setInt(NoRelatedSym.FIELD, numSymbols);
        }
        for( Instrument oneSymbol : symbols ) {
            if( oneSymbol != null ) {
                Group symbolGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
                //symbolGroup.setField(new SecurityType("FUT") );
                //symbolGroup.setField(new MaturityMonthYear("201104") );
                symbolGroup.setField( oneSymbol.getSymbol() );
                symbolGroup.setField(oneSymbol.getSecurityExchange() );
                symbolGroup.setField(oneSymbol.getSecurityID() );

                if( inExchange != null &&
                    !inExchange.isEmpty() ) {
                    symbolGroup.setField(new SecurityExchange(inExchange));
                }
                request.addGroup(symbolGroup);
            }
        }
        return request;
    }


    public Message newMarketDataRequest(String reqID, List<Instrument> symbols) {
        return newMarketDataRequest(reqID,
                                    symbols,
                                    null);
    }



    //reqid is the id used when subscribing
    public  Message newCancelMarketStreamRequest(String reqID , List<Instrument> symbols) {
        log.info("newCxlMarketStreamRequest called");
        MarketDataRequest request = new MarketDataRequest();
        request.setField(new MDReqID(reqID));
        request.setField(new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
        request.setField(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        request.setInt(NoMDEntryTypes.FIELD, 2);

        MarketDataRequest.NoMDEntryTypes entryTypeGroup =  new MarketDataRequest.NoMDEntryTypes();

        entryTypeGroup.setField(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        request.setField(new NoRelatedSym( symbols.size() ));

        for( Instrument oneSymbol : symbols ) {
            MarketDataRequest.NoRelatedSym symbolGroup =  new MarketDataRequest.NoRelatedSym();
            symbolGroup.setField( oneSymbol.getSymbol() );
            symbolGroup.setField(oneSymbol.getSecurityExchange() );
            symbolGroup.setField(oneSymbol.getSecurityID() );

            request.addGroup(symbolGroup);
        }


        return request;
    }


    public  Message newTradeStreamRequest(String reqID, List<Instrument> symbols) {
        log.info("newTradeStreamRequest called");
        MarketDataRequest request = new MarketDataRequest();
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new MDReqID(reqID));
        request.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
        //request.setField(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        request.setField(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        request.setField(new AggregatedBook(true) );


        //symbol group
        request.setField(new NoRelatedSym( symbols.size() ));

        for( Instrument oneSymbol : symbols ) {
            MarketDataRequest.NoRelatedSym symbolGroup =  new MarketDataRequest.NoRelatedSym();
            symbolGroup.setField( oneSymbol.getSymbol() );
            symbolGroup.setField(oneSymbol.getSecurityExchange() );
            symbolGroup.setField(oneSymbol.getSecurityID() );
            //symbolGroup.setField(oneSymbol.getSecurityType() );
            request.addGroup(symbolGroup);
        }


        request.set( new NoMDEntryTypes(1) );

        MarketDataRequest.NoMDEntryTypes entryTypeGroup =  new MarketDataRequest.NoMDEntryTypes();
        entryTypeGroup.setField(new MDEntryType(MDEntryType.TRADE));
        request.addGroup(entryTypeGroup);



        return request;
    }



    public  Message newMarketStreamRequest(String reqID, List<Instrument> symbols) {
        log.info("newMarketStreamRequest called");
        MarketDataRequest request = new MarketDataRequest();
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new MDReqID(reqID));
        request.setField(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES));
        request.setField(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        //request.setField(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        request.setField(new AggregatedBook(true) );
        request.setInt(NoMDEntryTypes.FIELD, 2);


        MarketDataRequest.NoMDEntryTypes entryTypeGroup =  new MarketDataRequest.NoMDEntryTypes();


        entryTypeGroup.setField(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        request.setField(new NoRelatedSym( symbols.size() ));

        for( Instrument oneSymbol : symbols ) {
            MarketDataRequest.NoRelatedSym symbolGroup =  new MarketDataRequest.NoRelatedSym();
            symbolGroup.setField( oneSymbol.getSymbol() );
            symbolGroup.setField(oneSymbol.getSecurityExchange() );
            symbolGroup.setField(oneSymbol.getSecurityID() );
            symbolGroup.setField(oneSymbol.getSecurityType() );
            request.addGroup(symbolGroup);
        }

        return request;
    }




    public NewOrderSingle newLMTworder42(String reqId,Instrument instru, double price,int qty,Side side)
    throws FieldNotFound {
        NewOrderSingle order = new NewOrderSingle(new ClOrdID(reqId),
                                                  new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                  instru.getSymbol(),
                                                  side,
                                                  new TransactTime(),
                                                  new OrdType(OrdType.LIMIT));


        order.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );
        order.set(new Account( cfg.getAccount() ));
        order.set(new Price(price));
        order.set(new OrderQty(qty) );
        order.set(new TimeInForce(TimeInForce.DAY));
        order.set(instru.getSecurityID() );
        order.set(instru.getSecurityExchange() );

        return order;
    }


    public NewOrderSingle newLMTworder42(Order order)
    throws FieldNotFound {
        log.info("newLMTworder42 called");
        NewOrderSingle xorder = new NewOrderSingle(new ClOrdID( order.getClOrdID() ),
                                                   new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                   new Symbol(order.getSymbol() ),
                                                   new Side(order.getSide() ),
                                                   new TransactTime(),
                                                   new OrdType(order.getOrdType() ));


        xorder.setField( new TTAccountType( TTAccountType.FIRST_AGENT_ACCT ) );
        xorder.set(new Account( cfg.getAccount() ));
        xorder.set(new Price(order.getPrice() ));
        xorder.set(new OrderQty(order.getOrderQty() ) );
        xorder.set(new TimeInForce(order.getTIF()));
        xorder.set( new SecurityID( order.getSecurityID() ) );
        //order.set(instru.getSecurityExchange() );
        xorder.set( new SecurityExchange(order.getSecurityExchange() ) );

        return xorder;
    }



    public Message newSecurityListRequest(String inReqID) {
        Message request = msgFactory.create(beginString,
                                            MsgType.SECURITY_LIST_REQUEST);
        request.setField(new SecurityReqID(inReqID));
        request.setField(new SecurityListRequestType(SecurityListRequestType.SYMBOL));
        return request;
    }


    public Message newSecurityDefinitionRequest(String inReqID,String symbol, String securityType, String securityexchange) {
        Message request = msgFactory.create(beginString,MsgType.SECURITY_DEFINITION_REQUEST);
        request.setField(new SecurityReqID(inReqID));
        request.setField(new Symbol(symbol) );
        request.setField(new SecurityType(securityType) );
        request.setField( new SecurityExchange(securityexchange ) );
        return request;
    }




    public Message newMarketOrder( String clOrderID,
                                   char side,
                                   double quantity,
                                   Instrument instrument,
                                   char timeInForce,
                                   String account
                                 ) {
        Message newMessage = newOrderHelper(clOrderID, side, quantity, instrument, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.MARKET));
        return newMessage;
    }


    public Message newStopOrder( String clOrderID,
                                 char side,
                                 double quantity,
                                 Instrument instrument,
                                 char timeInForce,
                                 String account,
                                 double price
                               ) {
        Message newMessage = newOrderHelper(clOrderID, side, quantity, instrument, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.STOP));
        newMessage.setField(new StopPx(price));
        return newMessage;
    }


    public Message newStopLimitOrder( String clOrderID,
                                      char side,
                                      double quantity,
                                      Instrument instrument,
                                      char timeInForce,
                                      String account,
                                      double stopprice,
                                      double lmtprice
                                    ) throws IllegalArgumentException{
        if( side == Side.BUY && lmtprice<stopprice ) {
            throw new IllegalArgumentException("buy limit stop lmtprice must be higher than stop price");
        }
        else if( side == Side.SELL && lmtprice> stopprice ) {
            throw new IllegalArgumentException("Sell limit stop lmtprice must be lower than stop price");
        }
        Message newMessage = newOrderHelper(clOrderID, side, quantity, instrument, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.STOP_LIMIT));
        newMessage.setField(new StopPx(stopprice));
        newMessage.setField(new Price(lmtprice));
        return newMessage;
    }




    private Message newOrderHelper(String clOrderID, char side, double quantity,
                                   Instrument instrument, char timeInForce, String account) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        aMessage.setField(new ClOrdID(clOrderID));
        addHandlingInst(aMessage);
        //todo handle instruments other than equity.
        aMessage.setField(instrument.getSymbol());
        aMessage.setField(instrument.getSecurityID());
        aMessage.setField(instrument.getSecurityExchange());

        //  aMessage.setField(new SecurityType(instrument.getSecurityType().getFIXValue()));
        aMessage.setField(new Side(side));

        aMessage.setField(new OrderQty(quantity));
        aMessage.setField(new TimeInForce(timeInForce));
        if( account != null ) {
            aMessage.setField(new Account(account));
        }
        // addTransactionTimeIfNeeded(aMessage);
        return aMessage;
    }


}
