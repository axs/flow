

package com.prc.tt.utils.fix.extension;

import quickfix.StringField;



public class TTAccountType extends StringField {
    static public String FIRST_AGENT_ACCT = "A1";
    static public String CUSTOMER_ALLOCATION = "A2";
    static public String SYSTEM_ALLOCATION = "A3";
    static public String PREDESIG_GIVEUP = "G1";
    static public String DESIG_GIVEUP = "G2";
    static public String GIVEUP_SYSALLOCATION = "G3";
    static public String FIRST_MARKETMAKER = "M1";
    static public String SECOND_MARKETMAKER = "M2";
    static public String FIRST_PRINCIPAL_ACCT = "P1";
    static public String SECOND_PRINCIPAL_ACCT = "P1";
    static public String HOUSE = "P3";
    static public String UNALOOCATED = "U1";
    static public String UNALOOCATED_AUTOMATIC = "U2";
    static public String UNALOOCATED_SYS = "U3";


    public TTAccountType() {
        super(18205);
    }

    public TTAccountType(String data) {
        super(18205, data);
    }
}
