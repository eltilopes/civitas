package br.com.civitas.arquitetura.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author sergio.danilo
 *
 */
public class SheetReaderUtil {

	/**
	 * 	Retorno um iterator com todas as linhas de uma planilha do tipo: XLSX
	 * @param inputStream
	 * @return
	 */
	public static Iterator<Row> getIteratorXLSX(InputStream inputStream) {
		XSSFWorkbook workbook = null;
		XSSFSheet xssfSheet = null;
		try {
			workbook = new XSSFWorkbook(inputStream);
			xssfSheet = workbook.getSheetAt(0);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xssfSheet.iterator();
	}
	
	/**
	 * 	Retorno um iterator com todas as linhas de uma planilha do tipo: XLS
	 * @param inputStream
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Row> getIteratorXLS(InputStream inputStream) {
		HSSFWorkbook workbook = null;
		HSSFSheet hssfSheet = null;
		try {
			workbook = new HSSFWorkbook(inputStream);
			hssfSheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hssfSheet.iterator();
	}
	
}
