package br.com.civitas.arquitetura.converter;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.civitas.arquitetura.entity.IEntity;

@FacesConverter(value="entityConverter")
public class SimpleEntityConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		IEntity retorno = null;
		if(value != null && !value.equals("") && !"null".equals(value)){
			IEntity entity = (IEntity)this.getAttributesFrom(component).get(value);
			if(entity != null){
				retorno = entity;
			}
		}
		return value != null ? retorno : null ;
	}

	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		if (value != null && !"".equals(value) && !"null".equals(value)) {
			IEntity entity =  (IEntity) value;
			// adiciona item como atributo do componente
			this.addAttribute(component, entity);
			Long codigo = entity.getId();
			return codigo != null ? String.valueOf(codigo) : null ;
		}
		return (String) value;
	}

	protected void addAttribute(UIComponent component, IEntity o) {
		String key = o.getId() != null ? o.getId().toString() : "" ;
		this.getAttributesFrom(component).put(key, o);
	}

	protected Map<String, Object> getAttributesFrom(UIComponent component) {
		return component.getAttributes();
	}

}