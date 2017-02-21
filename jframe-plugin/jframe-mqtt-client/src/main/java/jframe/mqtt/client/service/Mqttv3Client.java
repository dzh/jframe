/**
 * 
 */
package jframe.mqtt.client.service;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import jframe.core.plugin.annotation.Service;

/**
 * @author dzh
 * @date Jul 26, 2016 9:42:58 AM
 * @since 1.0
 */
@Service(clazz = "jframe.mqtt.client.service.impl.Mqttv3ClientImpl", id = "jframe.service.mqttv3client")
public interface Mqttv3Client {

    IMqttDeliveryToken publish(String id, String topic, MqttMessage message);

    IMqttDeliveryToken publish(String id, String topic, MqttMessage message, Object userContext,
            IMqttActionListener callback);

    IMqttAsyncClient borrowMqttClient(String id);

    void returnMqttClient(String id, IMqttAsyncClient mqttClient);

}
