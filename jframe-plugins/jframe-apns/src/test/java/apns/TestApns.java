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
				.withCert("/home/dzh/share/dono/bak/shareCar.p12", "sharecar")
				.withProductionDestination().build();
		String payload = APNS.newPayload().alertTitle("爱拼车")
				.alertBody("Can't be simpler than this!").build();
		String token = "857309a68c3fe80751b65a0c6c9f394960ebc1a1942dc5219ec46d9e40ed5ace";
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
