/**
 * 
 */
package ehcache;

import org.junit.Test;

/**
 * @author dzh
 * @date Nov 12, 2016 11:36:16 AM
 * @since 1.0
 */
public class TestEventListener {

    @Test
    public void eventListenerTest() {
        // CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration =
        // CacheEventListenerConfigurationBuilder
        // .newEventListenerConfiguration(new ListenerObject(), EventType.CREATED,
        // EventType.UPDATED).unordered().asynchronous();
        //
        // final CacheManager manager = CacheManagerBuilder.newCacheManagerBuilder()
        // .withCache("foo",
        // CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class,
        // ResourcePoolsBuilder.heap(10))
        // .add(cacheEventListenerConfiguration))
        // .build(true);
        //
        // CacheConfiguration<Long, String> cacheConfiguration =
        // CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
        // ResourcePoolsBuilder.heap(5L))
        // .withDispatcherConcurrency(10).withEventListenersThreadPool("listeners-pool").build();
        //
        // final Cache<String, String> cache = manager.getCache("foo", String.class, String.class);
        // cache.put("Hello", "World");
        // cache.put("Hello", "Everyone");
        // cache.remove("Hello");
    }

    @Test
    public void runtimeEventListener() {
        // ListenerObject listener = new ListenerObject();
        // cache.getRuntimeConfiguration().registerCacheEventListener(listener, EventOrdering.ORDERED,
        // EventFiring.ASYNCHRONOUS,
        // EnumSet.of(EventType.CREATED, EventType.REMOVED));
        //
        // cache.put(1L, "one");
        // cache.put(2L, "two");
        // cache.remove(1L);
        // cache.remove(2L);
        //
        // cache.getRuntimeConfiguration().deregisterCacheEventListener(listener);
        //
        // cache.put(1L, "one again");
        // cache.remove(1L);
    }

}
