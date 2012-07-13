



package com.prc.tt.utils.fix.extension;

import quickfix.StringField;



public class MDEntrySizeType extends StringField {

    public MDEntrySizeType() {
        super(16487);
    }

    public MDEntrySizeType(String data) {
        super(16487, data);
    }
}
