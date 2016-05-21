package br.com.civitas.helpers.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
	
	//Matches a person's full name
	public static final String REGEX_FULL_NAME = "^(?!.{52,})[\\p{L}'-]{1,50}(?:\\s?[\\p{L}'-]{1,50})+$";
	
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
}
