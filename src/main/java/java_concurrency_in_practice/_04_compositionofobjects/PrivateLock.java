package java_concurrency_in_practice._04_compositionofobjects;

import net.jcip.annotations.GuardedBy;

public class PrivateLock {
    private final Object myLock = new Object();

    @GuardedBy("myLock") Widget widget;

    void someMethod() {
        synchronized (myLock) {
            //访问或者修改Widget的状态
        }
    }
}
