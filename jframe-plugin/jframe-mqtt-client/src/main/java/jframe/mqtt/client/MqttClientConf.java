/**
 * 
 */
package jframe.mqtt.client;

import jframe.core.util.PropsConf;

/**
 * @author dzh
 * @date Aug 12, 2016 3:18:34 PM
 * @since 1.0
 */
public class MqttClientConf extends PropsConf {

    public static String F_mqtt_broker = "mqtt.broker";
    public static String F_mqtt_session_clean = "mqtt.session.clean";
    public static String F_mqtt_session_autoReconnect = "mqtt.session.autoReconnect";
    public static String F_mqtt_persistence = "mqtt.persistence";
    public static String F_pool_maxTotal = "pool.maxTotal";
    public static String F_pool_maxIdle = "pool.maxIdle";
    public static String F_pool_minIdle = "pool.minIdle";

}
