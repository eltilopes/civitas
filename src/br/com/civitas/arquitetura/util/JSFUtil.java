package br.com.civitas.arquitetura.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class JSFUtil {
	
   public static final String USUARIO_LOGADO = "usuarioLogado";
   
	public static String getRequestContext(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getExternalContext().getRequestContextPath();
	}
	
	public static FacesContext getFacesContext(){
		return FacesContext.getCurrentInstance();
	}
	
	public static void addGlobalMessage(String messageBundleKey, Severity facesSeverity, Object... messageArguments){
		if(facesContextIsNull()){
			return;
		}
		try{
			String translatedMessage = getMessageFromBundle(messageBundleKey, messageArguments);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(facesSeverity, translatedMessage , null));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(facesSeverity, messageBundleKey, null));
		}
	
	}

	public static String getMessageFromBundle(String messageBundleKey, Object... messageArguments) {
		ResourceBundle bundle = getBundle();
		MessageFormat formatter = new MessageFormat(bundle.getString(messageBundleKey));
		String translatedMessage = formatter.format(messageArguments);
		return translatedMessage;
	}

	@SuppressWarnings("unchecked")
	private static Map<Severity, String> getMessagesMap(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Map <String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
		Map<Severity, String> messageMap = (Map<Severity, String>) sessionMap.get("message_map");
		if ( messageMap == null ){
			messageMap = new HashMap<Severity, String>();
			sessionMap.put("message_map", messageMap);
		}
		return messageMap;
	}
	
	public static Severity getMaximumMessageSeverity() {
		Map<Severity, String> messageMap = getMessagesMap();
		Severity maximumSeverity = null;
		
		if ( !messageMap.isEmpty() ){
			maximumSeverity = FacesMessage.SEVERITY_INFO;
			for(Severity severity: messageMap.keySet()){
				if (maximumSeverity.getOrdinal() < severity.getOrdinal()){
					maximumSeverity = severity;
				}
			}
		}
		
		return maximumSeverity;
	}

	public static String getErrorMessage(){
		Map<Severity, String> messageMap = getMessagesMap();
		return messageMap.remove(FacesMessage.SEVERITY_ERROR);
	}

	public static String getWarningMessage(){
		Map<Severity, String> messageMap = getMessagesMap();
		return messageMap.remove(FacesMessage.SEVERITY_WARN);
	}

	public static String getSuccessMessage(){
		Map<Severity, String> messageMap = getMessagesMap();
		return messageMap.remove(FacesMessage.SEVERITY_INFO);
	}
	
	public static ResourceBundle getBundle(){
		if(facesContextIsNull()){
			return ResourceBundle.getBundle("civitas", new Locale("pt_BR"));
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application facesApplication = facesContext.getApplication();
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(facesApplication.getMessageBundle(), facesContext.getExternalContext().getRequestLocale());
		} catch(MissingResourceException mre) {
			bundle = ResourceBundle.getBundle(facesApplication.getMessageBundle(), facesApplication.getDefaultLocale());
		}
		return bundle;
	}

	public static ResourceBundle getBundle(String nomeArquivoPropriedade, Locale contextoArquivoPropriedade){
	   ResourceBundle bundle = ResourceBundle.getBundle(nomeArquivoPropriedade, contextoArquivoPropriedade);
	   if (bundle == null){
	      bundle = ResourceBundle.getBundle(nomeArquivoPropriedade, Locale.getDefault());
	   }
	   return bundle;
	}
	
	public static HttpSession getSession(){
       return ((HttpServletRequest) getFacesContext().getExternalContext().getRequest()).getSession();
    }
	
	public static boolean facesContextIsNull(){
		return FacesContext.getCurrentInstance() == null;
	}
	
	public static String getParameterForFaces(String value){
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(value);
	}
	
	public static Boolean isPesquisar(){
		if(FacesContext.getCurrentInstance() != null){
			String pesquisar = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pesquisar");
			if(pesquisar != null && pesquisar.equals("true")){
				return true;
			}
		}
		return false;
	}
}