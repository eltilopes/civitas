package br.com.civitas.arquitetura.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// @WebServlet(urlPatterns = {"/report"})
public class WriterReportServlet extends HttpServlet {

    private static final long serialVersionUID = -6222263685880916197L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BufferedOutputStream bos = null;

        HttpSession session = req.getSession();
        
        String documentoId = req.getParameter("documento");
        
        if(documentoId == null) {
        	throw new IllegalArgumentException("Nenhuma chave foi informada para recuperar o documento.");
        }
        
        final Documento documento = (Documento) session.getAttribute( documentoId );
        
        if(documento == null) {
        	throw new IllegalArgumentException("Nenhum documento para a chave informada foi encontrado.");
        }
        
        ServletOutputStream outputStream = resp.getOutputStream();
        
        String content  = documento.isPopup() ? "inline" :"attachment",
        	   filename = documento.toString();
        
        final String contentDisposition = MessageFormat.format(" {0}; filename={1}", content, filename);
        
        resp.setHeader("Content-Disposition", contentDisposition);
        resp.setContentType( documento.getExtensao().getContentType() );
        
        /*if (formato.equalsIgnoreCase(Extensao.PDF.name())) {
            resp.setContentType("application/pdf");
        } else if (formato.equalsIgnoreCase(Extensao.HTML.name())) {
        	// resp.setHeader("Content-Disposition"," inline; filename=Report.html");
            resp.setContentType("text/html");
        } else if (formato.equalsIgnoreCase(Extensao.XLS.name())) {
        	// resp.setHeader("Content-Disposition"," inline; filename=Report.xls");
            resp.setContentType("application/vnd.ms-excel");
        } else if (formato.equalsIgnoreCase(Extensao.ODT.name())) {
        	// resp.setHeader("Content-Disposition"," inline; filename=Report.odt");
            resp.setContentType("application/vnd.oasis.opendocument.text");
        }*/

        byte[] data = documento.getStream();

        bos = new BufferedOutputStream(outputStream);

        bos.write(data);

        bos.flush();
        
        session.removeAttribute( documentoId );

        outputStream.flush();
        outputStream.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
