/**
 * 
 */
package es;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import jframe.demo.elasticsearch.weike.MemberDO;

/**
 * @author dzh
 * @date Oct 20, 2016 4:44:43 PM
 * @since 1.0
 */
@Ignore
public class TestJsonPath {

    static Gson Gson = new Gson();

    @Test
    public void testRead() {
        String json = "{\"_scroll_id\":\"cXVlcnlBbmRGZXRjaDsxOzE2NTUwMDc6c0x6bWo0eERTSTZyYUdZVG9LYThfQTswOw==\",\"took\":2994,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"failed\":0},\"hits\":{\"total\":117375727,\"max_score\":null,\"hits\":[{\"_index\":\"weike\",\"_type\":\"member\",\"_id\":\"AVeslHLGT4gPDlYJn1J2\",\"_score\":null,\"_source\":{\"birthday\":0,\"lm\":1447897716678,\"creditLevel\":0,\"relationSource\":0,\"fstp\":0,\"lt\":0,\"itemCloseCount\":0,\"type\":0,\"tradeFroms\":[\"WAP\"],\"tc\":0,\"ta\":0,\"minp\":0,\"province\":13,\"buyerNick\":\"闽丫丫\",\"receiverName\":\"陆小姐\",\"grade\":0,\"tradeAmount\":102.48,\"closeTradeAmount\":0,\"ft\":0,\"black\":false,\"itemNum\":1,\"closeTradeCount\":0,\"lastEdmTime\":0,\"hasRefund\":true,\"buyerId\":0,\"emailType\":2,\"avgPrice\":102.48,\"giveNBRate\":false,\"lastCouponTimeEnd\":0,\"tradeCount\":1,\"email\":\"sunny8286@163.com\",\"ap\":0,\"address\":\"莲前街道新景中心B2010\",\"items\":[523045242297],\"sellerId\":479184430,\"registered\":0,\"goodRate\":0,\"lastTradeTime\":1447256536000,\"lastSmsTime\":0,\"bizOrderId\":1403847313137758,\"maxp\":0,\"mobile\":\"18659211097\"},\"sort\":[0]},{\"_index\":\"weike\",\"_type\":\"member\",\"_id\":\"AVeslHLGT4gPDlYJn1J3\",\"_score\":null,\"_source\":{\"birthday\":0,\"lm\":1448650655763,\"creditLevel\":0,\"relationSource\":1,\"fstp\":0,\"lt\":0,\"itemCloseCount\":0,\"type\":0,\"city\":\"150100\",\"tradeFroms\":[\"WAP\"],\"tc\":0,\"ta\":0,\"minp\":0,\"province\":150000,\"buyerNick\":\"pengran0727\",\"receiverName\":\"彭冉\",\"grade\":1,\"tradeAmount\":238.63,\"closeTradeAmount\":0,\"ft\":0,\"black\":false,\"itemNum\":2,\"status\":\"normal\",\"lastEdmTime\":0,\"closeTradeCount\":0,\"hasRefund\":false,\"buyerId\":0,\"emailType\":0,\"groupIds\":\"418525357\",\"avgPrice\":238.63,\"giveNBRate\":false,\"lastCouponTimeEnd\":0,\"tradeCount\":1,\"ap\":0,\"address\":\"新华西街新华桥农行营业厅（监狱管理局西侧）\",\"items\":[522190672466,522917969407],\"sellerId\":479184430,\"registered\":0,\"goodRate\":0,\"lastTradeTime\":1447256537000,\"lastSmsTime\":0,\"bizOrderId\":0,\"maxp\":0,\"mobile\":\"13624848066\"},\"sort\":[1]}]}}";
        long start = System.currentTimeMillis();
        DocumentContext context = JsonPath.parse(json);
        List<MemberDO> source = context.read("$.hits.hits.._source");
        String scrollId = context.read("$._scroll_id");
        int total = context.<Integer> read("$.hits.total");
        System.out.println(System.currentTimeMillis() - start);

        // System.out.println(scrollId);
        System.out.println(total);
        // System.out.println(source);

        start = System.currentTimeMillis();
        Gson gson = new Gson();
        Map<String, String> obj = gson.fromJson(json, HashMap.class);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(obj.get("_scroll_id"));
        // System.out.println((obj.get("hits")).get("total"));
        // System.out.println(source);

        List<Map<String, String>> list = new LinkedList<Map<String, String>>();
        Map map = new HashMap<>();
        map.put("a", "1");
        list.add(map);
        map = new HashMap<>();
        map.put("a", "2");
        list.add(map);
        json = Gson.toJson(list);
        context = JsonPath.parse(json);
        System.out.println(context.<List> read("$..a"));

    }

}
