package br.com.civitas.arquitetura.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service("reportService")
public class ReportService implements Serializable {

    private static final long serialVersionUID = -4154822090445611695L;
    
    private static int parameter = 1;
    
    private @Resource FacadeReport facadeReport;
    
    public void generate(final Documento documento) {
    	
    	if(documento.isValido()) {
    		
    		FacesContext faces = FacesContext.getCurrentInstance();
    		Map<String, Object> session = faces.getExternalContext().getSessionMap();
    		
    		if( documento.isPopup() ) {
    			
    			long version =  new Date().getTime();
    			session.put(String.valueOf(version), documento);
    			
    			montaPopup(version);
    			
    		} else {
    			
    			byte[] stream = documento.getStream();
    			
    			FacesContext facesContext = FacesContext.getCurrentInstance();
    			ExternalContext externalContext = facesContext.getExternalContext();
    			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
    			
    			response.reset();
    			response.setContentType( documento.getExtensao().getContentType() );
    			response.setHeader("Content-disposition", "attachment; filename=\"" +  documento.toString() + "\"");
    			
    			try {
    				
    				OutputStream output = response.getOutputStream();
    				output.write(stream);
    				output.close();
    				
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			
    			facesContext.renderResponse();
    			facesContext.responseComplete();
    			
    		}
    	}
    }
    
    public void execute(Map<String, Object> parameters, String report, Extensao formato, String name) {
    	execute(parameters, report, formato, name, true);
    }
    
    public void execute(Map<String, Object> parameters, String report, Extensao formato, String name, boolean popup) {
    	
    	byte[] data = null;

        if ( Extensao.PDF.equals( formato ) ) {
            data = facadeReport.executePdf(report, parameters);
        } else if ( Extensao.HTML.equals( formato ) ) {
        	
        	FacesContext faces = FacesContext.getCurrentInstance();
        	
            ServletContext ctx = ((HttpServletRequest) faces.getExternalContext().getRequest()).getSession(false).getServletContext();
            String dest = ctx.getRealPath("/");
            data = facadeReport.executeHtml(report, "report_" + parameter, dest, parameters);
            
        } else if ( Extensao.XLS.equals( formato ) ) {
            data = facadeReport.executeXls(report, parameters);
        } else if ( Extensao.ODT.equals( formato ) ) {
            data = facadeReport.executeODT(report, parameters);
        } else {
        	return;
        }
        
        Documento documento = new Documento( data );
        
        documento.setPopup(popup);
        documento.setExtensao(formato);
        documento.setName(name);
        
        generate(documento);
    }
    
    public void execute(Map<String, Object> parameters, String report, Extensao formato, boolean popup) {
    	execute(parameters, report, formato, "report", popup);
    }
    
    public void execute(Map<String, Object> parameters, String report, Extensao formato) {    	
    	execute(parameters, report, formato, true);
    }

    private void montaPopup(long relarorio) {
        FacesContext faces = FacesContext.getCurrentInstance();
        ResourceUtils.addOutputStylesheetResource(faces, getScript(relarorio));
    }

    public FacadeReport getFacadeReport() {
        return facadeReport;
    }

    public void setFacadeReport(FacadeReport facadeReport) {
        this.facadeReport = facadeReport;
    }

    public String getScript(long relatorio) {
        
        final StringBuffer script = new StringBuffer();
        
		script.append(String.format("visualizarRelatorio(%d);", relatorio));
        
        return script.toString();
    }
}
