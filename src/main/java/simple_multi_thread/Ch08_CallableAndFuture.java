package simple_multi_thread;

import java.util.Random;
import java.util.concurrent.*;

public class Ch08_CallableAndFuture {

    public static void main(String[] args) {
        /**
         * CallableAndFuture
         * 	threadPool.submit方法是有返回值，注意与execute的区别
         */
        test();
        /**
         * 提交一组任务
         */
        test2();
    }

    public static void test() {
        ExecutorService threadPool =  Executors.newSingleThreadExecutor();
        Future<String> future = //返回值放在future中，future可以调用cancel（）方法取消任务
                threadPool.submit(
                        new Callable<String>() { //有返回值的任务
                            public String call() throws Exception {
                                Thread.sleep(2000);
                                return "hello";
                            };
                        }
                );
        //此处取结果前可以做其他事情
        System.out.println("等待结果");
        try {
            System.out.println("拿到结果：" + future.get());//futrue拿不到结果会一直等待，可以设置等待的时间，超时未得结果抛出超时异常
            threadPool.shutdown();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void test2() {
        ExecutorService threadPool2 =  Executors.newFixedThreadPool(10);
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(threadPool2);
        for(int i=1;i<=10;i++){
            final int seq = i;
            completionService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Thread.sleep(new Random().nextInt(5000));
                    return seq;
                }
            });
        }
        for(int i=0;i<10;i++){
            try {
                System.out.println(
                        completionService.take().get());//返回一个已完成的Callable对象对用的future对象
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
