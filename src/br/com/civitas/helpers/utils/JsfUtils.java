package br.com.civitas.helpers.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.MultiValueMap;

public class JsfUtils {
	
	private static final Logger log = Logger.getLogger(JsfUtils.class);
	
	public static Object getBean(String elValue, Class<?> clazz){
		FacesContext context = FacesContext.getCurrentInstance();
		ValueExpression valueExpression = context.getApplication().getExpressionFactory()
				.createValueExpression(context.getELContext(), elValue, clazz);
		return valueExpression.getValue(context.getELContext());
	} 
	
	public static void redirectTo(String path){
		redirectTo(path, null);
	}
	
	public static void redirectTo(String path, MultiValueMap<String, String> params){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		
		URIBuilder builder = new URIBuilder();
		
		builder
			.setScheme(request.getScheme())
			.setHost(request.getServerName())
			.setPort(request.getLocalPort())
			.setPath(request.getContextPath() + path);
		
		if(Objects.nonNull(params)){
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();
			
			params.entrySet().forEach(entry -> {
				nvp.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			});
			
			builder.addParameters(nvp);
		}

		try {
			externalContext.redirect(builder.build().toURL().toString());
		} catch (IOException e) {
			log.log(Level.ERROR, e.getMessage(), e);
		} catch (URISyntaxException e) {
			log.log(Level.ERROR, e.getMessage(), e);
		}
	}
	
	public static Object getFlash(String objectName){
		return FacesContext.getCurrentInstance().getExternalContext().getFlash().get(objectName);
	}
	
	public static void setFlash(String objectName, Object object){
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put(objectName, object);
	}
	
	public static class Messages{
		
		public static final String SUCCESS_MESSAGE = "Dados inseridos com sucesso";
		
		public static final String FAILURE_MESSAGE = "Não foi possível inserir os dados";
		
		public static final String SUCCESS_UPDATE_MESSAGE = "Dados inseridos com sucesso";
		
		public static final String FAILURE_UPDATE_MESSAGE = "Não foi possível inserir os dados";
		
	}

}
