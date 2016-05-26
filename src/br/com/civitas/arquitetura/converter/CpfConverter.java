package br.com.civitas.arquitetura.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converte um cpf no formato 999.999.999-99 para o formato 99999999999 quando faz a requisição 
 * e faz o inverso quando recebe uma resposta.
 * @author Lucas Sobreira, adaptado por Samuel Soares.
 */
public class CpfConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String cpf) {
		if (cpf == null || cpf.trim().isEmpty() ){ 
			return null;
		}
		
		return cpf.replace(".", "").replace("-", "");
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object cpf) {
			String cpfString = ( cpf != null ? cpf.toString() : null );
			if ( cpfString == null || cpfString.trim().isEmpty() ){ 
				return null;
			}
			
			return cpf.toString().substring(0, 3) + "."
			+ cpf.toString().substring(3, 6) + "."
			+ cpf.toString().substring(6, 9) + "-"
			+ cpf.toString().substring(9, 11);
	}
}