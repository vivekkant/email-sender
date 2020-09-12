/**
 * 
 */
package org.weekendsoft.emailsender;

import org.apache.log4j.Logger;
import org.weekendsoft.configmanager.ConfigManager;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
		this.sendEmail(from, to, subject, body, false);
	}

	@Override
	public void sendHTMLEmail(String from, String to, String subject, String body) throws Exception {
		this.sendEmail(from, to, subject, body, true);

	}
	
	public void sendEmail(String from, String to, String subject, String body, boolean html) throws Exception {
		
		LOG.debug("Sending email with the details, TO:" + to + " from:" + from + " subject:" + subject);
		LOG.debug("Body: " + body);
		
		OkHttpClient client = new OkHttpClient.Builder().build();
		String json = createRequestJSON(from, to, subject, body, html);
		RequestBody reqBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

		LOG.debug("Request body: " + json);
		
		Request request = new Request.Builder()
							.url(url)
							.addHeader("Accept", "application/json")
							.addHeader("api-key", key)
							.post(reqBody)
							.build();
		
		Call call = client.newCall(request);
		Response response = call.execute();
		
		if (!response.isSuccessful()) {
			LOG.error("Response code: " + response.code());
			LOG.error("Response message: " + response.body().string());
			throw new Exception("Sending email failed : ");
		}
		else {
			
		}
		LOG.debug("Response code: " + response.code());
		LOG.debug("Response message: " + response.body().string());
	}
	
	private String createRequestJSON(String from, String to, String subject, String body, boolean html) {
		
		String json = "{\"sender\": {\"name\": \"" + from
				+ "\",\"email\": \"" + from
				+ "\"},\"to\": [{\"email\": \"" + to
				+ "\"}],\"" + (html?"html":"text")
				+ "Content\": \"" + body
				+ "\",\"subject\": \"" + subject
				+ "\"}";
		
		return json;
	}
	
	

}
