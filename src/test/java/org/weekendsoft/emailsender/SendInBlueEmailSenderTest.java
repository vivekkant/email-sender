package org.weekendsoft.emailsender;

import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SendInBlueEmailSenderTest {
	
	private EmailSender sender = SendInBlueEmailSender.getInstance();
	private Random random = new Random();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void testTextEmails() throws Exception {
		String from = "test@weekendsoft.org";
		String to = "vivek.kant@gmail.com";
		String subject = "Test " + random.nextInt();
		String body = "This is a test " + random.nextInt();
		
		sender.sendPlainTextEmail(from, to, subject, body);
	}
	
	@Test
	void testHtmlEmails() throws Exception {
		String from = "test@weekendsoft.org";
		String to = "vivek.kant@gmail.com";
		String subject = "Test " + random.nextInt();
		String body = "This is a test " + random.nextInt();
		
		sender.sendHTMLEmail(from, to, subject, body);
	}

}
