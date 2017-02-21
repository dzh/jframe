/**
 * 
 */
package es;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzh
 * @date Sep 24, 2016 1:44:21 PM
 * @since 1.0
 */
@Ignore
public class TestTransportClient {

    TransportClient client;

    @Before
    public void init() throws Exception {
        Settings settings = Settings.settingsBuilder().put("cluster.name", "elasticsearch").put("client.transport.sniff", true)
                .put("client.transport.ignore_cluster_name", false).put("client.transport.ping_timeout", "5s")
                .put("client.transport.nodes_sampler_interval", "5s").build();

        TransportAddress addr = new InetSocketTransportAddress(InetAddress.getByName(null), 9300);
        client = TransportClient.builder().settings(settings).build()
                // .addTransportAddress(new
                // InetSocketTransportAddress(InetAddress.getByName("host1"),
                // 9300))
                .addTransportAddress(addr);
        // on shutdown
        System.out.println(client.toString());
    }

    @After
    public void stop() {
        client.close();
    }

    @Test
    public void testIndex() throws IOException {
        IndexResponse response = client.prepareIndex("twitter", "tweet", "1").setSource(XContentFactory.jsonBuilder().startObject()
                .field("user", "kimchy").field("postDate", new Date()).field("message", "trying out Elasticsearch").endObject()).get();
        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will
        // get: 1)
        long _version = response.getVersion();
        // isCreated() is true if the document is a new one, false if it has
        // been updated
        boolean created = response.isCreated();
        System.out.println(response.toString());

    }

    public void testIndexJsonString() {
        String json = "{" + "\"user\":\"kimchy\"," + "\"postDate\":\"2013-01-30\"," + "\"message\":\"trying out Elasticsearch\"" + "}";

        IndexResponse response = client.prepareIndex("twitter", "tweet").setSource(json).get();

    }

    @Test
    public void testGet() {
        GetResponse response = client.prepareGet("twitter", "tweet", "1").get();
        System.out.println(response.toString());
    }

    public void testDel() {
        DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").get();
        System.out.println(response.toString());
    }

    public void testUpdate() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("index");
        updateRequest.type("type");
        updateRequest.id("1");
        updateRequest.doc(XContentFactory.jsonBuilder().startObject().field("gender", "male").endObject());
        UpdateResponse response = client.update(updateRequest).get();
        System.out.println(response.toString());

        client.prepareUpdate("ttl", "doc", "1").setScript(new Script("ctx._source.gender = \"male\"", ScriptService.ScriptType.INLINE, null, null))
                .get();

        client.prepareUpdate("ttl", "doc", "1").setDoc(XContentFactory.jsonBuilder().startObject().field("gender", "male").endObject()).get();
    }

    @Test
    public void testUpsert() throws Exception {
        IndexRequest indexRequest = new IndexRequest("index", "type", "1")
                .source(XContentFactory.jsonBuilder().startObject().field("name", "Joe Smith").field("gender", "male").endObject());
        UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
                .doc(XContentFactory.jsonBuilder().startObject().field("gender", "male").endObject()).upsert(indexRequest);
        UpdateResponse response = client.update(updateRequest).get();
        System.out.println(response.getGetResult().sourceAsString());
    }

    @Test
    public void testMultiGet() {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet().add("twitter", "tweet", "1").add("twitter", "tweet", "2", "3", "4")
                .add("another", "type", "foo").get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println(json);
            }
        }
    }

    @Test
    public void testBulk() throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        // either use client#prepare, or use Requests# to directly build
        // index/delete requests
        bulkRequest.add(client.prepareIndex("twitter", "tweet", "1").setSource(XContentFactory.jsonBuilder().startObject().field("user", "kimchy")
                .field("postDate", new Date()).field("message", "trying out Elasticsearch").endObject()));

        bulkRequest.add(client.prepareIndex("twitter", "tweet", "2").setSource(XContentFactory.jsonBuilder().startObject().field("user", "kimchy")
                .field("postDate", new Date()).field("message", "another post").endObject()));

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
        }
    }

    @Test
    public void testBulkProcessor() throws Exception {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            }
        }).setBulkActions(10000).setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)).setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1).setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

        bulkProcessor.add(new IndexRequest("twitter", "tweet", "1").source(""));
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));

        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        // bulkProcessor.close();
    }

    @Test
    public void testSearch() {
        SearchResponse response = client.prepareSearch("index1", "index2").setTypes("type1", "type2").setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("multi", "test")) // Query
                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18)) // Filter
                .setFrom(0).setSize(60).setExplain(true).execute().actionGet();
    }

    @Test
    public void testScrollSearch() {
        QueryBuilder qb = QueryBuilders.termQuery("multi", "test");

        // 100 hits per shard will be returned for each scroll
        SearchResponse scrollResp = client.prepareSearch("index1").addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000)).setQuery(qb).setSize(100).execute().actionGet();
        // Scroll until no hits are returned
        while (true) {

            for (SearchHit hit : scrollResp.getHits().getHits()) {
                // Handle the hit...
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            // Break condition: No hits are returned
            if (scrollResp.getHits().getHits().length == 0) {
                break;
            }
        }
    }

    @Test
    public void testMultiSearch() {
        SearchRequestBuilder srb1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder srb2 = client.prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

        MultiSearchResponse sr = client.prepareMultiSearch().add(srb1).add(srb2).execute().actionGet();

        // You will get all individual responses from
        // MultiSearchResponse#getResponses()
        long nbHits = 0;
        for (MultiSearchResponse.Item item : sr.getResponses()) {
            SearchResponse response = item.getResponse();
            nbHits += response.getHits().getTotalHits();
        }
    }

    @Test
    public void testAggregation() {
        SearchResponse sr = client.prepareSearch().setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("agg1").field("field"))
                .addAggregation(AggregationBuilders.dateHistogram("agg2").field("birth").interval(DateHistogramInterval.YEAR)).execute().actionGet();

        // Get your facet results
        Terms agg1 = sr.getAggregations().get("agg1");
        // DateHistogram agg2 = sr.getAggregations().get("agg2");

        sr = client.prepareSearch("index1").setTerminateAfter(1000).get();
        if (sr.isTerminatedEarly()) {
            // We finished early
        }

        // sr = client.prepareSearch()
        // .addAggregation(
        // AggregationBuilders.terms("by_country").field("country")
        // .subAggregation(AggregationBuilders.dateHistogram("by_year")
        // .field("dateOfBirth")
        // .interval((DateHistogramInterval.YEAR)
        // .subAggregation(AggregationBuilders.avg("avg_children").field("children"))
        // )
        // ).execute().actionGet();

        MetricsAggregationBuilder aggregation = AggregationBuilders.max("agg").field("height");

    }

    public void testAdmin() {
        AdminClient adminClient = client.admin();
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        client.admin().indices().prepareCreate("twitter")
                .setSettings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2)).get();

        client.admin().indices().prepareCreate("twitter").addMapping("tweet", "{\n" + "    \"tweet\": {\n" + "      \"properties\": {\n"
                + "        \"message\": {\n" + "          \"type\": \"string\"\n" + "        }\n" + "      }\n" + "    }\n" + "  }").get();

        ClusterAdminClient clusterAdminClient = client.admin().cluster();
        ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
        String clusterName = healths.getClusterName();
        int numberOfDataNodes = healths.getNumberOfDataNodes();
        int numberOfNodes = healths.getNumberOfNodes();

        for (ClusterIndexHealth health : healths.getIndices().values()) {
            String index = health.getIndex();
            int numberOfShards = health.getNumberOfShards();
            int numberOfReplicas = health.getNumberOfReplicas();
            ClusterHealthStatus status = health.getStatus();
        }

        client.admin().cluster().prepareHealth().setWaitForYellowStatus().get();
        client.admin().cluster().prepareHealth("company").setWaitForGreenStatus().get();

        client.admin().cluster().prepareHealth("employee").setWaitForGreenStatus().setTimeout(TimeValue.timeValueSeconds(2)).get();

        ClusterHealthResponse response = client.admin().cluster().prepareHealth("company").setWaitForGreenStatus().get();

        ClusterHealthStatus status = response.getIndices().get("company").getStatus();
        if (!status.equals(ClusterHealthStatus.GREEN)) {
            throw new RuntimeException("Index is in " + status + " state");
        }
    }
}
