



package com.prc.tt.utils.fix.extension;

import quickfix.IntField;



public class AggressorSide extends IntField {

    public AggressorSide() {
        super(16488);
    }

    public AggressorSide(int data) {
        super(16488, data);
    }
}
