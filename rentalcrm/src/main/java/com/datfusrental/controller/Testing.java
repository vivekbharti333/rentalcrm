package com.datfusrental.controller;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.datfusrental.email.Netcore;
import com.datfusrental.invoice.ItextInvoice;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@CrossOrigin(origins = "*")
@RestController
public class Testing {
	
	@Autowired
	private ItextInvoice itextInvoice;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private Netcore netcore;

	@RequestMapping(value = "/log")
	public ModelAndView test(HttpServletResponse response) throws Exception {
		return new ModelAndView("home");
	}

	@RequestMapping(value = "/testing")
	public String testing(HttpServletResponse response) throws IOException {
		
		netcore.sentEmail();
//		this.sendemail();
		return "Security Working";
	}
	
	@RequestMapping(value = "/")
	public String version(HttpServletResponse response) throws Exception {
	
		return "1.2";
	}

	public void sendemail() {

        final String username = "no-reply.india@cefinternational.org";
        final String password = "Cef@123#Account";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply.india@cefinternational.org"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("vivekbharti333@gmail.com, info@datfuslab.com")
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Dear Mail Crawler,"
                    + "\n\n Please do not spam my email!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


	
}
