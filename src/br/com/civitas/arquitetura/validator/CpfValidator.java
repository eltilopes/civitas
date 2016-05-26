package br.com.civitas.arquitetura.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Responsável pela validação do número de cpf.
 * @author Samuel Soares
 */
@FacesValidator("cpfValidator")
public class CpfValidator implements Validator{

	/**
	 * Efetua validação de cpf com base nos dígitos verificadores.
	 */
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		String cpf = value.toString();
		int somatorio1 = 0; //Somatório para o primeiro dígito verificador.
		int somatorio2 = 0; //Somatório para o segundo dígito verificador.
		int digitoVerif;
		boolean numeroValido = false;
		
		//Verifica se todos os digitos são iguais.
		if (cpf.length() == 11){
			for (int i = 0; i < 10; ++i){
				if (cpf.charAt(i) != cpf.charAt(i + 1)){
					numeroValido = true;
					break;
				}
			}
		}
		
		if (numeroValido) {
			for (int i = 0; i <= 8; ++i) {
				try{
					int digito = Integer.parseInt(String.valueOf( cpf.charAt(i) ) );
					somatorio1 = somatorio1 + (digito * (10 - i));
					somatorio2 = somatorio2 + (digito * (11 - i));
				}
				catch( NumberFormatException nfe ){
					FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Número de CPF inválido.", "Número de CPF inválido." );
					
					throw new ValidatorException(message);
				}
			}
			// Cálculo do primeiro dígito verificador.
			int resto = somatorio1 % 11;
			if (resto < 2) {
				digitoVerif = 0;
			} else {
				digitoVerif = 11 - resto;
			}

			if ( Integer.parseInt( String.valueOf( cpf.charAt(9) ) ) != digitoVerif ) {
				numeroValido = false;
			}
			
			if (numeroValido) {
				// Cálculo do segundo dígito verificador.

				somatorio2 = somatorio2 + (digitoVerif * 2);
				resto = somatorio2 % 11;

				if (resto < 2) {
					digitoVerif = 0;
				} else {
					digitoVerif = 11 - resto;
				}

				if ( Integer.parseInt(String.valueOf( cpf.charAt(10) ) ) != digitoVerif) {
					numeroValido = false;
				}
			}
		}
		
		if( !numeroValido ){
			FacesMessage message = new FacesMessage( FacesMessage.SEVERITY_ERROR, "Número de CPF inválido.", "Número de CPF inválido." );
			throw new ValidatorException(message);
		}
	}
}