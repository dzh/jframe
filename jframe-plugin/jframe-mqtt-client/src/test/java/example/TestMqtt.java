/**
 * 
 */
package example;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzh
 * @date Jul 31, 2016 7:25:04 PM
 * @since 1.0
 */
@Ignore
public class TestMqtt {

    int qos = 2;
    String broker = "tcp://127.0.0.1:1883";
    MemoryPersistence persistence = new MemoryPersistence();

    MqttClient pubClient;

    MqttClient subClient;

    @Before
    public void init() {
        try {
            pubClient = new MqttClient(broker, "pubClient", persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            pubClient.connect(connOpts);

            subClient = new MqttClient(broker, "subClient", persistence);
            connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            subClient.connect(connOpts);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    @Test
    public void testPubSub() {
        try {
            subClient.subscribe("x/+/z", new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(topic + "-" + message.toString() + "-" + new Date());
                }
            });

            String content = "1111";
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(1);
            message.setRetained(false);
            pubClient.publish("x/y/z", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void testHash() {
        String s = "1111";
        System.out.println(s.hashCode());
        s = "1110";
        System.out.println(s.hashCode());
    }

    @After
    public void stop() {
        try {
            pubClient.disconnect();
            subClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pubClient.close();
                subClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }

        }
    }

}
