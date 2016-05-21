package br.com.civitas.arquitetura.converter;

import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.civitas.arquitetura.util.JSFUtil;

@FacesConverter(value="valorNumericoMoedaConverter")
public class ValorNumericoMoedaConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		if ((value != null) && !value.equals("")) {
			value = value.replace(".", "").replace(",", ".");
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				JSFUtil.addGlobalMessage("converter.numero.moeda", FacesMessage.SEVERITY_WARN);
			}
		}
		return "".equals(value) ? null : Double.parseDouble(value);
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		Locale locale = new Locale("pt", "BR");
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        String valor = value != null ? nf.format(Double.parseDouble(value.toString())) : null;
		return valor;
	}
}