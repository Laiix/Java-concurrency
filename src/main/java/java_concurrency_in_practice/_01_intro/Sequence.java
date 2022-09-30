package java_concurrency_in_practice._01_intro;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Sequence {
    @GuardedBy("this") private int value;

    /**
     * 返回一个唯一的数值
     * @return
     */
    public synchronized int getNext() {
        return value++;//非原子操作
    }
}
