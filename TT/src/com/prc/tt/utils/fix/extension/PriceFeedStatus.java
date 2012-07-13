




package com.prc.tt.utils.fix.extension;

import quickfix.IntField;



public class PriceFeedStatus extends IntField {

    public PriceFeedStatus() {
        super(18210);
    }

    public PriceFeedStatus(int data) {
        super(18210, data);
    }
}
