package team2485.smartdashboard.extension.util;

import java.io.UnsupportedEncodingException;

/**
 * Base 64 Encoder
 * Copied from http://www.wikihow.com/Encode-a-String-to-Base64-With-Java
 */
public class Base64Encoder {
    private Base64Encoder() {}

    private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static byte[] zeroPad(int length, byte[] bytes) {
        byte[] padded = new byte[length]; // initialized to zero by JVM
        System.arraycopy(bytes, 0, padded, 0, bytes.length);
        return padded;
    }

    public static String encode(String string) {
        String encoded = "";
        byte[] stringArray;
        try {
            stringArray = string.getBytes("UTF-8");  // use appropriate encoding string!
        } catch (UnsupportedEncodingException e) {
            stringArray = string.getBytes();  // use locale default rather than croak
        }

        // determine how many padding bytes to add to the output
        int paddingCount = (3 - (stringArray.length % 3)) % 3;
        // add any necessary padding to the input
        stringArray = zeroPad(stringArray.length + paddingCount, stringArray);
        // process 3 bytes at a time, churning out 4 output bytes
        // worry about CRLF insertions later
        for (int i = 0; i < stringArray.length; i += 3) {
            int j = ((stringArray[i] & 0xff) << 16) +
                ((stringArray[i + 1] & 0xff) << 8) +
                (stringArray[i + 2] & 0xff);
            encoded = encoded + BASE64_CHARS.charAt((j >> 18) & 0x3f) +
                BASE64_CHARS.charAt((j >> 12) & 0x3f) +
                BASE64_CHARS.charAt((j >> 6) & 0x3f) +
                BASE64_CHARS.charAt(j & 0x3f);
        }

        // replace encoded padding nulls with "="
        return encoded.substring(0, encoded.length() - paddingCount) + "==".substring(0, paddingCount);
    }
}
