/**
 * 
 */
package ehcache;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.UserManagedCache;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.xml.XmlConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://www.ehcache.org/documentation/3.1/xsds.html
 * 
 * @author dzh
 * @date Nov 4, 2016 5:08:07 PM
 * @since 1.0
 */
public class TestEhcache {

    private CacheManager cacheManager;

    static Logger LOG = LoggerFactory.getLogger(TestEhcache.class);

    @Before
    public void init() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfigured",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10))
                                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(20, TimeUnit.SECONDS))))
                .build();
        cacheManager.init();

        // PersistentCacheManager persistentCacheManager =
        // CacheManagerBuilder.newCacheManagerBuilder()
        // .with(CacheManagerBuilder.persistence(getStoragePath() +
        // File.separator + "myData"))
        // .withCache("threeTieredCache",
        // CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class,
        // String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10,
        // EntryUnit.ENTRIES)
        // .offheap(1, MemoryUnit.MB).disk(20, MemoryUnit.MB)))
        // .build(true);
        // persistentCacheManager.close();
    }

    @Test
    public void xmlConfigTest() {
        final URL myUrl = this.getClass().getResource("ehcache.xml");
        Configuration xmlConfig = new XmlConfiguration(myUrl);
        CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        //
        // XmlConfiguration xmlConfiguration = new XmlConfiguration(
        // getClass().getResource("/configs/docs/template-sample.xml"));
        // CacheConfigurationBuilder<Long, String> configurationBuilder =
        // xmlConfiguration
        // .newCacheConfigurationBuilderFromTemplate("example", Long.class,
        // String.class);
        // configurationBuilder =
        // configurationBuilder.withResourcePools(ResourcePoolsBuilder.heap(1000));
    }

    @Test
    public void cacheConfigTest() {
        CacheConfiguration<Long, String> usesConfiguredInCacheConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, MemoryUnit.KB).offheap(10,
                                MemoryUnit.MB))
                .withSizeOfMaxObjectGraph(1000).withSizeOfMaxObjectSize(1000, MemoryUnit.B).build();

        CacheConfiguration<Long, String> usesDefaultSizeOfEngineConfig = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(10, MemoryUnit.KB).offheap(10, MemoryUnit.MB))
                .build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withDefaultSizeOfMaxObjectSize(500, MemoryUnit.B).withDefaultSizeOfMaxObjectGraph(2000)
                .withCache("usesConfiguredInCache", usesConfiguredInCacheConfig)
                .withCache("usesDefaultSizeOfEngine", usesDefaultSizeOfEngineConfig).build(true);

        Cache<Long, String> usesConfiguredInCache = cacheManager.getCache("usesConfiguredInCache", Long.class,
                String.class);
        usesConfiguredInCache.put(1L, "one");
        // assertThat(usesConfiguredInCache.get(1L), equalTo("one"));

        Cache<Long, String> usesDefaultSizeOfEngine = cacheManager.getCache("usesDefaultSizeOfEngine", Long.class,
                String.class);
        usesDefaultSizeOfEngine.put(1L, "one");
        // assertThat(usesDefaultSizeOfEngine.get(1L), equalTo("one"));

        cacheManager.close();
    }

    @Test
    public void udpateResourcePool() {
        // ResourcePools pools =
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(20L,
        // EntryUnit.ENTRIES).build();
        // cache.getRuntimeConfiguration().updateResourcePools(pools);
        // assertThat(
        // cache.getRuntimeConfiguration().getResourcePools().getPoolForResource(ResourceType.Core.HEAP).getSize(),
        // is(20L));
    }

    @Test
    public void createCacheTest() {
        Cache<Long, String> myCache = cacheManager.createCache("myCache", CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)).build());

        myCache.put(1L, "da one!");
        String value = myCache.get(1L);
        LOG.info(value);
    }

    /**
     * -XX:MaxDirectMemorySize
     */
    @Test
    public void offHeadpManagedTest() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("tieredCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).offheap(10,
                                        MemoryUnit.MB)))
                .build(true);

        cacheManager.close();
    }

    @Test
    public void diskManagedTest() {
        String path = "";
        PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(path + File.separator + "myData"))
                .withCache("persistent-cache",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(Long.class,
                                        String.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                                                .heap(10, EntryUnit.ENTRIES).disk(10, MemoryUnit.MB, true)))
                .build(true);

        persistentCacheManager.close();
    }

    @Test
    @Ignore
    public void userManagedCacheTest() {
        UserManagedCache<Long, String> userManagedCache = UserManagedCacheBuilder
                .newUserManagedCacheBuilder(Long.class, String.class).build(false);
        userManagedCache.init();

        userManagedCache.put(1L, "da one!");
        userManagedCache.close();

        String value = userManagedCache.get(1L);
        LOG.info(value);
    }

    @Test
    public void removeCacheTest() {
        Cache<Long, String> preConfigured = cacheManager.getCache("preConfigured", Long.class, String.class);
        cacheManager.removeCache("preConfigured");

    }

    @After
    public void close() {
        cacheManager.close();
    }

}
