package java_concurrency_in_practice._05_basicbuildingblocks;

import java.util.concurrent.BlockingQueue;

public class TaskRunnable implements Runnable{
    BlockingQueue<Task> queue;
    @Override
    public void run() {
        try {
            processTask(queue.take());
        } catch (InterruptedException e) {
            //恢复被中断的状态
            Thread.currentThread().interrupt();
        }
    }

    private void processTask(Task task) {
    }
}
