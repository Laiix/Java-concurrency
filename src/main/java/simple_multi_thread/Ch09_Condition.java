package simple_multi_thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ch09_Condition {
    public static void main(String[] args) throws InterruptedException {
        /**
         * Condition
         * 	    Condition的功能类似在传统线程技术中Oject.wait和Object.notify的功能。在等待Condition时，允许发生“虚假唤醒（即if判断条件）”，
         * 	这通常作为对基础平台语义的让步。对于大多数应用程序，这带来的实际实际影响很小，因为Condition应该总是在一个循环中被等待，并测试
         * 	正在等待的状态声明。某个实现可以随意移除可能的虚假唤醒，但建议应用程序程序员总是假定这些虚假唤醒可能发生，因此总是在一个循环中等待。
         * 	    一个锁内部可以有多个Condition，即有多路等待和通知，可以参看Jdk1.5提供的Lock与Condition实现的可阻塞队列的应用案例，从中除了要
         * 	体味算法，还要体味面向对象的封装。在传统的线程机制中一个监视器对象上只能有一路等待和通知，要实现多路等待和通知，必须嵌套使用多个同步监
         * 	视器对象。（如果只用一个Condition，两个放的都在等，一旦一个放的进去了，那么他通知可能会导致另一个方接着往下走）
         *
         * 例子一：（修改Ch03_Synchronized文档中的线程通信）
         */
        test();

        /**
         * 例子二：
         *      API中的一个阻塞对列示例，假定有一个绑定的缓冲区，它支持 put 和 take 方法。如果试图在空的缓冲区上执行 take 操作，则在某一个项变得
         *  可用之前，线程将一直阻塞；如果试图在满的缓冲区上执行 put 操作，则在有空间变得可用之前，线程将一直阻塞。我们喜欢在单独的等待 set 中保存
         *  put 线程和 take 线程，这样就可以在缓冲区中的项或空间变得可用时利用最佳规划，一次只通知一个线程。可以使用两个 Condition 实例来做到这一点。
         *
         *  如下：BoundedBuffer
         *
         *  如果不使用JUC,大概是这样的：
         */
        BoundedBuffer_Synchronized.test2();

        /**
         * 例子三：（修改Ch03_Synchronized文档中的线程通信实现三个线程通信调度）
         */
        test3();
    }


    public static void test3() {
        final Business3 business = new Business3();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        for(int i=1;i<=50;i++){
                            business.sub2(i);
                        }
                    }
                }
        ).start();
        new Thread(
                new Runnable() {

                    @Override
                    public void run() {

                        for(int i=1;i<=50;i++){
                            business.sub3(i);
                        }

                    }
                }
        ).start();

        for(int i=1;i<=50;i++){
            business.main(i);
        }
    }
    static class Business3 {
        Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();
        Condition condition3 = lock.newCondition();
        private int shouldSub = 1;
        public  void sub2(int i){
            lock.lock();
            try{
                while(shouldSub != 2){
                    try {
                        condition2.await();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int j=1;j<=10;j++){
                    System.out.println("sub2 thread sequence of " + j + ",loop of " + i);
                }
                shouldSub = 3;
                condition3.signal();
            }finally{
                lock.unlock();
            }
        }
        public void sub3(int i){
            lock.lock();
            try{
                while(shouldSub != 3){
                    try {
                        condition3.await();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int j=1;j<=20;j++){
                    System.out.println("sub3 thread sequence of " + j + ",loop of " + i);
                }
                shouldSub = 1;
                condition1.signal();
            }finally{
                lock.unlock();
            }
        }
        public void main(int i){
            lock.lock();
            try{
                while(shouldSub != 1){
                    try {
                        condition1.await();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int j=1;j<=100;j++){
                    System.out.println("main thread sequence of " + j + ",loop of " + i);
                }
                shouldSub = 2;
                condition2.signal();
            }finally{
                lock.unlock();
            }
        }
    }

    static class BoundedBuffer_Synchronized {
        private Object[] items = new Object[2];
        private Object notEmpty = new Object();
        private Object notFull = new Object();
        int count,putidx,takeidx;

        public void put(Object obj) throws InterruptedException{
            synchronized(notFull){
                while(count == items.length){
                    notFull.wait();
                }
            }
            items[putidx] = obj;
            if(++putidx == items.length){
                putidx = 0;
            }
            count ++;
            synchronized (notEmpty) {
                notEmpty.notify();
            }
        }
        public Object take() throws InterruptedException{
            synchronized(notEmpty){
                while(count == 0){ // 啥也没有呢 取啥
                    notEmpty.wait();
                }
            }
            Object x = items[takeidx];
            System.out.println("取第"+takeidx+"个元素"+x);
            if(++takeidx == items.length){
                takeidx = 0;
            }
            count --;
            synchronized (notFull) {
                notFull.notify();
            }
            return x;
        }
        public static void test2() throws InterruptedException {
            final BoundedBuffer_Synchronized bb = new BoundedBuffer_Synchronized();
            System.out.println(Thread.currentThread()+","+bb);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread()+","+bb);
                        bb.put("xx");
                        bb.put("yy");
                        bb.put("zz");
                        bb.put("zz");
                        bb.put("zz");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            bb.take();
            bb.take();
            bb.take();
            bb.take();
            bb.take();
        }
    }


    class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition notFull  = lock.newCondition();
        final Condition notEmpty = lock.newCondition();

        final Object[] items = new Object[100];
        int putptr, takeptr, count;

        public void put(Object x) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length)
                    notFull.await();
                items[putptr] = x;
                if (++putptr == items.length) putptr = 0;
                ++count;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0)
                    notEmpty.await();
                Object x = items[takeptr];
                if (++takeptr == items.length) takeptr = 0;
                --count;
                notFull.signal();
                return x;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void test() {
        final Business business = new Business();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for(int i=1;i<=50;i++){
                            business.sub(i);
                        }
                    }
                }
        ).start();
        for(int i=1;i<=50;i++){
            business.main(i);
        }
    }
    static class Business {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        private boolean bShouldSub = true;
        public  void sub(int i){
            lock.lock();
            try{
                while(!bShouldSub){
                    try {
                        condition.await();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int j=1;j<=10;j++){
                    System.out.println("sub thread sequence of " + j + ",loop of " + i);
                }
                bShouldSub = false;
                condition.signal();
            }finally{
                lock.unlock();
            }
        }
        public  void main(int i){
            lock.lock();
            try{
                while(bShouldSub){
                    try {
                        condition.await();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int j=1;j<=100;j++){
                    System.out.println("main thread sequence of " + j + ",loop of " + i);
                }
                bShouldSub = true;
                condition.signal();
            }finally{
                lock.unlock();
            }
        }
    }
}
