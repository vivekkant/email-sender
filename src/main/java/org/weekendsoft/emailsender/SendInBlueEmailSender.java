/**
 * 
 */
package org.weekendsoft.emailsender;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.weekendsoft.configmanager.ConfigManager;

import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.SmtpApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

/**
 * @author Vivek Kant
 *
 */
public class SendInBlueEmailSender implements EmailSender {
	
	private static final Logger LOG = Logger.getLogger(ConfigManager.class);
	
	private static final String APP_URL_KEY = "sendinblue.url";
	private static final String APP_KEY_KEY = "sendinblue.appkey";
	
	private static SendInBlueEmailSender INSTANCE = null;
	private static ConfigManager cfg = null;
	
	private static String key = null;
	private static String url = null;
	
	private SendInBlueEmailSender() {
		super();
	}
	
	public static EmailSender getInstance() {
		
		if (INSTANCE == null) {
			
			INSTANCE = new SendInBlueEmailSender();
			
			cfg = ConfigManager.getInstance();
			key = cfg.getProperty(APP_KEY_KEY);
			url = cfg.getProperty(APP_URL_KEY);
			
			if (key == null || "".equals(key)) {
				LOG.error("API Key is null or empty: " + key);
			}
			else {
				LOG.debug("API Key found in configuration: " + key);
			}
			
			if (key == null || "".equals(key)) {
				LOG.error("API URL is null or empty: " + url);
			}
			else {
				LOG.debug("API URL found in configuration: " + url);
			}
		}
		
		return INSTANCE;
	}

	@Override
	public void sendPlainTextEmail(String from, String to, String subject, String body) throws Exception {
		this.sendEmailSDK(from, to, subject, body, false);
	}

	@Override
	public void sendHTMLEmail(String from, String to, String subject, String body) throws Exception {
		this.sendEmailSDK(from, to, subject, body, true);

	}
	
	public void sendEmailSDK(String from, String to, String subject, String body, boolean html) throws Exception {
		
		LOG.debug("Sending email with the details, TO:" + to + " from:" + from + " subject:" + subject);
		LOG.debug("Body: " + body);
		
		ApiClient defaultClient = Configuration.getDefaultApiClient();
        
		ApiKeyAuth apiKey = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKey.setApiKey(key);
        
        ApiKeyAuth partnerKey = (ApiKeyAuth) defaultClient.getAuthentication("partner-key");
        partnerKey.setApiKey(key);
        
        SmtpApi apiInstance = new SmtpApi();
        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setName(from);
        sender.setEmail(from);
        
        SendSmtpEmailTo sendTo = new SendSmtpEmailTo();
        sendTo.setName(to);
        sendTo.setEmail(to);
        List<SendSmtpEmailTo> toList = new ArrayList<SendSmtpEmailTo>();
        toList.add(sendTo);
        
        sendSmtpEmail.sender(sender);
        sendSmtpEmail.to(toList);
        sendSmtpEmail.subject(subject);
        
        if (html) {
            sendSmtpEmail.htmlContent(body);
        } else {
            sendSmtpEmail.textContent(body);
        }
        
        
        
        try {
            CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);
			LOG.debug("Response message id: " + result.getMessageId());
        } 
        catch (ApiException e) {
        	LOG.error("Got exception while sending email", e);
			throw new Exception("Sending email failed", e);
        }

	}

}
