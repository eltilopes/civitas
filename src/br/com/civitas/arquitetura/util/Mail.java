package br.com.civitas.arquitetura.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sun.mail.smtp.SMTPMessage;

import br.com.civitas.arquitetura.base.config.MailConfig;
import br.com.civitas.arquitetura.base.entity.Email;
import br.com.civitas.arquitetura.service.EmailService;

@Component
public class Mail {
	
	private static final String HOSTNAME= "hostname";
	private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	private static final String MAIL_SMTP_PORT= "mail.smtp.port";
	private static final String MAIL_SMTP_HOST= "mail.smtp.host";
	private static final String MAIL_TRANSPORT_PROTOCOL= "mail.transport.protocol";

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private MailConfig mailConfig;

	@Async
	public void enviarEmail(Email email) {
		if(mailConfig.getEnviaEmail()){
			Properties properties = System.getProperties();
			properties.put(MAIL_SMTP_AUTH, mailConfig.getStmpAuth());
			properties.put(MAIL_SMTP_HOST, mailConfig.getHost());
			properties.put(MAIL_SMTP_PORT, mailConfig.getPort());
			properties.put(MAIL_TRANSPORT_PROTOCOL, mailConfig.getProtocol());
			properties.put(HOSTNAME, mailConfig.getHostname());
			
			Session session = Session.getDefaultInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailConfig.getUsername(), mailConfig.getPassword());
				}
			});
			
			MimeMessage message = new SMTPMessage(session);
			
			try {
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setFrom(new InternetAddress(mailConfig.getUsername()));
				helper.setTo(email.getDestinatario().toLowerCase());
				helper.setSubject(email.getTitulo());
				helper.setText(email.getCorpo(), true);
				Transport.send(message);
			} catch (Exception e) {
				email.setEnviado(false);
				e.printStackTrace();
			}
		}
		emailService.save(email);
	}
}