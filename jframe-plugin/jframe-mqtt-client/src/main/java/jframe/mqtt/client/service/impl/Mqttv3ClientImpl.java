package jframe.mqtt.client.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jframe.core.conf.Config;
import jframe.core.plugin.annotation.InjectPlugin;
import jframe.core.plugin.annotation.Injector;
import jframe.core.plugin.annotation.Start;
import jframe.core.plugin.annotation.Stop;
import jframe.mqtt.client.MqttClientConf;
import jframe.mqtt.client.MqttClientPlugin;
import jframe.mqtt.client.service.Mqttv3Client;

/**
 * <p>
 * <li>connection pool</li>
 * <li>TODO persistence</li>
 * </p>
 * 
 * @author dzh
 * @date Jul 26, 2016 9:44:26 AM
 * @since 1.0
 */
@Injector
public class Mqttv3ClientImpl implements Mqttv3Client {

    static Logger LOG = LoggerFactory.getLogger(Mqttv3ClientImpl.class);

    @InjectPlugin
    static MqttClientPlugin Plugin;

    private Map<String, ObjectPool<IMqttAsyncClient>> clnt;

    @Start
    void start() {
        LOG.info("Mqttv3Client starting!");
        try {
            String file = Plugin.getConfig("file.mqttclient", Plugin.getConfig(Config.APP_HOME) + File.separator
                    + "conf" + File.separator + "mqttclient.properties");
            init(new FileInputStream(file));
            LOG.info("Mqttv3Client starting successfully!");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

    public void init(InputStream file) throws Exception {
        MqttClientConf props = new MqttClientConf();
        props.init(file);

        String[] ids = props.getGroupIds();
        clnt = new HashMap<>(ids.length, 1);
        for (String id : ids) {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(props.getConfInt(id, MqttClientConf.F_pool_maxTotal, "100"));
            config.setMaxIdle(props.getConfInt(id, MqttClientConf.F_pool_maxIdle, "10"));
            config.setMinIdle(props.getConfInt(id, MqttClientConf.F_pool_minIdle, "1"));
            clnt.put(id, new GenericObjectPool<IMqttAsyncClient>(new MqttAsyncClientFactory(id, props), config));
        }
    }

    @Stop
    public void stop() {
        LOG.info("Mqttv3Client stoping!");

        if (clnt != null) {
            for (Entry<String, ObjectPool<IMqttAsyncClient>> entry : clnt.entrySet()) {
                try {
                    entry.getValue().close();
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e.fillInStackTrace());
                }
            }
            clnt = null;
        }

        LOG.info("Mqttv3Client finish stoping!");
    }

    @Override
    public IMqttDeliveryToken publish(String id, String topic, MqttMessage message) {
        return publish(id, topic, message, null, null);
    }

    @Override
    public IMqttDeliveryToken publish(String id, String topic, MqttMessage message, Object userContext,
            IMqttActionListener callback) {
        IMqttAsyncClient mqtt = null;
        try {
            mqtt = clnt.get(id).borrowObject();
            return mqtt.publish(topic, message, userContext, callback);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        } finally {
            if (mqtt != null) {
                try {
                    clnt.get(id).returnObject(mqtt);
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    @Override
    public IMqttAsyncClient borrowMqttClient(String id) {
        try {
            return clnt.get(id).borrowObject();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    @Override
    public void returnMqttClient(String id, IMqttAsyncClient mqttClient) {
        try {
            clnt.get(id).returnObject(mqttClient);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.fillInStackTrace());
        }
    }

}
