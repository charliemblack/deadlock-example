package demo.gemfire;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;

import java.util.concurrent.Semaphore;

public class DeadLockingFunction implements Function<String> {
    public static final String ID = "DeadLockingFunction";

    private final Semaphore semaphore = new Semaphore(1);
    private final Object lock = new Object();

    @Override
    public void execute(FunctionContext<String> functionContext) {

        String arg = functionContext.getArguments();
        ResultSender<Object> resultSender = functionContext.getResultSender();

        try {
            if("lock".equals(arg)){
                synchronized (lock) {
                    semaphore.acquire();
                    resultSender.lastResult("locked");
                }
            }else if ("unlock".equals(arg)){
                semaphore.release();
                resultSender.lastResult("unlocked");
            }else{
                resultSender.lastResult("error - argument is >" + arg +"<");
            }
        } catch (InterruptedException e) {
            resultSender.lastResult("exception - " + e.getMessage());
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean optimizeForWrite() {
        //If we run on a region goto the primary
        return true;
    }

    @Override
    public boolean isHA() {
        //don't try to re-run.
        return false;
    }
    @Override
    public boolean hasResult() {
        return true;
    }
}
