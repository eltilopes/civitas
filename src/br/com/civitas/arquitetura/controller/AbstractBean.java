package br.com.civitas.arquitetura.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.event.ActionEvent;

import br.com.civitas.arquitetura.util.FacesUtils;
import br.com.civitas.arquitetura.util.PropertiesUtils;

public class AbstractBean {

	private String fileMessages = "/modelo/src/MessagesResources.properties";

	private Properties messageProperties;

	public static final String STATE_EDIT = "edit";

	public static final String STATE_INSERT = "insert";

	public static final String STATE_DELETE = "delete";

	public static final String STATE_VIEW = "view";

	public static final String STATE_SEARCH = "search";

	private String currentState;

	private Integer numeroPagina = 1;

	public AbstractBean() {
		setCurrentState(STATE_SEARCH);
	}

	public String getMessage(String key) {
		if (messageProperties == null) {
			PropertiesUtils pUtils = new PropertiesUtils();
			messageProperties = pUtils.getProperties(getFileMessages());
		}
		return (String) getMessageProperties().get(key);
	}

	public List<String> getAlphaNumericList() {
		List<String> lista = new ArrayList<String>();
		lista.add("Todas");
		for (int i = 0; i < 10; i++) {
			lista.add(String.valueOf(i));
		}
		for (int i = 65; i <= 90; i++) {
			lista.add(String.valueOf((char) i));
		}
		return lista;
	}

	public boolean isEditing() {
		return STATE_EDIT.equals(getCurrentState());
	}

	public boolean isInserting() {
		return STATE_INSERT.equals(getCurrentState());
	}

	public boolean isDeleting() {
		return STATE_DELETE.equals(getCurrentState());
	}

	public boolean isViewing() {
		return STATE_VIEW.equals(getCurrentState());
	}

	public boolean isUpdating() {
		return (this.isEditing() || this.isInserting() || this.isDeleting());
	}

	public boolean isSearching() {
		return (getCurrentState() == null || STATE_SEARCH.equals(getCurrentState()));
	}

	public void cancel(ActionEvent event) {
		init();
		FacesUtils.refresh();
	}

	public void init() {
		this.setCurrentState(STATE_SEARCH);
		this.numeroPagina = 1;
	}

	public void init(ActionEvent event) {
		init();
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getCurrentState() {
		return currentState;
	}

	@SuppressWarnings("unused")
	private boolean mensagemErro;

	public boolean isMensagemErro() {
		if (FacesUtils.isMessageErro()) {
			return true;
		}
		return false;
	}

	public void setMensagemErro(boolean mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public Integer getNumeroPagina() {
		return numeroPagina;
	}

	public void setNumeroPagina(Integer numeroPagina) {
		this.numeroPagina = numeroPagina;
	}

	public String getFileMessages() {
		return fileMessages;
	}

	public void setFileMessages(String fileMessages) {
		this.fileMessages = fileMessages;
	}

	public Properties getMessageProperties() {
		return messageProperties;
	}

	public void setMessageProperties(Properties messageProperties) {
		this.messageProperties = messageProperties;
	}

}
