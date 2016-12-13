package br.com.civitas.arquitetura.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtil implements FilenameFilter {

	private static ArrayList<String> patterns;
	
	private boolean regexp;
	
	private boolean acceptdir;
	
	public static List<File> getFileByPath(String path , String ... extensoes) {
		
		ArrayList<File> files = new ArrayList<File>();
		for (String extensao : extensoes){
			files.addAll(listFiles(path , extensao , false, false));
		}
		return files;
	}
	
	private FileUtil(String filtro, boolean regexp, boolean acceptdir) {
		patterns = new ArrayList<String>();
		this.acceptdir = acceptdir;
		this.setPatterns(filtro);
		this.regexp = regexp;
	}

	public boolean accept(File dir, String name) {
		if (acceptdir) {
			File f = new File(dir.getAbsolutePath() + File.separatorChar + name);
			if (f.exists() && f.isDirectory()) {
				return(true);
			}
		}
		return(valida(name));
	}
	
	@SuppressWarnings("rawtypes")
	private static boolean valida(String name) {
		boolean result = false;
		boolean bMatch = false;
		for (Iterator iter = patterns.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			bMatch = Pattern.matches(element, name);
			if (bMatch) {
				result = true;
				break;
			}
		}
		return(result);
	}
	
	private void setPatterns(String filtro) {
		String aux = filtro;
		if (!regexp) {
			//configura a explressao regular
			//aux = "^" + aux;
			aux = aux.replace("\\", "\\\\");
			aux = aux.replace("[", "\\[");
			aux = aux.replace("]", "\\]");
			aux = aux.replace(".", "[.]{1}");
			aux = aux.replace("*", ".*");
			aux = aux.replace("?", ".{1}");
			//aux = aux + "$";
		}
		patterns.clear();
		String []auxiliar = aux.split(";");
		for (int i = 0; i < auxiliar.length; i++) {
			String str = auxiliar[i];
			patterns.add(str);
		}
	}
	
	private static ArrayList<File> processDir(File dir, FilenameFilter filtro, boolean recursive) {
		File []lista = dir.listFiles(filtro);
		ArrayList<File> files = new ArrayList<File>();
		for (int i = 0; i < lista.length; i++) {
			File elem = lista[i];
			if (elem.isDirectory()) {
				if (recursive) {
					ArrayList<File> tmp = processDir(elem, filtro, recursive);
					files.addAll(tmp);
				}
			} else {
				files.add(elem);
			}
		}
		return(files);
	}
	
	private static ArrayList<File> listFiles(String directory, String filtro, boolean recursive, boolean regexp) {
		ArrayList<File> result = new ArrayList<File>();
		File dir = new File(directory);
		if (dir.exists() && dir.isDirectory()) {
			FileUtil fileUtil = new FileUtil(filtro, regexp, recursive);
			ArrayList<File> fileTmp = processDir(dir, fileUtil, recursive);
			result.addAll(fileTmp);
			
		} else {
			throw new RuntimeException("Diretorio nao encontrado: " + dir.getAbsolutePath());
		}
		return(result);
	}
}
