package br.com.civitas.helpers.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	// Matches a person's full name
	public static final String REGEX_FULL_NAME = "^(?!.{52,})[\\p{L}'-]{1,50}(?:\\s?[\\p{L}'-]{1,50})+$";
	private final static Charset UTF8_CHARSET = Charset.forName("UTF-8");

	public static boolean notNullOrEmpty(String string) {
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

	public static int contarVezesPalavraNaFrase(String frase, String palavra) {
		try {
			return (frase.split(palavra)).length - 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static String decodeUTF8(byte[] bytes) {
		return new String(bytes, UTF8_CHARSET);
	}

	public static byte[] encodeUTF8(String string) {
		return string.getBytes(UTF8_CHARSET);
	}

	public static String converteStringUTF8(String valor) {
		return decodeUTF8(encodeUTF8(valor));
	}
	
	public static boolean contemDiasTrabalhados(String string) {
		return string.matches("^\\s\\d{1,2}\\D\\s$");
	}
	
	public static String getDiasTrabalhados(String linha) {
		
		String queremosIsso = "^\\s\\d{1,2}\\D\\s$";
		
		Pattern p = Pattern.compile(queremosIsso);
		Matcher m = p.matcher(linha);
		
		while(m.find()) {
			return m.group();
		}
		
		return null;
	}

}
