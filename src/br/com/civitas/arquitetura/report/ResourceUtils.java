package br.com.civitas.arquitetura.report;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * 
 * @author evandir
 *  
 */
public class ResourceUtils {

	public static final String JAVAX_FACES_OUTPUT_COMPONENT_TYPE = "javax.faces.Output";
	public static final String JAVAX_FACES_TEXT_RENDERER_TYPE = "javax.faces.Text";
	public static final String DEFAULT_SCRIPT_RENDERER_TYPE = "javax.faces.resource.Script";
	public static final String TARGET_ATTR = "form";

	public static void addOutputStylesheetResource(final FacesContext facesContext, final String jsResource) {
		
		UIOutput output = new UIOutput();		
		output.setRendererType("javax.faces.resource.Script");
		output.getAttributes().put("name", "relatorio-seduc.js");
		output.getAttributes().put("library", "js");		
		facesContext.getViewRoot().addComponentResource(facesContext, output, "form");

		UIOutput script = (UIOutput) facesContext.getApplication()
				.createComponent(facesContext,
						JAVAX_FACES_OUTPUT_COMPONENT_TYPE,
						JAVAX_FACES_TEXT_RENDERER_TYPE);

		UIOutput outputScript = (UIOutput) facesContext.getApplication()
				.createComponent(facesContext,
						JAVAX_FACES_OUTPUT_COMPONENT_TYPE,
						DEFAULT_SCRIPT_RENDERER_TYPE);

		script.setValue(jsResource);

		script.setTransient(true);

		script.setId(facesContext.getViewRoot().createUniqueId());

		outputScript.getChildren().add(script);

		outputScript.setTransient(true);
		outputScript.setId(facesContext.getViewRoot().createUniqueId());

		facesContext.getViewRoot().addComponentResource(facesContext,
				outputScript, TARGET_ATTR);
	}
}
