package br.com.civitas.arquitetura.util;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Service;

@Service
public class Util {

	public static Double converterDoubleDoisDecimais(Double precoDouble) {
		if (precoDouble == null) {
			return 0D;
		}
		DecimalFormat fmt = new DecimalFormat();
		fmt.setMinimumFractionDigits(2);
		fmt.setMaximumFractionDigits(2);
		String string = fmt.format(precoDouble);
		double preco = Double.parseDouble(string.replaceAll(",", "."));
		return preco;
	}
	
	public static Double stringToDouble(String valor){
		if(StringUtils.isBlank(valor)){
			return null;
		}
		
		String valorSemSinal = valor.substring(1);
		String novoValor = valorSemSinal.substring(0, valorSemSinal.length() - 2) + "." + valorSemSinal.substring(valorSemSinal.length() - 2);
		Double resultado = Double.parseDouble(novoValor);
		
		if(valor.subSequence(0, 1).equals("-")){
			resultado = (-1) * resultado;
		}
		
		return resultado;
	}
	
	public static Date stringToDate(String data){
		if(StringUtils.isBlank(data) || data.equals("00000000")){
			return null;
		}
		
		return DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(data).toDate();
	}
	
	public static String removerAcento(String str) {
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;
	}
	
	public static String removerZerosEsquerda(String valor){
		while(valor.startsWith("0")){
			valor = StringUtils.removeStart(valor, "0");
		}
		return valor;
	}
	
	public static Long getLong(Map<String, Object> filter, String value){
		return Long.parseLong(filter.get(value).toString());
	}
	
	public static String doubleToString(double valor){
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		return nf.format(valor);
	}
	
	public static String getNomeFormatado(int quantidade, String valor){
		String zeros = "";
		for (int i = 0; i < (quantidade - valor.length()); i++) {
			zeros += "0";
		}
		return zeros + valor;
	}
	
	public static int posicaoPrimeiraLetra(String palavra){
		int posicao = 0;
		for(Character c : palavra.toCharArray()){
			if(!Character.isDigit(c))	return posicao;
			posicao++;
		}
		return posicao;
	}

	public static int posicaoPrimeiraNumero(String palavra){
		int posicao = 0;
		for(Character c : palavra.toCharArray()){
			if(Character.isDigit(c))	return posicao;
			posicao++;
		}
		return posicao;
	}
	
	public static String logTime(DateTime dataInicio, DateTime dataFim){
		PeriodFormatter periodFormatter = new PeriodFormatterBuilder().printZeroAlways()
																	  .minimumPrintedDigits(2)
																	  .appendHours()
																	  .appendLiteral(":")
																	  .appendMinutes()
																	  .appendLiteral(":")
																	  .appendSeconds()
																	  .toFormatter();
		
		return periodFormatter.print(new Period(dataInicio, dataFim));
	}
	
	public static String cepFormatado(String cep){
		if (cep == null){
			return null;
		}
		cep = cep.substring(0,2) + "." + cep.substring(2,5) + "-" + cep.substring(5,8);
		return cep;
	}
	
	public static String telefoneFormatado(String telefone){
		if(telefone == null){
			return null;
		}
		telefone = "(" + telefone.substring(0, 2) + ")" + telefone.substring(2, 6) + "-" + telefone.substring(6);
		return telefone;
	}
}