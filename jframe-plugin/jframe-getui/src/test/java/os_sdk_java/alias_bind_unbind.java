package os_sdk_java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IAliasResult;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;

public class alias_bind_unbind {

	static String appId = "";
	static String appkey = "";
	static String master = "";
	static String CID = "";
	static String Alias = "";

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// // 单个ClientId别名绑定
		 bindAlias();
		 // 根据别名查询ClientId
		queryClientId();
		// //根据ClientId查询别名
		 queryAilas();
		// // //解除单个Client的别名绑定
		 AliasUnBind();
		// // // //相同别名绑定多个ClientId的功能
		bindAliasAll();
		// // // //解除别名下的所有ClientId绑定
		 AliasUnBindAll();
	}

	public static void bindAlias() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);

		// 单个CID绑定别名
		IAliasResult bindSCid = push.bindAlias(appId, Alias, CID);
		System.out.println("绑定结果：" + bindSCid.getResult() + "错误码:"
				+ bindSCid.getErrorMsg());
	}

	public static void queryClientId() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		IAliasResult queryClient = push.queryClientId(appId, Alias);
		System.out.println("根据别名获取的CID：" + queryClient.getClientIdList());
	}

	public static void queryAilas() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		IAliasResult queryRet = push.queryAlias(appId, CID);
		System.out.println("根据cid获取别名：" + queryRet.getAlias());

	}

	public static void AliasUnBind() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		IAliasResult AliasUnBind = push.unBindAlias(appId, Alias, CID);
		System.out.println("解除绑定结果:" + AliasUnBind.getResult());
	}

	public static void bindAliasAll() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);

		List<Target> Lcids = new ArrayList<Target>();


			Target target = new Target();
			target.setClientId(CID);
			target.setAlias(Alias);
			Lcids.add(target);
		IAliasResult bindAliasAll = push.bindAlias(appId, Lcids);
		System.out.println("绑定结果:" + bindAliasAll.getResult());
	}

	public static void AliasUnBindAll() throws Exception {
		IGtPush push = new IGtPush(host, appkey, master);
		IAliasResult AliasUnBindAll = push.unBindAliasAll(appId, Alias);
		System.out.println("解除绑定结果:" + AliasUnBindAll.getResult());
	}

}
