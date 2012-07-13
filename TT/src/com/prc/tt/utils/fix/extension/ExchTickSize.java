



package com.prc.tt.utils.fix.extension;

import quickfix.DoubleField;



public class ExchTickSize extends DoubleField {


    public ExchTickSize() {
        super(16552);
    }

    public ExchTickSize(double data) {
        super(16552, data);
    }
}
