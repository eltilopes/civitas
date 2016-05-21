package br.com.civitas.helpers.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Classe utilitária para manipulação de datas.
 * 
 * @author Sérgio Danilo
 *
 */
public class DateUtils {
	
	public static String dateToString(Date date){
		return toStringAnyDate("dd/MM/yyyy", date);
	}

	public static Date createNewDate(String date){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String dateToString(LocalDate date){
		Objects.requireNonNull(date, "Data não pode ser nula");	
		return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	
	public static LocalDate toLocalDate(Date date){
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static int getCurrentYear() {
		return LocalDate.now().getYear();
	}
	
	/**
	 * 	Retorna a data e hora atual formatada de acordo com o padrão desejado
	 * 
	 * @return
	 */
	public static String toStringLocalDateTime(String pattern) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime today = LocalDateTime.now();
		return today.format(dtf);
	}
	
	/**
	 * 	Retorna a data e hora atual formatada para concatenação no nome do
	 * arquivo que necessitem de timestamp em seu nome
	 * 
	 * @return
	 */
	public static String toStringLocalDateTimeFile() {
		return toStringLocalDateTime("ddMMyyyy_HHmmss");
	}
	
	public static String toStringAnyDate(String pattern, Date date) {
		Objects.requireNonNull(date, "Data não pode ser nula");	
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
}
