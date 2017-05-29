package ru.protei.portal.ui.common.client.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public MD5Utils() {}

    public static String getHash( String theNeedsHash ) {
        if ( theNeedsHash == null ) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance( "MD5" );
        } catch ( NoSuchAlgorithmException ex ) {
            throw new UnsupportedOperationException( ex.getMessage() );
        }
        byte[] b = md5.digest( theNeedsHash.getBytes() );
        char[] ConvMap = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for ( int i = 0; i < b.length; i++ ) {
            int nHigh = ( b[i] & 0xF0 ) >>> 4;
            int nLow = b[i] & 0x0F;
            sb.append( ConvMap[nHigh] );
            sb.append( ConvMap[nLow] );
        }
        return sb.toString();
    }
}
