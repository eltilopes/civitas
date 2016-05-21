package br.com.civitas.arquitetura.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Digest {
    public static String MD5digest(String val) {

        String crip = null;
        try {
            if (val == null) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(val.getBytes()));
            crip = hash.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crip;
    }
    
    public static String SHAdigest(String val) {
        String crip = null;
        try {
            if (val == null) {
                return null;
            }
            MessageDigest md = MessageDigest.getInstance("SHA");
            BigInteger hash = new BigInteger(1, md.digest(val.getBytes()));
            crip = hash.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crip;
    }
}
