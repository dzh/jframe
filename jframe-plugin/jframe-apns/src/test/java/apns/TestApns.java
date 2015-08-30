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
		ApnsService service = APNS.newService()
				.withCert("/Users/dzh/temp/lech/drivercer.p12", "123456")
				.withSandboxDestination().build();
		String payload = APNS.newPayload()
				// .alertTitle("爱拼车22")
				.customField("custom1", "custom1")
				.alertBody("Can't be simpler than this!").build();
		System.out.println(payload);
		String token = "70854405ac6b60b64bdc5338a2d8f4a55a683f63e786a872be42454f6731618d";
		token = "644737130b7d6dde50c1cf1e6fe6bb8be81f728d4b51fc357e3706e431bea213";
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
