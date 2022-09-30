package java_concurrency_in_practice._01_intro;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class UnsafeSequence {
    private int value;

    /**
     * 返回一个唯一的数值
     * @return
     */
    public int getNext() {
        return value++;//非原子操作
    }
}
