package br.com.civitas.arquitetura.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.faces.context.FacesContext;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ResourceUtils {
	
	private String[] resourcesPath;
	
	public ResourceUtils() {}
	
	
	public String getResource(String resource, Map<String, String> params){
		String resourceText  = null;
		try{
			resourceText = loadResource(resource);
			
			params.putAll(getParamsMap());
			
			for (Entry<String, String> param: params.entrySet()) {
				
				Objects.requireNonNull(param.getValue(), String.format("Valor nulo para a chave: %s", param.getKey() ));
				
				resourceText = resourceText.replace("#{"+param.getKey()+"}", param.getValue());
				
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return resourceText;
	}
	
	public Map<String, String> getParamsMap(){
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("contextPath", FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath());
		
		return params;
	}
	
	
	public String loadResource(String resource) throws IOException{
		Resource r = null;
		for (String resourcePath : getResourcesPath()) {
			r = new ClassPathResource(resourcePath +resource);
			if(r.isReadable())
				return getStringFromInputStream(r.getInputStream());
		}
		
		throw new IllegalArgumentException("Não foi possível encontrar o recurso informado. Recurso: "+ resource);
	}
	
	
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}


	public String[] getResourcesPath() {return resourcesPath;}
	public void setResourcesPath(String[] resourcesPath) {this.resourcesPath = resourcesPath;}	
	
	
}
