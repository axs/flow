
/*
    $Id$
*/

package com.prc.tt;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.configuration.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import quickfix.field.Symbol;



public class Configuration {

    private static final Log mylog = LogFactory.getLog(Configuration.class);
    private static final Configuration INSTANCE  = new Configuration();
    private XMLConfiguration config;

    private HashMap<String,Instrument> instruments =new HashMap<String,Instrument>();

    private static String password;
    private static String account;
    private static String fixconf;
    private static String msguser;
    private static String msgpassword;
    private static String msgvhost    ;
    private static String msgipaddress;
    private static int msgport;
    private static String sourceid = "generic";


    private Configuration() {
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public static String getSourceID() {
        return sourceid;
    }
    public static void setSourceID(String sid) {
        sourceid=sid;
    }

    public static String getPassword() {
        return password;
    }
    public static void setPassword(String passwd) {
        password=passwd;
    }


    public static void setAccount(String acct) {
        account=acct;
    }

    public static String getAccount() {
        return account;
    }

    public static void setFIXconf(String fixc) {
        fixconf=fixc;
    }

    public static String getFIXconf() {
        return fixconf;
    }


    public static String getMsgUser() {
        return msguser;
    }
    public static String getMsgPassword() {
        return msgpassword;
    }
    public static String getMsgVhost() {
        return msgvhost;
    }
    public static String getMsgIPaddress() {
        return msgipaddress;
    }
    public static int getMsgPort() {
        return msgport;
    }


    public Instrument getInstrument(String securityid) {
        if ( instruments.containsKey(securityid) ) {
            return instruments.get(securityid);
        }
        return null;
    }


    public ArrayList<Symbol> getSymbols() {
        ArrayList<Symbol> symbols = new ArrayList<Symbol>();

        for ( Instrument i : instruments.values() ) {
            symbols.add(i.getSymbol());
        }

        return symbols;
    }


    public ArrayList<Instrument> getInstruments() {

        return new ArrayList<Instrument>( instruments.values() );

    }


    /*
     * XML configuration
     */
    public void subcribeReader(String conffile) {
        try {
            config = new XMLConfiguration(conffile);

            account = config.getString("account");
            mylog.info("Account: " + account);

            fixconf = config.getString("fixconf");
            mylog.info("fixconf: " + fixconf);

            password = config.getString("password");


            msguser     = config.getString("messaging.user");
            msgpassword = config.getString("messaging.password");
            msgvhost      = config.getString("messaging.vhost");
            msgipaddress = config.getString("messaging.ipaddress");
            msgport      = config.getInt("messaging.port");
            mylog.info("msguser: " + msguser);
            mylog.info("msgvhost: " + msgvhost);
            mylog.info("msgipaddress: " + msgipaddress);
            mylog.info("msgport: " + msgport);



            Object prop = config.getProperty("instruments.instrument.sid");

            if ( prop instanceof  Collection ) {

                mylog.info("Number of instruments: " + ((Collection) prop).size());

                for ( int i=0;i<((Collection) prop).size();i++ ) {
                    int sid = config.getInt("instruments.instrument("+i+").sid");
                    String symbol = config.getString("instruments.instrument("+i+").symbol");
                    String exchange = config.getString("instruments.instrument("+i+").exchange");
                    String securityid = config.getString("instruments.instrument("+i+").securityid");
                    String securitytype = config.getString("instruments.instrument("+i+").securitytype");
                    double exchticksize = config.getDouble("instruments.instrument("+i+").exchticksize");


                    mylog.info("symbol "+ symbol   +" UID "+sid + " tick "+ exchticksize);

                    Instrument instrument   = new Instrument(symbol,sid,exchange,securityid,exchticksize,securitytype);
                    instruments.put(securityid,instrument);
                }
            }
            else {

                int sid = config.getInt("instruments.instrument.sid");
                String symbol = config.getString("instruments.instrument.symbol");
                String exchange = config.getString("instruments.instrument.exchange");
                String securityid = config.getString("instruments.instrument.securityid");
                String securitytype = config.getString("instruments.instrument.securitytype");
                double exchticksize = config.getDouble("instruments.instrument.exchticksize");


                mylog.info("symbol "+ symbol +" UID "+sid + " tick "+ exchticksize);
                Instrument instrument   = new Instrument(symbol,sid,exchange,securityid,exchticksize,securitytype);
                instruments.put(securityid,instrument);
            }


        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }



}
