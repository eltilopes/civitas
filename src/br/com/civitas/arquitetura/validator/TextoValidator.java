package br.com.civitas.arquitetura.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.util.StringUtils;

@FacesValidator("textoValidator")
public class TextoValidator implements Validator{
	
	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
		throws ValidatorException {
		if( arg2!=null && !StringUtils.hasText( arg2.toString() ) ){
			String label = (String)arg1.getAttributes().get("label");
			String texto = arg2.toString();
			String [] ar = texto.split(" ");
			for( String s : ar ){
				if( s.length() > 30 ){
					FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Texto inválido. A palavra " + s + " do campo " + label + " é muito longa.", "Texto inválido. A palavra " + s + " do campo " + label + " é muito longa." );
					throw new ValidatorException(message);
				}
			}
		}
	}
	
}
