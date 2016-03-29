package push.android;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import push.AndroidNotification;

public class AndroidCustomizedcast extends AndroidNotification {
	public AndroidCustomizedcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "customizedcast");	
	}
	
	public void setAlias(String alias,String aliasType) throws Exception {
    	setPredefinedKeyValue("alias", alias);
    	setPredefinedKeyValue("alias_type", aliasType);
    }
			
	public void setFileId(String fileId,String aliasType) throws Exception {
    	setPredefinedKeyValue("file_id", fileId);
    	setPredefinedKeyValue("alias_type", aliasType);
    }

}
