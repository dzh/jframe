/**
 * 
 */
package ehcache;

import java.io.File;

import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentUserManagedCache;
import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.LocalPersistenceService;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.impl.config.persistence.UserManagedPersistenceContext;
import org.ehcache.impl.persistence.DefaultLocalPersistenceService;

/**
 * @author dzh
 * @date Nov 10, 2016 6:11:24 PM
 * @since 1.0
 */
public class TestUserManagedCache {

    public void init() {
        UserManagedCache<Long, String> userManagedCache = UserManagedCacheBuilder
                .newUserManagedCacheBuilder(Long.class, String.class).build(false);
        userManagedCache.init();

        userManagedCache.put(1L, "da one!");

        userManagedCache.close();
    }

    public void persistenceServiceTest() {
        LocalPersistenceService persistenceService = new DefaultLocalPersistenceService(
                new DefaultPersistenceConfiguration(new File("", "myUserData")));

        PersistentUserManagedCache<Long, String> cache = UserManagedCacheBuilder
                .newUserManagedCacheBuilder(Long.class, String.class)
                .with(new UserManagedPersistenceContext<Long, String>("cache-name", persistenceService))
                .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10L, EntryUnit.ENTRIES).disk(10L,
                        MemoryUnit.MB, true))
                .build(true);

        // Work with the cache
        cache.put(42L, "The Answer!");
        // assertThat(cache.get(42L), is("The Answer!"));

        cache.close();
        try {
            cache.destroy();
        } catch (CachePersistenceException e) {
            e.printStackTrace();
        }

        persistenceService.stop();
    }

    public void eventListenerTest() {
        // UserManagedCache<Long, String> cache = UserManagedCacheBuilder
        // .newUserManagedCacheBuilder(Long.class, String.class)
        // .withEventExecutors(Executors.newSingleThreadExecutor(),
        // Executors.newFixedThreadPool(5))
        // .withEventListeners(CacheEventListenerConfigurationBuilder
        // .newEventListenerConfiguration(ListenerObject.class,
        // EventType.CREATED, EventType.UPDATED)
        // .asynchronous().unordered())
        // .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(3,
        // EntryUnit.ENTRIES))
        // .build(true);
        //
        // cache.put(1L, "Put it");
        // cache.put(1L, "Update it");
        //
        // cache.close();
    }

}
