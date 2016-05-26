package br.com.civitas.arquitetura.util;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import br.com.civitas.arquitetura.ApplicationException;
import br.com.civitas.arquitetura.base.vo.EmailVO;


public class EmailUtil implements InitializingBean {
	
	private String userName;
	private String password;
	private Properties properties;
	
	
	public static String geraSenha() {

		String[] carct = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
				"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
				"Y", "Z" };
		String senha = "";
		for (int x = 0; x < 6; x++) {
			senha += carct[(int) (Math.random() * carct.length)];
		}
		return senha;
	}

	public void enviar(EmailVO email) {
		
		try {
			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});
			
			MimeMessage mimiMessage = new MimeMessage(session);
			
			MimeMessageHelper mimeMessageHelper;
			mimeMessageHelper = new MimeMessageHelper(mimiMessage, true);
			
			mimeMessageHelper.setSentDate(new Date());
			mimeMessageHelper.setTo(email.getDestino());
			mimeMessageHelper.setText(email.getMensagem(), email.isHtml());
			mimeMessageHelper.setSubject(email.getAssunto());
			mimeMessageHelper.setFrom(email.getRemetente());
	
			if (email.getAnexos() != null) {
				FileSystemResource file = null;
				for (Map.Entry<String, String> item : email.getAnexos().entrySet()) {
					file = new FileSystemResource(new File(item.getValue()));
					mimeMessageHelper.addAttachment(item.getKey(), file);
				}
			}
			
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
			
			Transport.send(mimiMessage);
		
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new ApplicationException("Ocorreu um problema no envio de e-mail. Tente novamente mais tarde.");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}


	/**
	 * Gera um template de documento html formatado pronto para inserir conteúdo   
	 * 
	 * */
	public String generateHtmlMessage(String htmlMessage) {
		StringBuilder contentEmail = new StringBuilder();
		contentEmail.append("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
 	 	contentEmail.append("<html>");
 	 	contentEmail.append("<head>");
 	 	contentEmail.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
 	 	contentEmail.append("<title>SME</title>");
 	 	contentEmail.append("</head>");
 	 	contentEmail.append("<body >");
 	 	contentEmail.append("<table width='500'  border='1' align='center' cellpadding='0' cellspacing='0' bordercolor='#E3E1BA' bgcolor='#FFFFFF'>");
 	 	contentEmail.append("<tr>");
 	 	contentEmail.append("<td><img src='http://www.sme.fortaleza.ce.gov.br/educacao/images/topoEmail.jpg' width='500' height='70'></td>");
 	 	contentEmail.append("</tr>");
 	 	contentEmail.append("<tr>");
 	 	contentEmail.append("<td><div align='justify'>");
 	 	contentEmail.append("<table width='100%'  border='0' cellpadding='10' cellspacing='0'>");
 	 	contentEmail.append("<tr>");
 	 	contentEmail.append("<td>");
		contentEmail.append(htmlMessage);
		contentEmail.append("</td>");
 	 	contentEmail.append("</tr>");
 	 	contentEmail.append("</table>");
 	 	contentEmail.append("</div></td>");
 	 	contentEmail.append("</tr>");
 	 	contentEmail.append("</table>");
 	 	contentEmail.append("</body>");
 	 	contentEmail.append("</html>");
		return contentEmail.toString();
	}

	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}

	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}

	public Properties getProperties() {return properties;}
	public void setProperties(Properties properties) {this.properties = properties;}

	@Override
	public void afterPropertiesSet() throws Exception {	}

}
