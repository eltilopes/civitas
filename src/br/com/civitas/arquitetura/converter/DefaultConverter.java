package br.com.civitas.arquitetura.converter;


import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.civitas.arquitetura.util.ReflectionUtil;

@FacesConverter("converterEntity")
public class DefaultConverter implements Converter {

    public Object getAsObject(FacesContext ctx, UIComponent component,
            String value) {
        if (value != null) {
            return this.getAttributesFrom(component).get(value);
        }
        return null;
    }

    public String getAsString(FacesContext ctx, UIComponent component,
            Object value) {

        if (value != null && !"".equals(value)) {

            this.addAttribute(component, value);

            return getId(value);
        }

        return (String) value;
    }

    public String getId(Object object) {
        String nomeId = ReflectionUtil.getFieldNameID(object.getClass());
        return ReflectionUtil.getValueField(object.getClass(), object, nomeId).toString();
    }

    protected void addAttribute(UIComponent component, Object o) {
        this.getAttributesFrom(component).put(getId(o), o);
    }
    
    protected Map<String, Object> getAttributesFrom(UIComponent component) {
        return component.getAttributes();
    }

}
