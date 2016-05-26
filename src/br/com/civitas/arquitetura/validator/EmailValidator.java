package br.com.civitas.arquitetura.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.util.StringUtils;

@FacesValidator("emailValidator") 
public class EmailValidator implements Validator {
	
	public static final String REGEX = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$\\%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
	public static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
		throws ValidatorException {
		if( StringUtils.hasText( value.toString() ) ){
			String email = value.toString();
			boolean emailValido = false;
			
			Matcher matcher = PATTERN.matcher(email);
			
			emailValido = matcher.matches();
			
			if( !emailValido ){
				FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Email inválido.", "Email inválido." );
				throw new ValidatorException(message);
			}
		}
	}

}
