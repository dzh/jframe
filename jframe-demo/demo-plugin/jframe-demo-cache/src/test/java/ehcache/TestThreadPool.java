/**
 * 
 */
package ehcache;

/**
 * @author dzh
 * @date Nov 11, 2016 6:49:56 PM
 * @since 1.0
 */
public class TestThreadPool {

    public void dickPoolTest() {
        // CacheManager cacheManager =
        // CacheManagerBuilder.newCacheManagerBuilder()
        // .using(PooledExecutionServiceConfigurationBuilder.newPooledExecutionServiceConfigurationBuilder()
        // .defaultPool("dflt", 0, 10).pool("defaultDiskPool", 1, 3).pool("cache2Pool", 2, 2).build())
        // .with(new CacheManagerPersistenceConfiguration(new File(getStoragePath(), "myData")))
        // .withDefaultDiskStoreThreadPool("defaultDiskPool")
        // .withCache("cache1",
        // CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).disk(10L,
        // MemoryUnit.MB)))
        // .withCache("cache2", CacheConfigurationBuilder
        // .newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES).disk(10L, MemoryUnit.MB))
        // .withDiskStoreThreadPool("cache2Pool", 2))
        // .build(true);
        //
        // Cache<Long, String> cache1 = cacheManager.getCache("cache1", Long.class, String.class);
        // Cache<Long, String> cache2 = cacheManager.getCache("cache2", Long.class, String.class);
        //
        // cacheManager.close();
    }

    public void writeBehindTest() {
        // CacheManager cacheManager =
        // CacheManagerBuilder.newCacheManagerBuilder()
        // .using(PooledExecutionServiceConfigurationBuilder.newPooledExecutionServiceConfigurationBuilder()
        // .defaultPool("dflt", 0, 10).pool("defaultWriteBehindPool", 1, 3).pool("cache2Pool", 2, 2).build())
        // .withDefaultWriteBehindThreadPool("defaultWriteBehindPool")
        // .withCache("cache1",
        // CacheConfigurationBuilder
        // .newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES))
        // .withLoaderWriter(new SampleLoaderWriter<Long, String>(singletonMap(41L, "zero")))
        // .add(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 3)
        // .queueSize(3).concurrencyLevel(1)))
        // .withCache("cache2",
        // CacheConfigurationBuilder
        // .newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES))
        // .withLoaderWriter(new SampleLoaderWriter<Long, String>(singletonMap(41L, "zero")))
        // .add(WriteBehindConfigurationBuilder.newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS, 3)
        // .useThreadPool("cache2Pool").queueSize(3).concurrencyLevel(2)))
        // .build(true);
        //
        // Cache<Long, String> cache1 = cacheManager.getCache("cache1", Long.class, String.class);
        // Cache<Long, String> cache2 = cacheManager.getCache("cache2", Long.class, String.class);
        //
        // cacheManager.close();
    }

    public void eventsTest() {
        // CacheManager cacheManager =
        // CacheManagerBuilder.newCacheManagerBuilder()
        // .using(PooledExecutionServiceConfigurationBuilder.newPooledExecutionServiceConfigurationBuilder()
        // .pool("defaultEventPool", 1, 3).pool("cache2Pool", 2, 2).build())
        // .withDefaultEventListenersThreadPool("defaultEventPool")
        // .withCache("cache1",
        // CacheConfigurationBuilder
        // .newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES))
        // .add(CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(new ListenerObject(),
        // EventType.CREATED, EventType.UPDATED)))
        // .withCache("cache2",
        // CacheConfigurationBuilder
        // .newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10, EntryUnit.ENTRIES))
        // .add(CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(new ListenerObject(),
        // EventType.CREATED, EventType.UPDATED))
        // .withEventListenersThreadPool("cache2Pool"))
        // .build(true);
        //
        // Cache<Long, String> cache1 = cacheManager.getCache("cache1", Long.class, String.class);
        // Cache<Long, String> cache2 = cacheManager.getCache("cache2", Long.class, String.class);
        //
        // cacheManager.close();
    }

}
