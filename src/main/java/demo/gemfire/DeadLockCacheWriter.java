package demo.gemfire;

import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheWriterAdapter;

import java.util.concurrent.Semaphore;

public class DeadLockCacheWriter extends CacheWriterAdapter<String, String> {

    private final Semaphore semaphore = new Semaphore(1);
    @Override
    public void beforeUpdate(EntryEvent<String, String> entryEvent) throws CacheWriterException {
        beforeCreate(entryEvent);
    }

    @Override
    public void beforeCreate(EntryEvent<String, String> entryEvent) throws CacheWriterException {

        if("lock".equals(entryEvent.getKey())){
            String value = entryEvent.getNewValue();
            if("lock".equals(value)){
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new CacheWriterException(e);
                }
            }else if ("unlock".equals(value)){
                semaphore.release();
            }
        }
    }
}
