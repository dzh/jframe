/**
 * 
 */
package ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @author dzh
 * @date Nov 12, 2016 11:43:36 AM
 * @since 1.0
 */
public class TestEviction {

    public void evictionTest() {
        CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(2L))
                .withEvictionAdvisor(new OddKeysEvictionAdvisor<Long, String>()).build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache("cache", cacheConfiguration)
                .build(true);

        Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);

        // Work with the cache
        cache.put(42L, "The Answer!");
        cache.put(41L, "The wrong Answer!");
        cache.put(39L, "The other wrong Answer!");

        cacheManager.close();
    }

}
