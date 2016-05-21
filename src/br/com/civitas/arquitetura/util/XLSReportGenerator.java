package br.com.civitas.arquitetura.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;

import com.google.common.collect.Maps;

import br.com.civitas.helpers.utils.DateUtils;

public class XLSReportGenerator {

	private HSSFWorkbook workbook;
	private Map<String, Object[]> data = Maps.newHashMap();
	private int numLines = 1;
	private String xlsTitle;
	
	public XLSReportGenerator(String xlsTitle) {
		this.xlsTitle = xlsTitle;
		workbook = new HSSFWorkbook();
	}
	
	public void addTitleColumns(Object[] objects) {
		data.put(numLines + "", objects);
	}
	
	public void addCells(Object[] objects) {
		data.put((numLines + 1) + "", objects);
		numLines++;
	}
	
	@SuppressWarnings("deprecation")
	public void generateXLS() {
		HSSFSheet sheet = workbook.createSheet(xlsTitle);
		
		Map<String, Object[]> treeMap = new TreeMap<String, Object[]>(
			new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
			}

		});
		treeMap.putAll(data);
			
		Set<String> keyset = treeMap.keySet();
		
		int rownum = 0;
		for (String key : keyset) {
			HSSFRow row = sheet.createRow(rownum++);
		    Object [] objArr = data.get(key);
		    int cellnum = 0;
		    for (Object obj : objArr) {
		    	HSSFCell cell = row.createCell(cellnum++);
	        	cell.setCellValue((String) obj);
		    }
		}
	}
	
	public DefaultStreamedContent getDefaultStreamedContent(String fileName) throws FileNotFoundException, IOException {
		File out = new File("file.xls");
		workbook.write(new FileOutputStream(out));

		return new DefaultStreamedContent(new FileInputStream(out), "application/vnd.ms-excel",
				fileName + DateUtils.toStringLocalDateTimeFile() + ".xls");
	}
	
	/**
	 * @return the numLines
	 */
	public String getNumLines() {
		return numLines + "";
	}
	
}
