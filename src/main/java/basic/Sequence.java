package basic;

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
