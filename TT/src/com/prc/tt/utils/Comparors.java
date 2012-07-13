
/*
 $Id$
*/

package com.prc.tt.utils;


import java.util.Comparator;
import com.prc.tt.messaging.Order;




public class Comparors{

    //highest is first
    public static final Comparator<Order> HIGH_PRICE_ORDERS =
    new Comparator<Order>() {
                        public int compare(Order e1, Order e2) {
                            double diff= e2.getPrice() - e1.getPrice() ;
                            return diff<0 ? -1:1;
                        }
    };


    public static final Comparator<Order> LOW_PRICE_ORDERS =
    new Comparator<Order>() {
                        public int compare(Order e1, Order e2) {
                            double diff= e2.getPrice() - e1.getPrice() ;
                            return diff<0 ? 1:-1;
                        }
    };

    /*
    //highest is first
    public static final Comparator<Order> HIGH_ORDERIDS =
    new Comparator<Order>() {
                        public int compare(Order e1, Order e2) {
                            double diff= e2.m_orderId - e1.m_orderId ;
                            return diff<0 ? -1:1;
                        }
    };


    public static final Comparator<Order> LOW_ORDERIDS =
    new Comparator<Order>() {
                        public int compare(Order e1, Order e2) {
                            double diff= e2.m_orderId - e1.m_orderId ;
                            return diff<0 ? 1:-1;
                        }
    };
    */


}

