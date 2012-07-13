

package com.prc.tt.utils.fix;

import quickfix.ConfigError;
import quickfix.DataDictionary;

import java.io.InputStream;


public class FIXDataDictionary {
    public static final String FIX_SYSTEM_BEGIN_STRING = "FIX.0.0"; //$NON-NLS-1$
    public static final String FIX_4_0_BEGIN_STRING = "FIX.4.0"; //$NON-NLS-1$
    public static final String FIX_4_1_BEGIN_STRING = "FIX.4.1"; //$NON-NLS-1$
    public static final String FIX_4_2_BEGIN_STRING = "FIX.4.2"; //$NON-NLS-1$
    public static final String FIX_4_3_BEGIN_STRING = "FIX.4.3"; //$NON-NLS-1$
    public static final String FIX_4_4_BEGIN_STRING = "FIX.4.4"; //$NON-NLS-1$

    private final DataDictionary mDictionary;

    public FIXDataDictionary(DataDictionary dictionary) {
        mDictionary=dictionary;
    }

    public FIXDataDictionary(String fixDataDictionaryPath) throws Exception
    {
        DataDictionary theDict;
        try {
            theDict = new DataDictionary(fixDataDictionaryPath);
        }
        catch ( DataDictionary.Exception ddex ) {
            InputStream input = FIXDataDictionary.class.getClassLoader().getResourceAsStream(fixDataDictionaryPath);
            try {
                theDict = new DataDictionary(input);
            }
            catch ( ConfigError configError1 ) {
                throw new Exception(ddex);
            }
        }
        catch ( ConfigError configError ) {
            throw new Exception(configError);
        }

        mDictionary = theDict;
    }

    public String getHumanFieldName(int fieldNumber) {
        return mDictionary.getFieldName(fieldNumber);
    }

    public String getHumanFieldValue(int fieldNumber, String value) {
        String result = mDictionary.getValueName(fieldNumber, value);
        return(result == null) ? result : result.replace('_', ' ');
    }

    public DataDictionary getDictionary() {
        return mDictionary;
    }

    public static FIXDataDictionary initializeDataDictionary(String fixDataDictionaryPath) throws Exception
    {
        return new FIXDataDictionary(fixDataDictionaryPath);
    }
}

