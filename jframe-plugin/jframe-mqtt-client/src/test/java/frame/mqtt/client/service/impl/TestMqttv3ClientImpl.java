/**
 * 
 */
package frame.mqtt.client.service.impl;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import jframe.mqtt.client.service.impl.Mqttv3ClientImpl;

/**
 * @author dzh
 * @date Jul 26, 2016 2:02:01 PM
 * @since 1.0
 */
@Ignore
public class TestMqttv3ClientImpl {

    static String ID = "clnt1";

    private Mqttv3ClientImpl client;

    @Before
    public void start() {
        client = new Mqttv3ClientImpl();
        try {
            client.init(this.getClass().getResourceAsStream("/mqttclient.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPublish() {
        String content = "2222";
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(1);
        message.setRetained(true);
        client.publish(ID, "test/x/y", message);

        IMqttAsyncClient subClient = null;
        try {
            subClient = client.borrowMqttClient(ID);
            subClient.subscribe("test/+/y", 1, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(topic + "-" + message.toString() + "-" + new Date());
                }
            });
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.returnMqttClient(ID, subClient);
        }
    }

    @Test
    public void testPublish2() {
        String ID = "clnt2";
        String content = "3333";
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(1);
        message.setRetained(true);
        client.publish(ID, "test/x/y", message);

        IMqttAsyncClient subClient = null;
        try {
            subClient = client.borrowMqttClient(ID);
            subClient.subscribe("test/+/y", 1, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(topic + "-" + message.toString() + "-" + new Date());
                }
            });
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (subClient != null)
                client.returnMqttClient(ID, subClient);
        }
    }

    @After
    public void stop() {
        client.stop();
    }

}
