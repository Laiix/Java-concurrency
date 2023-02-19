package java_concurrency_in_practice._05_basicbuildingblocks;

import java.util.Map;
import java.util.concurrent.*;

public class Memoizer3<A, V> implements Computable<A, V>{

    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    @Override
    public V compute(final A arg) throws InterruptedException {
       Future<V> f = cache.get(arg);
       if(f==null) {
           Callable<V> eval = new Callable<V>() {
               @Override
               public V call() throws Exception {
                   return c.compute(arg);
               }
           };
           FutureTask<V> ft = new FutureTask<>(eval);
           f = ft;
           cache.put(arg, ft);
           ft.run();//在这里调用c.compute
       }
       try {
           return f.get();
       } catch (ExecutionException e) {
           throw launderThrowable(e);
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
}
