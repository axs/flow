
/*
    $Id$
*/


package com.prc.tt;

import quickfix.field.Symbol;
import quickfix.field.SecurityExchange;
import quickfix.field.SecurityID;
import quickfix.field.SecurityType;
import com.prc.tt.utils.fix.extension.ExchTickSize;


public class Instrument {
    final private Symbol symbol;
    final private SecurityID  securityID;
    final private SecurityExchange exchange;
    final private ExchTickSize exchticksize;
    private SecurityType securitytype;
    final private int id;


    public Instrument(String sym,int id,String exchange,String securityID ,double exchticksize ) {
        this.symbol = new Symbol(sym);
        this.id=id;
        this.exchange = new SecurityExchange(exchange);
        this.securityID = new SecurityID(securityID);
        this.exchticksize = new ExchTickSize(exchticksize);
        //XXX  hardcoded for now
        this.securitytype = new SecurityType(SecurityType.FUTURE);
    }


    public Instrument(String sym,int id,String exchange,String securityID ,double exchticksize,String securitytype ) {
            this.symbol = new Symbol(sym);
            this.id=id;
            this.exchange = new SecurityExchange(exchange);
            this.securityID = new SecurityID(securityID);
            this.exchticksize = new ExchTickSize(exchticksize);
            if( securitytype.equals("MLEG") ){
                this.securitytype = new SecurityType(SecurityType.MULTI_LEG_INSTRUMENT);
            }
            else{
                this.securitytype = new SecurityType(SecurityType.FUTURE);
            }
        }


    public Symbol getSymbol() {
        return this.symbol;
    }

    public SecurityID getSecurityID() {
        return this.securityID;
    }
    public SecurityExchange getSecurityExchange() {
        return this.exchange;
    }

    public ExchTickSize getExchTickSize(){
        return exchticksize;
    }

    public SecurityType getSecurityType(){
        return securitytype;
    }


    public int getID() {
        return this.id;
    }



    }
