

/*
    $Id$
*/


package com.prc.tt.utils.fix;



import quickfix.Field;
import quickfix.FieldType;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.FieldMap;
import quickfix.Group;
import quickfix.field.MsgType;

import java.util.Iterator;


import org.apache.commons.configuration.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*
  public class MetadataExample {
    public static void main(String[] args) throws Exception {
        DataDictionary dd = new DataDictionary("FIX44.xml");

        Message m = new Message("8=FIX.4.4\0019=247\00135=s\00134=5\001"
                + "49=sender\00152=20060319-09:08:20.881\001"
                + "56=target\00122=8\00140=2\00144=9\00148=ABC\00155=ABC\001"
                + "60=20060319-09:08:19\001548=184214\001549=2\001"
                + "550=0\001552=2\00154=1\001453=2\001448=8\001447=D\001"
                + "452=4\001448=AAA35777\001447=D\001452=3\00138=9\00154=2\001"
                + "453=2\001448=8\001447=D\001452=4\001448=aaa\001447=D\001"
                + "452=3\00138=9\00110=056\001", dd);

        MessagePrinter printer = new MessagePrinter();
        printer.print(System.out, dd, m);
    }
}

*/

public class MessagePrinter {
    private static final Log log = LogFactory.getLog(MessagePrinter.class);

    public void print(DataDictionary dd, Message message) throws FieldNotFound {
        String msgType = message.getHeader().getString(MsgType.FIELD);
        printFieldMap("", dd, msgType, message.getHeader());
        printFieldMap("", dd, msgType, message);
        printFieldMap("", dd, msgType, message.getTrailer());
    }

    private void printFieldMap(String prefix, DataDictionary dd, String msgType, FieldMap fieldMap)
    throws FieldNotFound {
        StringBuilder sb = new StringBuilder();

        Iterator fieldIterator = fieldMap.iterator();
        while ( fieldIterator.hasNext() ) {
            Field field = (Field) fieldIterator.next();
            if ( !isGroupCountField(dd, field) ) {
                String value = fieldMap.getString(field.getTag());
                if ( dd.hasFieldValue(field.getTag()) ) {
                    value = dd.getValueName(field.getTag(), fieldMap.getString(field.getTag())) + " (" + value + ")";
                }
                //System.out.println(prefix + dd.getFieldName(field.getTag()) + ": " + value);
                sb.append(prefix + dd.getFieldName(field.getTag()) + ": " + value + "\n");
            }
        }
        System.out.print(sb.toString());
        sb.setLength( 0 );

        Iterator groupsKeys = fieldMap.groupKeyIterator();
        while ( groupsKeys.hasNext() ) {
            int groupCountTag = ((Integer) groupsKeys.next()).intValue();
            System.out.println(prefix + dd.getFieldName(groupCountTag) + ": count = "+ fieldMap.getInt(groupCountTag));
            Group g = new Group(groupCountTag, 0);
            int i = 1;
            while ( fieldMap.hasGroup(i, groupCountTag) ) {
                if ( i > 1 ) {
                    System.out.println(prefix + "  ----");
                }
                fieldMap.getGroup(i, g);
                printFieldMap(prefix + "  ", dd, msgType, g);
                i++;
            }
        }
    }

    private boolean isGroupCountField(DataDictionary dd, Field field) {
        return dd.getFieldTypeEnum(field.getTag()) == FieldType.NumInGroup;
    }

}

