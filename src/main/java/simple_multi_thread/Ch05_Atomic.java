package simple_multi_thread;

import java.util.concurrent.atomic.*;

public class Ch05_Atomic {
    public static void main(String[] args) throws InterruptedException {
        /**
         * Java从JDK1.5开始提供了java.util.concurrent.atomic包，方便程序员在多线程环境下，无锁的进行原子操作。原子变量的底层使用了
         * 处理器提供的原子指令，但是不同的CPU架构可能提供的原子指令不一样，也有可能需要某种形式的内部锁,所以该方法不能绝对保证线程不被阻塞。
         *
         * 在Atomic包里一共有12个类，四种原子更新方式，分别是原子更新基本类型，原子更新数组，原子更新引用和原子更新字段。Atomic包里的类
         * 基本都是使用Unsafe（Unsafe类是在sun.misc包下，不属于Java标准。但是很多Java的基础类库，包括一些被广泛使用的高性能开发库都是
         * 基于Unsafe类开发的，比如Netty、Cassandra、Hadoop、Kafka等。Unsafe类在提升Java运行效率，增强Java语言底层操作能力方面起了
         * 很大的作用。Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，同时也带来了指针的问题。过度的使用Unsafe类会使得出错的
         * 几率变大，因此Java官方并不建议使用的，官方文档也几乎没有。Oracle正在计划从Java 9中去掉Unsafe类，如果真是如此影响就太大了）
         * 实现的包装类
         *
         * 原子更新基本类型类:
         *  用于通过原子的方式更新基本类型，Atomic包提供了以下三个类：
         *      AtomicBoolean：原子更新布尔类型。
         *      AtomicInteger：原子更新整型。
         *      AtomicLong：原子更新长整型。
         *  AtomicInteger的常用方法如下：
         *      int addAndGet(int delta) ：以原子方式将输入的数值与实例中的值（AtomicInteger里的value）相加，并返回结果
         *      boolean compareAndSet(int expect, int update) ：如果输入的数值等于预期值，则以原子方式将该值设置为输入的值。
         *      int getAndIncrement()：以原子方式将当前值加1，注意：这里返回的是自增前的值。
         *      void lazySet(int newValue)：最终会设置成newValue，使用lazySet设置值后，可能导致其他线程在之后的一小段时间内
         *          还是可以读到旧的值。关于该方法的更多信息可以参考并发网翻译的一篇文章《AtomicLong.lazySet是如何工作的？》
         *      int getAndSet(int newValue)：以原子方式设置为newValue的值，并返回旧值。
         */
        testAtomicInteger();
        /**
         * Atomic包提供了三种基本类型的原子更新，但是Java的基本类型里还有char，float和double等。那么问题来了，如何原子的更新其他的
         * 基本类型呢？
         * Atomic包里的类基本都是使用Unsafe实现的，让我们一起看下Unsafe的源码，发现Unsafe只提供了三种CAS方法，compareAndSwapObject，
         * compareAndSwapInt和compareAndSwapLong，再看AtomicBoolean源码，发现其是先把Boolean转换成整型，再使用compareAndSwapInt
         * 进行CAS，所以原子更新double也可以用类似的思路来实现。
         *
         * 原子更新数组类型:
         *  通过原子的方式更新数组里的某个元素
         *  Atomic包提供了以下三个类：
         *      AtomicIntegerArray：原子更新整型数组里的元素。
         *      AtomicLongArray：原子更新长整型数组里的元素。
         *      AtomicReferenceArray：原子更新引用类型数组里的元素。
         *  AtomicIntegerArray类主要是提供原子的方式更新数组里的整型，其常用方法如下:
         *      int addAndGet(int i, int delta)：以原子方式将输入值与数组中索引i的元素相加。
         *      boolean compareAndSet(int i, int expect, int update)：如果当前值等于预期值，则以原子方式将数组位置i的元素设置成update值。
         */
        testAtomicIntegerArray();
        /**
         * AtomicIntegerArray类需要注意的是:
         *      数组value通过构造方法传递进去，然后AtomicIntegerArray会将当前数组复制一份，所以当AtomicIntegerArray对内部的
         *      数组元素进行修改时，不会影响到传入的数组。
         *
         * 原子更新引用类型:
         *  原子更新基本类型的AtomicInteger，只能更新一个变量，如果要原子的更新多个变量，就需要使用这个原子更新引用类型提供的类。
         *  Atomic包提供了以下三个类：
         *      AtomicReference：原子更新引用类型。
         *      AtomicReferenceFieldUpdater：原子更新引用类型里的字段。
         *      AtomicMarkableReference：原子更新带有标记位的引用类型。可以原子的更新一个布尔类型的标记位和引用类型。
         *          构造方法是AtomicMarkableReference(V initialRef, boolean initialMark)
         */
        testAtomicReference();
        testAtomicReferenceFieldUpdater();
        /**
         * 原子更新类字段:
         *  如果我们只需要某个类里的某个字段，那么就需要使用原子更新字段类，Atomic包提供了以下三个类：
         *      AtomicIntegerFieldUpdater：原子更新整型的字段的更新器。
         *      AtomicLongFieldUpdater：原子更新长整型字段的更新器。
         *      AtomicStampedReference：原子更新带有版本号的引用类型。该类将整数值与引用关联起来，可用于原子的更数据和数据的版本号，
         *          可以解决使用CAS进行原子更新时，可能出现的ABA问题。
         *  原子更新字段类都是抽象类，每次使用都时候必须使用静态方法newUpdater创建一个更新器。原子更新类的字段的必须使用public volatile修饰符。
         */
        testAtomicIntegerFieldUpdater();

    }

    private static void testAtomicIntegerFieldUpdater() {
        AtomicIntegerFieldUpdater<User> a = AtomicIntegerFieldUpdater
                .newUpdater(User.class, "old");
        User conan = new User("conan", 10);
        System.out.println(a.getAndIncrement(conan));
        System.out.println(a.get(conan));
    }

    private static void testAtomicReferenceFieldUpdater() {
        AtomicReferenceFieldUpdater<User, Integer> a = AtomicReferenceFieldUpdater
                .newUpdater(User.class,Integer.class, "old");
        User conan = new User("conan", 10);
        System.out.println(a.compareAndSet(conan, conan.getOld(), 18));//如果当前值 == 预期值，则以原子方式将此更新器管理的给定对象的字段设置为给定的更新值。对 compareAndSet 和 set 的其他调用，此方法可以确保原子性，但对于字段中的其他更改则不一定确保原子性。
        System.out.println(a.get(conan));
    }

    private static void testAtomicReference() {
        AtomicReference<User> atomicUserRef = new AtomicReference<>();
        User user = new User("conan", 15);
        atomicUserRef.set(user);
        User updateUser = new User("Shinichi", 17);
        atomicUserRef.compareAndSet(user, updateUser);
        System.out.println(atomicUserRef.get().getName());
        System.out.println(atomicUserRef.get().getOld());
    }

    static class User {
        private String name;
        public volatile Integer old;
        public User(String name, Integer old) {
            this.name = name;
            this.old = old;
        }
        public String getName() {
            return name;
        }
        public Integer getOld() {
            return old;
        }
    }

    private static void testAtomicIntegerArray() {
        int[] value = new int[] { 1, 2 };
        AtomicIntegerArray ai = new AtomicIntegerArray(value);
        System.out.println(ai.getAndSet(0, 3));
        System.out.println(ai.get(0));
        System.out.println(value[0]);
    }

    private static void testAtomicInteger() throws InterruptedException {
        final AtomicInteger ai = new AtomicInteger(0);
        for(int i=0; i<5; i++) {
            new Thread(new Runnable() {
                public void run() {
                    System.out.println(Thread.currentThread().getName() + ":" + ai.getAndIncrement());//注意为什么会用这个方法
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        }
        Thread.sleep(100);
    }
}
