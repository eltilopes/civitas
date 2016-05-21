package br.com.civitas.arquitetura.util;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.civitas.arquitetura.ApplicationException;


public class PropertiesUtils {

	/**
	 * Retorna propriedades de um arquivo .properties.
	 * @param path caminho do arquivo .properties.
	 * @return
	 */
	public static Properties getProperties( String path ) {
		
		Properties prop = new Properties();
		try (InputStream stream = PropertiesUtils.class.getResourceAsStream( path )) {
			
			prop.load( stream );
		}catch (FileNotFoundException ex) {  
			throw new ApplicationException("Arquivo .properties n�o encontrado.");  
		} catch (IOException ex) {  
			throw new ApplicationException("Erro de I/O do arquivo .properties.");
		}
		
		return prop;
	}
	
	/**
	 * Pega o valor de determinada chave do arquivo .properties.
	 * @param path caminho do arquivo .properties. E.g.: /properties.properties.
	 * @param key
	 * @return
	 */
	public static String getValueProperty( String path, String key ){
		return (String) PropertiesUtils.getProperties( path ).get( key );
	}
}
