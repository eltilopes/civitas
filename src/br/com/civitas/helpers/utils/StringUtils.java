package br.com.civitas.helpers.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.Charset;

public class StringUtils {
	
	//Matches a person's full name
	public static final String REGEX_FULL_NAME = "^(?!.{52,})[\\p{L}'-]{1,50}(?:\\s?[\\p{L}'-]{1,50})+$";
	private final static Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	public static boolean notNullOrEmpty(String string){
		return (string != null && !string.isEmpty());
	}
	
	public static String md5(String senha) {
		String sen;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
		sen = hash.toString(16);
		while (sen.length() < 32) {  
			sen = "0" + sen;  
		}  
		return sen;
	}
	
	public static String decodeUTF8(byte[] bytes) {
	    return new String(bytes, UTF8_CHARSET);
	}

	public static byte[] encodeUTF8(String string) {
	    return string.getBytes(UTF8_CHARSET);
	}
	
	public static String converteStringUTF8(String valor){
		return decodeUTF8(encodeUTF8(valor));
	}
	
}
