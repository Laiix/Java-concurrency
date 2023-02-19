package java_concurrency_in_practice._05_basicbuildingblocks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Preloader {

    private final FutureTask<ProductInfo> future =
            new FutureTask<ProductInfo>(new Callable<ProductInfo>() {
        @Override
        public ProductInfo call() throws Exception {
            return loadProductInfo();
        }
    });

    private final Thread thread = new Thread(future);

    public void start() {
        thread.start();
    }

    public ProductInfo get() throws InterruptedException, DataLoadException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if(cause instanceof DataLoadException) {
                throw (DataLoadException)cause;
            } else {
                throw launderThrowable(cause);
            }
        }
    }

    /**
     * 如果 Throwable是Error, 那么抛出它；如果是RuntimeException, 那么返回它，否则抛出 IllegalStateException
     * @param t
     * @return
     */
    private RuntimeException launderThrowable(Throwable t) {
        if(t instanceof RuntimeException)
            return (RuntimeException) t;
        else if(t instanceof Error)
            throw (Error)t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }


    private ProductInfo loadProductInfo() {
        return null;
    }
}
