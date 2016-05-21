package br.com.civitas.arquitetura.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.component.PartialStateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.springframework.context.i18n.LocaleContextHolder;

@FacesConverter(forClass = LocalDate.class, value = "localDateConverter")
public class LocalDateConverter implements Converter, PartialStateHolder {
	
	private final String pattern = "dd/MM/yyyy";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    private boolean transientFlag = true;
    private boolean initialState;
    
    @Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	if (value == null || value.isEmpty()) {
            return null;
        }
    	 return LocalDate.from(formatter.withLocale(LocaleContextHolder.getLocale()).parse(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if(object == null){
			return "";
		}
		
		if(object instanceof LocalDate){
			return formatter.withLocale(LocaleContextHolder.getLocale()).format((LocalDate) object);
		}

		throw new IllegalArgumentException("Expecting a LocalDate instance but received " + object.getClass().getName());
	}

    public String getPattern() {
        return pattern;
    }
    
	@Override
	public boolean isTransient() {
		return (transientFlag);
	}

	@Override
	public void restoreState(FacesContext arg0, Object arg1) {
		
	}

	@Override
	public Object saveState(FacesContext arg0) {
		return "";
	}

	@Override
	public void setTransient(boolean transientFlag) {
		this.transientFlag = transientFlag;
		
	}

	@Override
	public void clearInitialState() {
		initialState = false;	
	}

	@Override
	public boolean initialStateMarked() {
		return initialState;
	}

	@Override
	public void markInitialState() {
		initialState = true;
	}

}
