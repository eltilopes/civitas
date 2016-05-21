package br.com.civitas.arquitetura.util;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class ManagedBeanUtil {
	
	@SuppressWarnings("rawtypes")
	public static Object getBean(String beanName, Class classe) {
		
		if((beanName == null) || beanName.isEmpty() || (classe == null)){
			return null;
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
		ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
		ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, "#{" + beanName + "}", classe);
		return valueExpression.getValue(elContext);
	}

}
