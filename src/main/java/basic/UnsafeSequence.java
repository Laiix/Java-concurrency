package basic;

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
