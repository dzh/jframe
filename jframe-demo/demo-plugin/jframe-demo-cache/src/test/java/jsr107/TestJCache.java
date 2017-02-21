/**
 * 
 */
package jsr107;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Nov 4, 2016 5:10:25 PM
 * @since 1.0
 */
public class TestJCache {

    static Logger LOG = LoggerFactory.getLogger(TestJCache.class);

    @Test
    public void createCacheTest() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();
        MutableConfiguration<Long, String> configuration = new MutableConfiguration<Long, String>()
                .setTypes(Long.class, String.class).setStoreByValue(false)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));
        Cache<Long, String> cache = cacheManager.createCache("jCache", configuration);
        cache.put(1L, "one");
        String value = cache.get(1L);
        LOG.info(value);

    }

    @Test
    public void cacheConfigTest() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        MutableConfiguration<Long, String> configuration = new MutableConfiguration<Long, String>();
        configuration.setTypes(Long.class, String.class);
        Cache<Long, String> cache = cacheManager.createCache("someCache", configuration);

        CompleteConfiguration<Long, String> completeConfiguration = cache.getConfiguration(CompleteConfiguration.class);

        Eh107Configuration<Long, String> eh107Configuration = cache.getConfiguration(Eh107Configuration.class);

        CacheRuntimeConfiguration<Long, String> runtimeConfiguration = eh107Configuration
                .unwrap(CacheRuntimeConfiguration.class);
    }

    @Test
    public void configEhcache2Jsr107() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build();

        Cache<Long, String> cache = cacheManager.createCache("myCache",
                Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));

        Eh107Configuration<Long, String> configuration = cache.getConfiguration(Eh107Configuration.class);
        configuration.unwrap(CacheConfiguration.class);

        configuration.unwrap(CacheRuntimeConfiguration.class);

        try {
            cache.getConfiguration(CompleteConfiguration.class);
            throw new AssertionError("IllegalArgumentException expected");
        } catch (IllegalArgumentException iaex) {
            // Expected
        }
    }

    @Test
    public void configFromEchache() {
//        CachingProvider cachingProvider = Caching.getCachingProvider();
//        CacheManager manager = cachingProvider.getCacheManager(
//                getClass().getResource("/org/ehcache/docs/ehcache-jsr107-config.xml").toURI(),
//                getClass().getClassLoader());
//        Cache<Long, Product> readyCache = manager.getCache("ready-cache", Long.class, Product.class);
    }

}
