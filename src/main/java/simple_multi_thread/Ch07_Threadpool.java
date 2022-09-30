package simple_multi_thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ch07_Threadpool {
    public static void main(String[] args) {
        /**
         * 固定线程池
         */
        test1();
        /**
         * 方法简介：
         * 	shutdown（）
         * 		问：shutdown()有什么功能？
         * 		答：阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的。
         * 		问：如何阻止新来的任务提交？
         * 		答：通过将线程池的状态改成SHUTDOWN，当再将执行execute提交任务时，如果测试到状态不为RUNNING，则抛出rejectedExecution，从而达到阻止新任务提交的目的。
         * 		问：为何对提交的任务不产生任何影响？
         * 		答：在调用中断任务的方法时，它会检测workers中的任务，如果worker对应的任务没有中断，并且是空闲线程，它才会去中断。另外的话，workQueue中的值，还是按照一定的逻辑顺
         * 	    	序不断的往works中进行输送的，这样一来，就可以保证提交的任务按照线程本身的逻辑执行，不受到影响。
         * 	shutdownNow()
         * 		问：shutdownNow()有什么功能？
         * 		答：阻止新来的任务提交，同时会中断当前正在运行的线程，即workers中的线程。另外它还将workQueue中的任务给移除，并将这些任务添加到列表中进行返回。
         * 		问：如何阻止新来的任务提交？
         * 		答：通过将线程池的状态改成STOP，当再将执行execute提交任务时，如果测试到状态不为RUNNING，则抛出rejectedExecution，从而达到阻止新任务提交的目的.
         * 		问：如果我提交的任务代码块中，正在等待某个资源，而这个资源没到，但此时执行shutdownNow()，会出现什么情况？
         * 		答：当执行shutdownNow()方法时，如遇已经激活的任务，并且处于阻塞状态时，shutdownNow()会执行1次中断阻塞的操作，此时对应的线程报InterruptedException，如果后续还要等待某个资源，
         * 	    	则按正常逻辑等待某个资源的到达。例如，一个线程正在sleep状态中，此时执行shutdownNow()，它向该线程发起interrupt()请求，而sleep()方法遇到有interrupt()请求时，
         * 	    	会抛出InterruptedException()，并继续往下执行。在这里要提醒注意的是，在激活的任务中，如果有多个sleep(),该方法只会中断第一个sleep()，而后面的仍然按照正常的执行逻辑进行。
         * 	awaitTermination(long timeout,TimeUnit unit)
         * 		简单来说，awaitTermination会一直等待，直到线程池状态为TERMINATED或者，等待的时间到达了指定的时间。
         * 	补充
         * 		//runState != RUNNING
         * 		System.out.println("-----isShutdown-------:"+tpe.isShutdown());
         * 		//state == SHUTDOWN || state == STOP
         * 		System.out.println("-----isTerminating----:"+tpe.isTerminating());
         * 		//runState == TERMINATED
         * 		System.out.println("-----isTerminated-----:"+tpe.isTerminated());
         *
         * 	缓存线程池:
         */
        test2();

        /**
         * 单一线程池:
         */
        test3();

        /**
         * 定时任务
         */
        test4();
    }

    public static void test4() {
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(3);//数字代表池中所保存的线程数（包括空闲线程）不是只能创建这些线程
        //虽然此类继承自 ThreadPoolExecutor，但是几个继承的调整方法对此类并无作用。特别是，因为它作为一个使用 corePoolSize 线程和一个无界队列的固定大小的池，所以调整 maximumPoolSize 没有什么效果
        threadPool.schedule(//多长时间以后开始执行
                new Runnable(){
                    @Override
                    public void run() {
                        System.out.println("bombing_1 !");
                    }},
                3,//delay
                TimeUnit.SECONDS); //单位
        threadPool.scheduleAtFixedRate(//多长时间以后开始按频率执行
                new Runnable(){
                    @Override
                    public void run() {
                        System.out.println("bombing_2 !");
                    }},
                2,
                2, //频率
                TimeUnit.SECONDS);
    }

    public static void test3() {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();//创建单一线程池，该线程时候回继续在创建一个线程池替补
        for(int i=1;i<=5;i++){
            final int task = i;
            threadPool.execute(new Runnable(){
                @Override
                public void run() {
                    for(int j=1;j<=2;j++){
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " is looping of " + j + " for  task of " + task);
                    }
                }
            });
        }
        System.out.println("all of 10 tasks have committed! ");
        //threadPool.shutdownNow();
    }

    public static void test2() {
        ExecutorService threadPool = Executors.newCachedThreadPool();//线程数不定的线程池，按照需要增长
        for(int i=1;i<=5;i++){
            final int task = i;
            threadPool.execute(new Runnable(){
                @Override
                public void run() {
                    for(int j=1;j<=2;j++){
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " is looping of " + j + " for  task of " + task);
                    }
                }
            });
        }
        System.out.println("all of 10 tasks have committed! ");
    }

    public static void test1() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);//大小为3的线程池
        for(int i=1;i<=5;i++){
            final int task = i;
            threadPool.execute(new Runnable(){
                @Override
                public void run() {
                    for(int j=1;j<=2;j++){
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + " is looping of " + j + " for  task of " + task);
                    }
                }
            });
        }
        System.out.println("all of 5 tasks have committed! ");
        //		threadPool.shutdownNow();//代码走到这里关闭
        threadPool.shutdown();//线程运行结束关闭
    }
}
