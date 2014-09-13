/**
 * 
 */
package email;

import jodd.mail.Email;
import jodd.mail.SendMailSession;
import jodd.mail.SimpleAuthenticator;
import jodd.mail.SmtpServer;

/**
 * @author dzh
 * @date Jun 7, 2014 2:39:42 PM
 * @since 1.0
 */
public class TestJavaMail {

	static String html_table = "<table border=\"1\"><tr><th>Month</th><th>Savings</th></tr>"
			+ "<tr><td>January</td><td>$100</td></tr></table>";

	// @Test
	public void testSendMail() {
		SmtpServer smtpServer = new SmtpServer("smtp.exmail.qq.com",
				new SimpleAuthenticator("sunflower@idonoo.com", "daizhong1123"));
		SendMailSession session = smtpServer.createSession();
		session.open();

		Email email = Email.create().from("sunflower@idonoo.com")
				.to("archer.dzh@gmail.com").subject("TestMail")
				.addText("A plain text message...");
		email = Email
				.create()
				.from("sunflower@idonoo.com")
				.to("archer.dzh@gmail.com")
				.subject("TestMail")
				.addHtml("<ol><li>123</li><li>456</li><li>789</li></ol>",
						"utf-8");
		session.sendMail(email);
		session.close();
	}

}
