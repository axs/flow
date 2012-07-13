/*
$Id$
*/


package com.prc.tt;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;

import com.prc.tt.messaging.Quote;



public class MarketWindow {
    private static final Log log = LogFactory.getLog(MarketWindow.class);
    private static final MarketWindow INSTANCE  = new MarketWindow();

    private ConcurrentHashMap<String,Quote> quotes = new ConcurrentHashMap<String,Quote>();

    private MarketWindow() {
    }

    public static MarketWindow getInstance() {
        return INSTANCE;
    }


    public void addQuote(Quote quote) {
        //log.info("adding quote " + quote.getSecurityID() );
        quotes.put(quote.getSecurityID(),quote);
    }

    public Quote getQuote(String secid) {
        if ( quotes.containsKey(secid ) ) {

            return quotes.get(secid);
        }
        return null;
    }
}
