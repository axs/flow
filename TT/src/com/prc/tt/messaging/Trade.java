/*

$Id$
*/




package com.prc.tt.messaging;




public class Trade extends Message {

    private String securityIdstr=null;
    private double price  = 0.0;
    private double size = 0;
    private String sendingtime=null;
    private String symbol=null;
    private int aggressorside=69;

    public Trade() {
    }


    public Trade( Trade other ) {
        this.symbol = other.getSymbol();
        this.securityIdstr = other.getSecurityID();
        this.sendingtime    = other.getSendingTime();
        this.price        = other.getPrice();
        this.size    = other.getSize();
        this.aggressorside = other.aggressorside;
    }


    public void setAggressorSide(int ags) {
        this.aggressorside = ags;
    }
    public int getAggressorSide() {
        return this.aggressorside;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(double sz) {
        this.size = sz;
    }

    public double getPrice() {
        return price;
    }

    public double getSize() {
        return size;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String s) {
        symbol=s;
    }



    public String getSecurityID() {
        return securityIdstr;
    }

    public String getSendingTime() {
        //return instrument.contract.m_conId;
        return sendingtime;
    }

    public void setSecurityID(String secid) {
        this.securityIdstr=secid;
    }

    public void setSendingTime(String st) {
        this.sendingtime= st;
    }



    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj instanceof Trade ) {
            Trade qt = (Trade) obj;
            return qt.getPrice()== this.getPrice()  &&
            qt.getSize()==this.getSize() ;
        }
        else {
            return false;
        }
    }


}

