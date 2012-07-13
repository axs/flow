

package com.prc.tt.messaging;


import quickfix.field.OrdStatus;
import quickfix.field.TimeInForce;

import com.prc.tt.IDGenerator;


public class Order extends Message {
    private String sessionID = null;
    private String symbol;
    private double quantity;
    private int open = 0;
    private int executed = 0;
    private char side;
    private char type;
    private char tif = TimeInForce.DAY;
    private double limit = 0.0;
    private double stop = 0.0;
    private double avgPx = 0.0;
    private double price ;
    private boolean rejected = false;
    private boolean canceled = false;
    private boolean isNew = true;
    private String message = null;
    private String ID = null;
    private String originalID = null;
    private String secid = null;
    private String securityexchange=null;
    private String  clordid=null;
    private String msgtype=null;
    private String orderid=null;
    private String origclordid=null;
    private char ordstatus;

    public Order() {
        //ID = IDGenerator.getID("Balh");
        clordid= IDGenerator.getID();
        ordstatus=OrdStatus.NEW;
    }

    public void setOrdStatus(char ostatus){
        this.ordstatus = ostatus;
    }
    public char getOrdStatus(){
        return this.ordstatus;
    }


    public String getMsgType(){
        return msgtype;
    }

    public void setMsgType(String mt){
        //either MsgType.ORDER_CANCEL_REPLACE_REQUEST or MsgType.ORDER_CANCEL_REQUEST
        msgtype=mt;
    }


    public void setOrderID(String OrderID){
        this.orderid=OrderID;
    }
    public void setOrigClOrdID(String OrigClOrdID){
        this.origclordid=OrigClOrdID;
    }

       public String  getOrderID(){
           return  this.orderid;
       }
       public String  getOrigClOrdID(){
           return  this.origclordid;
       }





     public String getClOrdID(){
         return clordid;
     }
     public void setClOrdID(String cid){
          clordid=cid;
     }


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getOrderQty() {
        return quantity;
    }
    public void setOrderQty(double quantity) {
        this.quantity = quantity;
    }
    public int getOpen() {
        return open;
    }
    public void setOpen(int open) {
        this.open = open;
    }
    public int getExecuted() {
        return executed;
    }
    public void setExecuted(int executed) {
        this.executed = executed;
    }

    public char getSide() {
        return side;
    }
    public void setSide(char side) {
        this.side = side;
    }

    public char getOrdType() {
        return type;
    }
    public void setOrdType(char type) {
        this.type = type;
    }
    public char getTIF() {
        return tif;
    }
    public void setTIF(char tif) {
        this.tif = tif;
    }



    public void setPrice(double avgPx) {
        this.price = avgPx;
    }
    public double getPrice() {
        return price;
    }


    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }
    public boolean getRejected() {
        return rejected;
    }
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
    public boolean getCanceled() {
        return canceled;
    }
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
    public boolean isNew() {
        return isNew;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }


    public String getSecurityID(){
        return this.secid;
    }
    public void setSecurityID(String ds){
        this.secid=ds;
    }


    public String     getSecurityExchange(){
        return this.securityexchange;
    }
    public void setSecurityExchange(String ds){
        this.securityexchange=ds;
    }



}
