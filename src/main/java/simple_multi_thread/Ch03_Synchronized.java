package simple_multi_thread;

public class Ch03_Synchronized {
    public static void main(String[] args) {
        /**
         * 场景:
         *  线程存在类似于银行转账的安全问题
         *
         * 互斥技术:
         *  线程互斥技术通过synchronized实现，是否互斥在于持有的锁。其中静态方法使用该关键字，默认持有的锁是xxx.class，
         *  成员方法使用该关键字，默认持有的锁是该类的实例化对象this，同步代码块中使用该关键字，锁是obj，当然obj可以按照
         *  需求传入xxx.class，this以及其他对象
         *      synchronized (obj)
         *      {
         * 	        //do something
         *      }
         *
         * synchronized原理:
         *  数据同步需要依赖锁，那锁的同步又依赖谁？
         *  synchronized给出的答案是在软件层面依赖JVM，而Lock给出的方案是在硬件层面依赖特殊的CPU指令，大家可能会进一步追问：
         *  JVM底层又是如何实现synchronized的？
         *  下面所说的JVM是指Hotspot的6u23版本，下面首先介绍synchronized的实现：
         *      synrhronized关键字简洁、清晰、语义明确，因此即使有了Lock接口，使用的还是非常广泛。其应用层的语义是可以把任何一个
         *     非null对象作为"锁"，当synchronized作用在方法上时，锁住的便是对象实例（this）；当作用在静态方法时锁住的便是对象
         *     对应的Class实例，因为Class数据存在于永久带，因此静态方法锁相当于该类的一个全局锁；当synchronized作用于某一个对象
         *     实例时，锁住的便是对应的代码块。在HotSpot JVM实现中，锁有个专门的名字：对象监视器。
         *
         * 线程间通信:
         *  1. wait()、notify/notifyAll() 方法是Object的本地final方法，无法被重写。
         *  2. wait()使当前线程阻塞，前提是必须先获得锁，所以一般配合synchronized关键字使用，因为这样可以获得锁，
         *      即，一般在synchronized 同步代码块里使用 wait()、notify/notifyAll()方法。
         *  3. 由于 wait()、notify/notifyAll() 在synchronized 代码块执行，说明当前线程一定是获取了锁的。当线程执行wait()方法时候，
         *      会释放当前的锁，然后让出CPU，进入等待状态。只有当 notify/notifyAll() 被执行时候，才会唤醒一个或多个正处于等待状态的线程，
         *      然后继续往下执行，直到执行完synchronized 代码块的代码或是中途遇到wait()，再次释放锁。也就是说，notify/notifyAll() 的
         *      执行只是唤醒沉睡的线程，而不会立即释放锁，锁的释放要看代码块的具体执行情况。所以在编程中，尽量在使用了notify/notifyAll()
         *      后立即退出临界区，以唤醒其他线程
         *  4. wait() 需要被try catch包围，中断也可以使wait等待的线程唤醒。
         *  5. notify 和wait 的顺序不能错，如果A线程先执行notify方法，B线程在执行wait方法，那么B线程是无法被唤醒的。
         *  6. notify 和 notifyAll的区别 notify方法只唤醒一个等待（对象的）线程并使该线程开始执行。所以如果有多个线程等待一个对象，
         *      这个方法只会唤醒其中一个线程，选择哪个线程取决于操作系统对多线程管理的实现。notifyAll 会唤醒所有等待(对象的)线程，尽管
         *      哪一个线程将会第一个处理取决于操作系统的实现。如果当前情况下有多个线程需要被唤醒，推荐使用notifyAll 方法。比如在生产者-消费者
         *      里面的使用，每次都需要唤醒所有的消费者或是生产者，以判断程序是否可以继续往下执行。
         *  7. 在多线程中要测试某个条件的变化，使用if 还是while？ 要注意，notify唤醒沉睡的线程后，线程会接着上次的执行继续往下执行。
         *      如果用if的话，意味着if继续往下走，会跳出if语句块。但是，notifyAll 只是负责唤醒线程，并不保证条件满足，所以需要手动来
         *      保证程序的逻辑。显然，要确保程序一定要执行，并且要保证程序直到满足一定的条件再执行，要使用while来执行，以确保条件满足和一定执行。
         *
         * 有关线程间通信的面试题：
         *  子线程循环5次，接着主线程循环10，接着又回到子线程循环5次，接着再回到主线程又循环10，如此循环5次，请写出程序：
         */
        rotation();
    }

    public static void rotation() {
        final Business business = new Business();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    business.sub(i);
                }
            }
        }).start();
        for (int i = 1; i <= 5; i++) {
            business.main(i);
        }
    }

    static class Business {
        private boolean bShouldSub = true;
        public synchronized void sub(int i) {
            while (!bShouldSub) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 5; j++) {
                System.out.println("sub thread sequence of " + j + ",loop of " + i);
            }
            bShouldSub = false;
            this.notify();
        }
        public synchronized void main(int i) {
            while (bShouldSub) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int j = 1; j <= 10; j++) {
                System.out.println("main thread sequence of " + j + ",loop of " + i);
            }
            bShouldSub = true;
            this.notify();
        }
    }

}
