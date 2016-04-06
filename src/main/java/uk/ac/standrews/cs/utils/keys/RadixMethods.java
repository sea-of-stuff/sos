/*
 * Created on Jan 17, 2005 at 3:56:58 PM.
 */
package uk.ac.standrews.cs.utils.keys;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public class RadixMethods {

    private static final int HEX_BASE = 16;

    private static char[] lookup = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f',
            							   'g', 'h', 'i', 'j', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            							   'u', 'v', 'w', 'x', 'y', 'z' };

    public static int hexCharToInt( char c ) {
        return baseXCharToInt(c, HEX_BASE);
    }  
    
    public static int baseXCharToInt( char c, int base ) {
        for( int i = 0; i < lookup.length && i <= base; i++ ) {
            if( c == lookup[i]) {
                return i;
            }
        }
        return -1;       
    }

    /**
     * @param radix
     * @return number of bits needed to represent this radix
     */
    public static int bitsNeededTORepresent(int radix) {
        int result = 0;
        while( radix > 0 ) {
            radix = radix / 2;
            if( radix > 0 ) result++;
        }
        return result;
    }
}
