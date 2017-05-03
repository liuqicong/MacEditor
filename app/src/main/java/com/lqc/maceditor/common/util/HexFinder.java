package com.lqc.maceditor.common.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HexFinder {

    /**
     * 以十六进制查看二进制文件
     */
    public static String format(byte[] bt) {
        int line = 0;
        StringBuilder buf = new StringBuilder();
        for (byte d : bt) {
            if (line % 16 == 0){
                buf.append(String.format("%05x: ", line));
            }
            buf.append(String.format("%02x  ", d));
            line++;
            if (line % 16 == 0){
                buf.append("\n");
            }
        }
        buf.append("\n");
        return buf.toString();
    }

    public static byte[] readFile(String file) throws IOException {
        InputStream is = new FileInputStream(file);
        int length = is.available();
        byte bt[] = new byte[length];
        is.read(bt);
        return bt;
    }


    public static byte[] fromHexString(String s) {
        int line=0;
        while(true){
            String start=String.format("%05x: ", line);
            if(s.contains(start)){
                s=s.replace(start, "");
                ++line;
            }else{
                break;
            }
        }
        s=s.replace("\n", "");
        s=s.replace(" ", "");
        s=s.replace(":", "");
        //Log.e("", s);

        int stringLength = s.length();
        if ((stringLength & 0x1) != 0) {
            throw new IllegalArgumentException(
                    "fromHexString requires an even number of hex characters");
        }
        byte[] bytes = new byte[stringLength / 2];

        for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
            int high = charToNibble(s.charAt(i));
            int low = charToNibble(s.charAt(i + 1));
            // You can store either unsigned 0..255 or signed -128..127 bytes in
            // a byte type.
            bytes[j] = (byte) ((high << 4) | low);
        }
        return bytes;
    }

    // -------------------------- STATIC METHODS --------------------------
    private static byte[] correspondingNibble = new byte['f' + 1];
    static {
        // only 0..9 A..F a..f have meaning. rest are errors.
        for (int i = 0; i <= 'f'; i++) {
            correspondingNibble[i] = -1;
        }
        for (int i = '0'; i <= '9'; i++) {
            correspondingNibble[i] = (byte) (i - '0');
        }
        for (int i = 'A'; i <= 'F'; i++) {
            correspondingNibble[i] = (byte) (i - 'A' + 10);
        }
        for (int i = 'a'; i <= 'f'; i++) {
            correspondingNibble[i] = (byte) (i - 'a' + 10);
        }
    }

    /**
     * convert a single char to corresponding nibble using a precalculated
     * array. Based on code by: Brian Marquis Orion Group Software Engineers
     * http://www.ogse.com
     *
     * @param c
     *            char to convert. must be 0-9 a-f A-F, no spaces, plus or minus
     *            signs.
     *
     * @return corresponding integer 0..15
     * @throws IllegalArgumentException
     *             on invalid c.
     */
    private static int charToNibble(char c) {
        if (c > 'f') {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
        int nibble = correspondingNibble[c];
        if (nibble < 0) {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
        return nibble;
    }

    /**
     * code not used, for explanation only. convert a single char to
     * corresponding nibble. Slow version, easier to understand.
     *
     * @param c
     *            char to convert. must be 0-9 a-f A-F, no spaces, plus or minus
     *            signs.
     *
     * @return corresponding integer
     */
    private static int slowCharToNibble(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 0xa;
        } else if ('A' <= c && c <= 'F') {
            return c - 'A' + 0xa;
        } else {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
    }


    public static void main(String[] agrs) throws IOException {
        //read file
        byte[] bt = HexFinder.readFile("c:\\1.rar");
        String hexData = HexFinder.format(bt);
        System.out.println(hexData);

        //save file
        String hexString = "FFD8FFE000104A46494606070C08070707070F";
        byte[] bytes = fromHexString(hexString);
        try {
            OutputStream os = new FileOutputStream("");
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
