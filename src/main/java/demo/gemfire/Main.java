package demo.gemfire;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.Region;


import java.io.Console;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.apache.geode.cache.execute.FunctionService.*;

public class Main {

    @SuppressWarnings("unchecked")
    private static void callFunction(Region region, String lockOrUnlock){
        Collection<String> results = (Collection<String>) onRegion(region)
                .withFilter( Set.of("lock"))
                .setArguments(lockOrUnlock).execute(DeadLockingFunction.ID).getResult();

        for( String serverResults : results){
            System.out.println("server result > " + serverResults);
        }
    }
    public static void main(String[] args) {

        try {
            ClientCache clientCache = new ClientCacheFactory()
                    .set("log-level", "config")
                    .addPoolLocator("localhost", 10334)
                    .create();

            // I want the function to run on specific server.   So on region with a specific key will be the easiest.
            final Region<Object, Object> region = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("test");

            //force the bucket to be created  - will look for another method.   There is one that pre-allocators all buckets.
            region.put("lock", "nothing");

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            callFunction(region, "lock"); // This will acquire the lock so call twice to induce a stuck thread
            Thread  t = new Thread(()->{
                callFunction(region, "lock");
                countDownLatch.countDown();
            });
            t.setDaemon(true);
            t.start();

            System.out.println("Press Enter to continue.");
            new Scanner(System.in).nextLine();
            callFunction(region, "unlock");
            countDownLatch.await();
            callFunction(region, "unlock");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}