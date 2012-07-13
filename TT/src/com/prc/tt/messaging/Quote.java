


/*

$Id$
*/




package com.prc.tt.messaging;




public class Quote extends Message {

    private String securityIdstr=null;
    private double bid  = 0.0;
    private double ask  = 0.0;
    private double bidsize = 0;
    private double offersize = 0;
    private String sendingtime=null;
    private String symbol=null;

    public Quote() {
    }


    public Quote( Quote other ) {
        this.symbol = other.getSymbol();
        this.securityIdstr = other.getSecurityID();
        this.sendingtime    = other.getSendingTime();
        this.bid        = other.getBid();
        this.ask        = other.getOffer();
        this.offersize  = other.getOfferSize();
        this.bidsize    = other.getBidSize();
    }




    public void setBid(double price) {
        this.bid = price;
    }

    public void setOffer(double price) {
        this.ask = price;
    }

    public double getBid() {
        return bid;
    }

    public double getOffer() {
        return ask;
    }


    /*
    public double getBidNotional(){
        return bid * instrument.pointValue;
    }
    public double getOfferNotional(){
        return ask * instrument.pointValue;
    }
      public double getMidNotional(){
        return getMid() * instrument.pointValue;
    }
    */

    public double getBidSize() {
        return bidsize;
    }

    public void setBidSize(double bsz) {
        bidsize=bsz;
    }

    public double getOfferSize() {
        return offersize;
    }

    public void setOfferSize(double osz) {
        offersize=osz;
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
        if ( obj instanceof Quote ) {
            Quote qt = (Quote) obj;
            return qt.getBid()== this.getBid()  &&
            qt.getBidSize()==this.getBidSize()  &&
            qt.getOfferSize()== this.getOfferSize()  &&
            qt.getOffer()==this.getOffer() ;
        }
        else {
            return false;
        }
    }


}

