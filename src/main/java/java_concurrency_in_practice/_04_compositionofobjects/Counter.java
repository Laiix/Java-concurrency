package java_concurrency_in_practice._04_compositionofobjects;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Counter {

    @GuardedBy("this")private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized  long increment() {
        if(value==Long.MAX_VALUE)
            throw new IllegalStateException("counter overflow");
        return ++value;
    }


}
