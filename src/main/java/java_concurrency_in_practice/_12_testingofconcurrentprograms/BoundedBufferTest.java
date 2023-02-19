package java_concurrency_in_practice._12_testingofconcurrentprograms;

import org.junit.Assert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class BoundedBufferTest {
    public static void main(String[] args) throws InterruptedException {
        testIsEmptyWhenConstructed();
        testIsFullAfterPuts();
        testTakeBlocksWhenEmpty();
    }

    public static void testIsEmptyWhenConstructed() {
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        Assert.assertTrue(bb.isEmpty());
        Assert.assertFalse(bb.isFull());
    }

    public static void testIsFullAfterPuts() throws InterruptedException {
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            bb.put(i);
        }
        Assert.assertFalse(bb.isEmpty());
        Assert.assertTrue(bb.isFull());
    }

    public static void testTakeBlocksWhenEmpty() {
        final BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        Thread taker = new Thread() {
            public void run() {
                try {
                    int unused = bb.take();
                    fail();
                } catch (InterruptedException success) {
                    System.out.println("SUCCESS");
                }
            }
        };
        try {
            taker.start();
            Thread.sleep(5000);
            taker.interrupt();
            taker.join(5000);
            Assert.assertFalse(taker.isAlive());
        } catch (Exception unexpected) {
            fail();
        }
    }

    static class Big {
        double[] data = new double[100000];
    }
    private final static int CAPACITY = 10000;
    private final static int THRESHOLD = 20;
    public static void testLead() throws InterruptedException {
        BoundedBuffer<Big> bb = new BoundedBuffer<>(CAPACITY);
        int heapSize1 = -1;//生成堆的快照
        for (int i = 0; i < CAPACITY; i++) {
            bb.put(new Big());
        }
        for (int i = 0; i < CAPACITY; i++) {
            bb.take();
        }
        int heapSize2 = -1;//生成堆的快照
        Assert.assertTrue(Math.abs(heapSize1-heapSize2)<THRESHOLD);
    }


    static class TestingThreadFactory implements ThreadFactory {
        public final AtomicInteger numCreated = new AtomicInteger();
        private final ThreadFactory factory = Executors.defaultThreadFactory();

        @Override
        public Thread newThread(Runnable r) {
            numCreated.incrementAndGet();
            return factory.newThread(r);
        }
    }

    private static TestingThreadFactory threadFactory = new TestingThreadFactory();

    public static void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE);

        for (int i = 0; i < 10*MAX_SIZE; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        for (int i = 0; i < 20 && threadFactory.numCreated.get()<MAX_SIZE; i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(threadFactory.numCreated.get(), MAX_SIZE);
        exec.shutdownNow();
    }

    private static void fail() {
        System.err.println("FAIL");
    }
}
