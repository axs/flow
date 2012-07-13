

/*
  $Id$
*/


package com.prc.tt;

import quickfix.field.AggregatedBook;
import quickfix.field.ApplQueueAction;
import quickfix.field.ApplQueueMax;
import quickfix.field.MarketDepth;
import quickfix.field.MDImplicitDelete;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.NoTradingSessions;
import quickfix.field.OpenCloseSettlFlag ;
import quickfix.field.Scope;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.MDEntryType;
import quickfix.field.SecurityExchange;
import quickfix.field.MsgType;

import quickfix.Group;
import quickfix.fix44.MarketDataRequest;
import quickfix.Message;
import quickfix.fix44.MessageFactory;

import java.util.List;


public class MarketDataRequestBuilder {
    MarketDataRequest mdr;
         public MarketDataRequestBuilder() {
         this.mdr = new MarketDataRequest();
    }



    public MarketDataRequestBuilder withAggregatedBook(AggregatedBook value) {
        this.mdr.setField(value);
        return this;
    }

    public MarketDataRequestBuilder withApplQueueAction(ApplQueueAction value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withApplQueueMax(ApplQueueMax value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withMarketDepth(MarketDepth value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withMDImplicitDelete(MDImplicitDelete value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withMDReqID(MDReqID value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withMDUpdateType(MDUpdateType value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withNoMDEntryTypes(NoMDEntryTypes value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withNoRelatedSym(NoRelatedSym value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withNoTradingSessions(NoTradingSessions value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withOpenCloseSettlFlag(OpenCloseSettlFlag  value) {
        this.mdr.setField(value);
        return this;

    }

    public MarketDataRequestBuilder withScope(Scope value) {
        this.mdr.setField(value);
        return this;
    }

    public MarketDataRequestBuilder withSubscriptionRequestType(SubscriptionRequestType value) {
        this.mdr.setField(value);
        return this;

    }


    public MarketDataRequest create() {
        return this.mdr;
    }
         

}
