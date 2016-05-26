package br.com.civitas.arquitetura.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converte uma string vazia para null.
 * @author Elias Sales, Samuel Soares
 *
 */

@FacesConverter("converterStringVazia")
public class StringVazioConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String str ) {
		if( str != null ){
			if( str.trim().isEmpty() ){
				return null;
			}
		}
		return str;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object obj ) {
		return (String)obj;
	}

}