package simple_multi_thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Ch11_BlockingQueue {
    public static void main(String[] args) {
        /**
         * 1-可阻塞的队列
         * 	1）注意查看BlockingQueue类的帮助文档，其中有一个各个方法对比的表格。可以看出只有put和take才具有阻塞功能
         */
        test();
        /**
         * 2）阻塞队列实现线程间通信
         */
        test2();

        /**
         * 2-Collections.synchronizedMap
         * 	    java5之前将不安全的map转换为线程安全的map的做法，实现了一个代理，每个方法都是synchronize方法
         * 3-java5提供了很多线程安全的集合，conncurrentHashMap等，多线程编程时，注意线程安全问题
         */

    }

    public static void test2() {

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


        BlockingQueue<Integer> queue1 = new ArrayBlockingQueue<Integer>(1);
        BlockingQueue<Integer> queue2 = new ArrayBlockingQueue<Integer>(1);

        {
            try {
                System.out.println("主线程先暂停");
                queue2.put(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public  void sub(int i){
            try {
                queue1.put(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for(int j=1;j<=10;j++){
                System.out.println("sub thread sequece of " + j + ",loop of " + i);
            }
            try {
                queue2.take();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public  void main(int i){
            try {
                queue2.put(1);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            for(int j=1;j<=100;j++){
                System.out.println("main thread sequece of " + j + ",loop of " + i);
            }
            try {
                queue1.take();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void test() {
        final BlockingQueue queue = new ArrayBlockingQueue(3);
        for(int i=0;i<2;i++){
            new Thread(){
                public void run(){
                    try {
                        Thread.sleep((long)(Math.random()*1000));
                        System.out.println(Thread.currentThread().getName() + "准备放数据!");
                        queue.put(1);
                        System.out.println(Thread.currentThread().getName() + "已经放了数据，" +
                                "队列目前有" + queue.size() + "个数据");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }.start();
        }

        new Thread(){
            public void run(){
                try {
                    //将此处的睡眠时间分别改为100和1000，观察运行结果
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + "准备取数据!");
                    queue.take();
                    System.out.println(Thread.currentThread().getName() + "已经取走数据，" +
                            "队列目前有" + queue.size() + "个数据");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}
