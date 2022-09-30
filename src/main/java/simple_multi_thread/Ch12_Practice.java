package simple_multi_thread;


import java.util.Iterator;
import java.util.concurrent.*;

public class Ch12_Practice {
    public static void main(String[] args) {
        /**
         * 现有的程序代码模拟产生了16个日志对象，并且需要运行16秒才能打印完这些日志，请在程序中增加4个线程去调用parseLog()方法来分头打印
         * 这16个日志对象，程序只需要运行4秒即可打印完这些日志对象。原始代码如下：
         */
//        test();
        /**
         * 解：
         */
//        test2();
        /**
         * 现成程序中的Test类中的代码在不断地产生数据，然后交给TestDo.doSome()方法去处理，就好像生产者在不断地产生数据，消费者在不断消费
         * 数据。请将程序改造成有10个线程来消费生成者产生的数据，这些消费者都调用TestDo.doSome()方法去进行处理，故每个消费者都需要一秒才
         * 能处理完，程序应保证这些消费者线程依次有序地消费数据，只有上一个消费者消费完后，下一个消费者才能消费数据，下一个消费者是谁都可以，
         * 但要保证这些消费者线程拿到的数据是有顺序的。原始代码如下：
         */
//        test3();
        /**
         * 解：
         */
//        test4();

        /**
         * 现有程序同时启动了4个线程去调用TestDo.doSome(key, value)方法，由于TestDo.doSome(key, value)方法内的代码是先暂停1秒，然后
         * 再输出以秒为单位的当前时间值，所以，会打印出4个相同的时间值，如下所示：
         * 		4:4:1258199615
         * 		1:1:1258199615
         * 		3:3:1258199615
         * 		1:2:1258199615
         *  请修改代码，如果有几个线程调用TestDo.doSome(key, value)方法时，传递进去的key相等（equals比较为true），则这几个
         *  线程应互斥排队输出结果，即当有两个线程的key都是"1"时，它们中的一个要比另外其他线程晚1秒输出结果，如下所示：
         * 		4:4:1258199615
         * 		1:1:1258199615
         * 		3:3:1258199615
         * 		1:2:1258199616
         * 	总之，当每个线程中指定的key相等时，这些相等key的线程应每隔一秒依次输出时间值（要用互斥），如果key不同，则并行执行（相互之间不互斥）。原始代码如下：
         */
        test5();
    }
    public static void test5() {
        Test a = new Test("1","","1");
        Test b = new Test("1","","2");
        Test c = new Test("3","","3");
        Test d = new Test("4","","4");
        System.out.println("begin:"+(System.currentTimeMillis()/1000));
        a.start();
        b.start();
        c.start();
        d.start();
    }


    //不能改动此Test类
    static class Test extends Thread{

        private TestDo2 testDo2;
        private String key;
        private String value;

        public Test(String key,String key2,String value){
            this.testDo2 = TestDo2.getInstance();
			/*常量"1"和"1"是同一个对象，下面这行代码就是要用"1"+""的方式产生新的对象，
			以实现内容没有改变，仍然相等（都还为"1"），但对象却不再是同一个的效果*/
            this.key = key+key2;
            this.value = value;
        }

        public void run(){
            testDo2.doSome(key, value);
        }
    }

    static class TestDo2 {

        private TestDo2() {}
        private static TestDo2 _instance = new TestDo2();
        public static TestDo2 getInstance() {
            return _instance;
        }

        //private ArrayList keys = new ArrayList();  //线程不安全，迭代过程中不能操作集合
        private CopyOnWriteArrayList keys = new CopyOnWriteArrayList();

        public void doSome(Object key, String value) {

            //*****************追加代码
            Object o = key;
            if(!keys.contains(o)){
                keys.add(o);
            }else{

                for(Iterator iter = keys.iterator(); iter.hasNext();){
                    //try {
                    //	Thread.sleep(20);
                    //} catch (InterruptedException e) {
                    //	// TODO Auto-generated catch block
                    //	e.printStackTrace();
                    //}
                    Object oo = iter.next();
                    if(oo.equals(o)){
                        o = oo;
                        break;
                    }
                }
            }
            synchronized(o)
            //************************

            // 以大括号内的是需要局部同步的代码，不能改动!
            {
                try {
                    Thread.sleep(1000);
                    System.out.println(key+":"+value + ":"
                            + (System.currentTimeMillis() / 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void test4() {
        final Semaphore semaphore = new Semaphore(1);
        final SynchronousQueue<String> queue = new SynchronousQueue<String>();
        for(int i=0;i<10;i++){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        String input = queue.take();
                        String output = TestDo.doSome(input);
                        System.out.println(Thread.currentThread().getName()+ ":" + output);
                        semaphore.release();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        System.out.println("begin:"+(System.currentTimeMillis()/1000));
        for(int i=0;i<10;i++){  //这行不能改动
            String input = i+"";  //这行不能改动
            try {
                queue.put(input);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            String output = TestDo.doSome(input);
//            System.out.println(Thread.currentThread().getName()+ ":" + output);
        }
    }

    public static void test3() {

        System.out.println("begin:"+(System.currentTimeMillis()/1000));
        for(int i=0;i<10;i++){  //这行不能改动
            String input = i+"";  //这行不能改动
            String output = TestDo.doSome(input);
            System.out.println(Thread.currentThread().getName()+ ":" + output);
        }
    }

    //不能改动此TestDo类
    static class TestDo {
        public static String doSome(String input){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String output = input + ":"+ (System.currentTimeMillis() / 1000);
            return output;
        }
    }

    public static void test2(){
        final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);
        for(int i=0;i<4;i++){
            new Thread(new Runnable(){
                @Override
                public void run() {
                    while(true){
                        try {
                            String log = queue.take();
                            parseLog(log);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            }).start();
        }

        System.out.println("begin:"+(System.currentTimeMillis()/1000));
					/*模拟处理16行日志，下面的代码产生了16个日志对象，当前代码需要运行16秒才能打印完这些日志。
					修改程序代码，开四个线程让这16个对象在4秒钟打完。
					*/
        for(int i=0;i<16;i++){  //这行代码不能改动
            final String log = ""+(i+1);//这行代码不能改动
            {
                try {
                    queue.put(log);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //Ch12_Practice.parseLog(log);
            }
        }
    }

    public static void test(){

        System.out.println("begin:"+(System.currentTimeMillis()/1000));
        /*模拟处理16行日志，下面的代码产生了16个日志对象，当前代码需要运行16秒才能打印完这些日志。
        修改程序代码，开四个线程让这16个对象在4秒钟打完。
        */
        for(int i=0;i<16;i++){  //这行代码不能改动
            final String log = ""+(i+1);//这行代码不能改动
            {
                Ch12_Practice.parseLog(log);
            }
        }
    }

    //parseLog方法内部的代码不能改动
    public static void parseLog(String log){
        System.out.println(log+":"+(System.currentTimeMillis()/1000));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
