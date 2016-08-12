/**
 * 
 */
package jframe.mqtt.client.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import jframe.mqtt.client.MqttClientConf;

/**
 * @author dzh
 * @date Aug 12, 2016 2:52:26 PM
 * @since 1.0
 */
public class MqttAsyncClientFactory extends BasePooledObjectFactory<IMqttAsyncClient> {

    private final String id;
    private final MqttClientConf conf;
    private AtomicInteger incr = new AtomicInteger(0);

    MqttAsyncClientFactory(String id, MqttClientConf conf) {
        this.id = id;
        this.conf = conf;
    }

    @Override
    public IMqttAsyncClient create() throws Exception {
        String broker = conf.getConf(id, MqttClientConf.F_mqtt_broker);
        MqttAsyncClient mqttClient = new MqttAsyncClient(broker, createClientId(), createPersistence());
        mqttClient.connect(createConnectOptions()).waitForCompletion();
        return mqttClient;
    }

    private MqttConnectOptions createConnectOptions() {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setAutomaticReconnect(conf.getConfBool(id, MqttClientConf.F_mqtt_session_autoReconnect, "false"));
        connOpts.setCleanSession(conf.getConfBool(id, MqttClientConf.F_mqtt_session_clean, "false"));
        return connOpts;
    }

    private String createClientId() throws UnknownHostException {
        StringBuilder buf = new StringBuilder();
        buf.append(InetAddress.getLocalHost().getHostName());
        buf.append('-');
        buf.append((int) Math.random() * conf.getConfInt(id, MqttClientConf.F_pool_maxTotal, "100"));
        buf.append('-');
        buf.append(incr.incrementAndGet());
        return buf.toString();
    }

    private MqttClientPersistence createPersistence() throws Exception {
        return (MqttClientPersistence) Class.forName(conf.getConf(id, MqttClientConf.F_mqtt_persistence)).newInstance();
    }

    @Override
    public PooledObject<IMqttAsyncClient> wrap(IMqttAsyncClient obj) {
        return new DefaultPooledObject<IMqttAsyncClient>(obj);
    }

    @Override
    public void destroyObject(PooledObject<IMqttAsyncClient> p) throws Exception {
        IMqttAsyncClient client = p.getObject();
        try {
            client.disconnect();
        } finally {
            client.close();
        }
    }

}
