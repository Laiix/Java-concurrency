package simple_multi_thread;

import java.util.concurrent.CountDownLatch;

public class Ch04_ThreadLocalAndSync {
    public static void main(String[] args) throws InterruptedException {
        /**
         * ThreadLocal:
         *  ThreadLocal相当于一个Map
         *  1. 实现线程内的数据共享，即对于在不同线程中相同的程序代码，多个模块在同一个线程中运行时要共享一份数据，
         *      而在另外线程中运行时又共享另外一份数据。
         *  2. Thread通过set方法，相当于在内部的map中增加了一条记录。线程结束后通过ThreadLocal.clear()方法，
         *      这样会更块的释放内存，不调用也可以，会自动释放。
         *  3. Thread多用在事务操作中
         *  4. 需要多个变量共享，可以定义多个ThreadLocal或者将变量放在一个对象中来一起处理
         *
         * ThreadLocal设计案例：
         */
        testThreadLocal();

        /**
         * 如果每个线程内执行的代码相同，可以使用同一个Runnable对象，这个Runnable对象中有那个共享数据，例如买票系统可以那样做。
         */
        test();
        /**
         * 如果每个线程执行的代码不同，这时候需要用不同的Runnable对象，有两种方式来实现这些Runnable对象之间的数据共享：
         *  一：将共享数据封装在另外一个对象中，然后将这个对象逐一传递给各个Runnable对象，每个线程对共享数据的操作方法也分配到
         *      那个对象身上去完成，这样容易实现针对给数据的各个操作的互斥和通信
         */
        testShareData1();
        /**
         *  二：将这些Runnable对象作为一个类中的内部类，共享数据作为这个外部类的成员变量，每个线程对共享数据的操作方法也分配该外部类，
         *      以便实现对共享数据进行的各个操作的互斥和通信，作为内部类的各个Runnable对象调用外部类的这个方法
         */
        MultiThread2.test();
        /**
         *  三：上面两种方式的组合：将共享数据封装在另外一个对象中，每个线程对共享数据的操作方法也分配到那个对象去完成，对象作为
         *      这个外部类中的成员变量或方法中的局部变量，每个线程的Runnable对象作为外部类中的成员内部类或局部内部类
         */

        MultiThread1.test();
        /**
         * 总之，要同步互斥的几段代码最好分别放在几个独立的方法中，这些方法再放在同一个类中，这样比较容易实现他们之间的同步互斥和通信。
         */


    }

    static class MultiThread1 {
        public static void test() throws InterruptedException {
            final ShareData data = new ShareData();
            Thread t1 = new Thread(new Runnable(){
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted()) {
                        data.decrement();
                    }

                }
            });
            Thread t2 = new Thread(new Runnable(){
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted()) {
                        data.increment();
                    }
                }
            });
            t1.start();
            t2.start();
            Thread.sleep(500);
            t1.interrupt();
            t2.interrupt();
        }
    }

    static class MultiThread2 {
        public static void test() throws InterruptedException {
            final MultiThread2 mh = new MultiThread2();
            Thread t1 = new Thread(new Runnable(){
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted()) {
                        mh.decrement();
                    }

                }
            });
            Thread t2 = new Thread(new Runnable(){
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted()) {
                        mh.increment();
                    }
                }
            });
            t1.start();
            t2.start();
            Thread.sleep(500);
            t1.interrupt();
            t2.interrupt();
        }

        int j = 0;
        public synchronized void increment(){
            j++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + "-class-" + j);
        }
        public synchronized void decrement(){
            j--;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + "-class-" + j);
        }

    }

    public static void testShareData1() throws InterruptedException {
        ShareData data = new ShareData();
        Thread t1 = new Thread(new MyRunnable_1(data));
        Thread t2 = new Thread(new MyRunnable_2(data));
        t1.start();
        t2.start();
        Thread.sleep(500);
        t1.interrupt();
        t2.interrupt();
    }

    //不同的runnbale对象
    static class MyRunnable_1 implements Runnable{
        private ShareData data;
        public MyRunnable_1(ShareData data){
            this.data = data;
        }
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                data.decrement();
            }

        }
    }
    static class MyRunnable_2 implements Runnable{
        private ShareData data;
        public MyRunnable_2(ShareData data){
            this.data = data;
        }
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                data.increment();
            }
        }
    }

    //共享数据
    static class ShareData{
        private int j = 0;
        public synchronized void increment(){
            j++;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + "-sharedata-" + j);
        }
        public synchronized void decrement(){
            j--;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + "-sharedata-" + j);
        }
    }


    public static void test() throws InterruptedException {
        int threadNum = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new MyThread(countDownLatch).start();
        }
        countDownLatch.await();
    }

    static class MyThread extends Thread {
        private CountDownLatch countDownLatch;

        public MyThread(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        static int j = 10;
        @Override
        public void run() {
            while (true) {
                synchronized (MyThread.class) {
                    if(j>0) {
                        j--;
                        System.out.println(Thread.currentThread().getName() + "--" + j);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        break;
                }
                countDownLatch.countDown();
            }
        }
    }

    private static void testThreadLocal() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final MyThreadScopeData[] myThreadScopeDataArr = new MyThreadScopeData[2];
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyThreadScopeData myThreadScopeData = MyThreadScopeData.getThreadInstance();
                myThreadScopeDataArr[0] = myThreadScopeData;
                myThreadScopeData.setName("thread-1");
                countDownLatch.countDown();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyThreadScopeData myThreadScopeData = MyThreadScopeData.getThreadInstance();
                myThreadScopeDataArr[1] = myThreadScopeData;
                myThreadScopeData.setName("thread-2");
                countDownLatch.countDown();
            }
        }).start();
        countDownLatch.await();
        System.out.println(myThreadScopeDataArr[0].toString() + "\t" + myThreadScopeDataArr[0].getName());
        System.out.println(myThreadScopeDataArr[1].toString() + "\t" + myThreadScopeDataArr[1].getName());
    }

    static class MyThreadScopeData{
        private MyThreadScopeData(){}
        public static /*synchronized*/ MyThreadScopeData getThreadInstance(){
            MyThreadScopeData instance = map.get();
            if(instance == null){
                instance = new MyThreadScopeData();
                map.set(instance);
            }
            return instance;
        }
        //private static MyThreadScopeData instance = null;//new MyThreadScopeData();
        private static ThreadLocal<MyThreadScopeData> map = new ThreadLocal<MyThreadScopeData>();

        private String name;
        private int age;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
