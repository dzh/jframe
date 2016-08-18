/**
 * 
 */
package jframe.httpclient;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jframe.core.util.PropsConf;

/**
 * TODO 配置文件验证
 * 
 * @author dzh
 * @date Dec 3, 2014 11:07:29 AM
 * @since 1.0
 */
public class HttpClientConfig {

    private static final PropsConf CONFIG = new PropsConf();

    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String GROUP = "group";

    public static final String M_POST = "post";
    public static final String M_GET = "get";

    public static final String HTTP_METHOD = "http.method";
    public static final String HTTP_MAX_CONN = "http.max.conn";
    public static final String HTTP_MAX_CONN_ROUTE = "http.max.conn.route";
    public static final String HTTP_IDLE_CONN_CLOSE = "http.idle.conn.close";
    public static final String HTTP_KEEP_ALIVE = "http.keep-alive";
    public static final String HTTP_CHARSET = "http.charset";
    public static final String HTTP_SO_TIMEOUT = "http.so.timeout";
    public static final String HTTP_CONN_TIMEOUT = "http.conn.timeout";

    public static void init(String file) throws Exception {
        CONFIG.init(file);
    }

    public static String getRandomHost() {
        return Arrays.asList(getHosts()).get(new Random().nextInt(getHosts().length));
    }

    public static String[] getHosts() {
        return CONFIG.getGroupIds();
    }

    public static String getConf(String group, String key, String defVal) {
        return CONFIG.getConf(group, key, defVal);
    }

    public static String getConf(String group, String key) {
        return CONFIG.getConf(group, key, "");
    }

    /**
     * 
     * @param gid
     * @return
     */
    public static List<String> getHostByGroup(String gid) {
        List<String> hosts = new LinkedList<String>();
        for (String host : getHosts()) {
            if (gid.equals(CONFIG.getConf(host, GROUP))) {
                hosts.add(host);
            }
        }
        return hosts;
    }
}
