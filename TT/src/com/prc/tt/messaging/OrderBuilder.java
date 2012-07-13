

/*
  $Id$


*/


package com.prc.tt.messaging;

import quickfix.SessionID;
import quickfix.field.Side;
import quickfix.field.OrdType;
import quickfix.field.TimeInForce;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.Symbol;
import quickfix.field.MsgType;
import quickfix.field.TransactTime;
import quickfix.field.OrderQty;
import quickfix.field.Symbol;
import quickfix.field.Price;
import quickfix.field.OrderQty;
import quickfix.fix42.NewOrderSingle;

import com.prc.tt.Instrument;
import java.util.Date;




public class OrderBuilder {
    Order order;

    public OrderBuilder() {
        this.order = new Order();
    }

    public OrderBuilder withTIF(char t) {
        this.order.setTIF(t);

        return this;
    }

    public OrderBuilder withSecurityExchange(String s) {
        this.order.setSecurityExchange(s);

        return this;
    }

    public OrderBuilder withMsgType(String s) {
        this.order.setMsgType(s);

        return this;
    }



    public OrderBuilder withSide(char s) {
        this.order.setSide(s);

        return this;
    }

    public OrderBuilder withOrdType(char ot) {
        this.order.setOrdType(ot);

        return this;
    }


    public OrderBuilder withSymbol(String sym) {
        this.order.setSymbol(sym);

        return this;
    }

    //so we know the client that sent this order
    public OrderBuilder withSourceID(String sym) {
        this.order.setSourceID(sym);

        return this;
    }


    public OrderBuilder withPrice(double pr) {
        this.order.setPrice(pr);

        return this;
    }


    public OrderBuilder withOrderQty(double qty) {
        this.order.setOrderQty(qty);

        return this;
    }

    public OrderBuilder withSecurityID(String secid) {
        this.order.setSecurityID(secid);

        return this;
    }


    public OrderBuilder withOrigClOrdID(String cid) {
        this.order.setOrigClOrdID(cid);
        return this;
    }
    public OrderBuilder withClOrdID(String cid) {
        this.order.setClOrdID(cid);
        return this;
    }
    public OrderBuilder withOrderID(String cid) {
        this.order.setOrderID(cid);
        return this;
    }

    //add the instrument block in one gulp
    public OrderBuilder withInstrument(Instrument myinstrument) {
        this.order.setSymbol(myinstrument.getSymbol().getValue() );
        this.order.setSecurityExchange(myinstrument.getSecurityExchange().getValue() );
        this.order.setSecurityID(myinstrument.getSecurityID().getValue() );
        return this;
    }



    public Order create() {
        return this.order;
    }


    public static Order createCxlReplace(String origclordid,String sourceid,Instrument instru, double price, double quantity, char side){
        Order cxlord = new OrderBuilder()
                       .withOrigClOrdID( origclordid )
                       .withPrice(price)
                       .withOrderQty(quantity)
                       .withSide(side)
                       .withOrdType(OrdType.LIMIT)
                       .withSourceID( sourceid )
                       .withMsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST)
                       .withInstrument(instru)
                       .create();

        return cxlord;
    }


    public static Order createCancel(String origclordid,String sourceid,Instrument instru ) {
        Order zcxlord = new OrderBuilder()
                        .withOrigClOrdID(origclordid)
                        .withOrdType(OrdType.LIMIT)
                        .withSourceID(sourceid)
                        .withMsgType(MsgType.ORDER_CANCEL_REQUEST)
                        .withInstrument(instru)
                        .create();
        return zcxlord;
    }

    public static quickfix.fix42.NewOrderSingle newOrderSingle42(Order o) {


        quickfix.fix42.NewOrderSingle newOrderSingle = new quickfix.fix42.NewOrderSingle(
                                                                                        new ClOrdID( o.getClOrdID() )
                                                                                        ,new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC)
                                                                                        ,new Symbol(o.getSymbol())
                                                                                        ,new Side(o.getSide())
                                                                                        ,new TransactTime( new Date() )
                                                                                        ,new OrdType(o.getOrdType())
                                                                                        );
        newOrderSingle.set( new OrderQty(o.getOrderQty() ));
        newOrderSingle.set( new Price(o.getPrice()) );

        return newOrderSingle;
        //send(populateOrder(order, newOrderSingle), order.getSessionID());
    }

}
