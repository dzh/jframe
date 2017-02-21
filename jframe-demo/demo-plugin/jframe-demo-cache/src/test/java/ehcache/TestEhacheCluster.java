/**
 * 
 */
package ehcache;

import java.net.URI;

import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.junit.Before;

/**
 * @author dzh
 * @date Nov 8, 2016 12:11:48 PM
 * @since 1.0
 */
public class TestEhacheCluster {

    @Before
    public void init() {
//        final CacheManagerBuilder<PersistentCacheManager> clusteredCacheManagerBuilder = CacheManagerBuilder
//                .newCacheManagerBuilder().with(ClusteringServiceConfigurationBuilder
//                        .cluster(URI.create("terracotta://localhost:9510/my-application")).autoCreate());
//        final PersistentCacheManager cacheManager = clusteredCacheManagerBuilder.build(true);
//        cacheManager.close();
    }

}
