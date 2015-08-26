/**
 * 
 */
package apns;

import java.util.Date;
import java.util.Map;

import org.junit.Test;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

/**
 * @author dzh
 * @date Oct 15, 2014 7:01:27 PM
 * @since 1.0
 */
public class TestApns {

	@Test
	public void testApns() {
		ApnsService service = APNS
				.newService()
				.withCert("/home/dzh/temp/apple/developer/sharecar.p12",
						"123456").withSandboxDestination().build();
		String payload = APNS.newPayload().alertTitle("爱拼车22")
				.alertBody("Can't be simpler than this!").build();
		String token = "857309a68c3fe80751b65a0c6c9f394960ebc1a1942dc5219ec46d9e40ed5ace";
		// String token = "83c02996f76268c6b569943cd42feec6"
		service.push(token, payload);

		service.testConnection();
		Map<String, Date> inactiveDevices = service.getInactiveDevices();
		for (String deviceToken : inactiveDevices.keySet()) {
			Date inactiveAsOf = inactiveDevices.get(deviceToken);
			System.out.println(deviceToken);
			System.out.println(inactiveAsOf);
		}
	}
}
