

package com.prc.tt.messaging;


import quickfix.fix42.ExecutionReport;
import quickfix.fix42.OrderCancelReject;
import quickfix.FieldNotFound;
import quickfix.field.LastQty;
import quickfix.field.MsgType;


import com.prc.tt.utils.fix.extension.SecurityAltID;
import com.prc.tt.utils.DateUtils;



public class Execution extends Message{
    private String orderid;
    private String account;
    private String execid;
    private String clordid;
    private String origclordid;
    private int ordrejreason;
    private double lastshares;
    private char ordstatus;
    private char exectype;
    private String maturitymonthyear;
    private double leavesqty;
    private double cumqty;
    private double price;
    private double stoppx;
    private double orderqty;
    private double lastqty;
    private char side;
    private char ordtype;
    private char openclose;
    private double lastpx;
    private String msgtype;
    private String transacttime;
    private String securityaltid;
    private String securityexchange;
    private String securityid;
    private String symbol;




    public Execution() {
    }



    public Execution(OrderCancelReject ocr) {

        msgtype = ocr.MSGTYPE;

        try {
            orderid = ocr.getOrderID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            clordid = ocr.getClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }

        try {
            origclordid = ocr.getOrigClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            origclordid = ocr.getOrigClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
         //   fnf.printStackTrace();
        }
                 try {
            ordstatus = ocr.getOrdStatus().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            transacttime =DateUtils.dateToString( ocr.getTransactTime().getValue() );
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }

    }

    public Execution(ExecutionReport exreport) {

        msgtype = exreport.MSGTYPE;

        try {
            orderid = exreport.getOrderID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }

        try {
            lastqty = exreport.getField( new LastQty() ).getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            account = exreport.getAccount().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            execid = exreport.getExecID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            clordid = exreport.getClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }

        try {
            origclordid = exreport.getOrigClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            origclordid = exreport.getOrigClOrdID().getValue();
        }
        catch ( FieldNotFound fnf ) {
         //   fnf.printStackTrace();
        }
        try {
            ordrejreason = exreport.getOrdRejReason().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }
        try {
            lastshares = exreport.getLastShares().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }
        try {
            ordstatus = exreport.getOrdStatus().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }
        try {
            exectype = exreport.getExecType().getValue();
        }
        catch ( FieldNotFound fnf ) {
            // fnf.printStackTrace();
        }


        try {
            maturitymonthyear = exreport.getMaturityMonthYear().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }

        try {
            leavesqty = exreport.getLeavesQty().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }

        try {
            cumqty = exreport.getCumQty().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }



        try {
            price = exreport.getPrice().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            stoppx = exreport.getStopPx().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
           orderqty= exreport.getOrderQty().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }



        try {
           side= exreport.getSide().getValue();
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }



        try {
           ordtype= exreport.getOrdType().getValue();
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }



        try {
            openclose =exreport.getOpenClose().getValue();
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }



        try {
           lastpx= exreport.getLastPx().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
            transacttime =DateUtils.dateToString( exreport.getTransactTime().getValue() );
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }


        try {
            securityaltid = exreport.getField(new SecurityAltID()).getValue();
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }


        try {
           securityexchange= exreport.getSecurityExchange().getValue() ;
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }


        try {
           securityid= exreport.getSecurityID().getValue();
        }
        catch ( FieldNotFound fnf ) {
          //  fnf.printStackTrace();
        }


        try {
            symbol = exreport.getSymbol().getValue();
        }
        catch ( FieldNotFound fnf ) {
           // fnf.printStackTrace();
        }
    }

    public String getMsgType(){
        return msgtype;
    }

    public double getLastQty() {
        return lastqty;
    }



    public String getAccount() {
        return account;
    }

    public String getOrderID() {
        return orderid;
    }

    public String getExecID() {
        return execid;
    }


    public String getClOrdID() {
        return clordid;
    }

    public String getOrigClOrdID() {
        return origclordid;
    }

    public int getOrdRejReason() {
        return ordrejreason;
    }

    public double getLastShares() {
        return lastshares;
    }

    public char getOrdStatus() {
        return ordstatus;
    }

    public char getExecType() {
        return exectype;
    }

    public String getMaturityMonthYear() {
        return maturitymonthyear;
    }


    public double getLeavesQty() {
        return leavesqty;
    }

    public double getCumQty() {
        return cumqty;
    }

    public double getPrice() {
        return price;
    }

    public double getStopPx() {
        return stoppx;
    }

    public double getOrderQty() {
        return orderqty;
    }

    public char getSide() {
        return side;
    }

    public char getOrdType() {
        return ordtype;
    }

    public char getOpenClose() {
        return openclose;
    }

    public double getLastPx() {
        return lastpx;
    }

    public String getTransactTime() {
        return transacttime;
    }


    public String getSecurityAltID() {
        return securityaltid;
    }

    public String getSecurityExchange() {
        return securityexchange;
    }

    public String getSecurityID() {
        return securityid;
    }

    public String getSymbol() {
        return symbol;
    }

}
