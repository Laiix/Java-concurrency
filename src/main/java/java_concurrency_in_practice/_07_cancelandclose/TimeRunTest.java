package java_concurrency_in_practice._07_cancelandclose;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeRunTest {

    private static final ScheduledExecutorService cancelExec = new ScheduledThreadPoolExecutor(5);

    public static void timedRun(Runnable r, long timeout, TimeUnit unit) {
        final Thread taskThread = Thread.currentThread();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        r.run();
    }

    public static void timedRun2(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        class ReThrowableTask implements Runnable {
            private volatile Throwable t;//注意volatile变量声明
            @Override
            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }
            void rethrow() {
                if(t!=null)
                    throw  launderThrowable(t);
            }

            /**
             * 如果 Throwable是Error, 那么抛出它；如果是RuntimeExcep
             * tion, 那么返回它，否则抛出 IllegalStateException
             * @param t
             * @return
             */
            private RuntimeException launderThrowable(Throwable t) {
                if(t instanceof RuntimeException)
                    return (RuntimeException) t;
                else if(t instanceof Error)
                    throw (Error)t;
                else
                    throw new IllegalStateException("Not unchecked", t);
            }
        }

        ReThrowableTask task = new ReThrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        cancelExec.schedule(new Runnable() {
            @Override
            public void run() {
                taskThread.interrupt();
            }
        }, timeout, unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }



}
