package jframe.qcloud.model;

import java.util.Map;

/**
 * https://cloud.tencent.com/document/product/436/14048
 * 
 * <p>
 * "expiredTime": 1494563462,
 * "credentials": {
 * "sessionToken": "sessionTokenXXXXX",
 * "tmpSecretId": "tmpSecretIdXXXXX",
 * "tmpSecretKey": "tmpSecretKeyXXXXX"
 * }
 * </p>
 * 
 * 临时签名
 * 
 * @author dzh
 * @date Aug 5, 2018 1:13:36 AM
 * @version 0.0.1
 */
public class TmpSecret {
    private long expiredTime;
    private Map<String, String> credentials;

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
    }

    public String getSessionToken() {
        return credentials == null ? null : credentials.get("sessionToken");
    }

    public String getTmpSecretId() {
        return credentials == null ? null : credentials.get("tmpSecretId");
    }

    public String getTmpSecretKey() {
        return credentials == null ? null : credentials.get("tmpSecretKey");
    }

}
