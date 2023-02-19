package java_concurrency_in_practice._03_sharingofobjects;

import net.jcip.annotations.NotThreadSafe;

/**
 * 非线程安全的可变整数类
 */
@NotThreadSafe
public class MutableInteger {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
