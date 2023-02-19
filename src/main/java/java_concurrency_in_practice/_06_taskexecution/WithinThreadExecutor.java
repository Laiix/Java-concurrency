package java_concurrency_in_practice._06_taskexecution;

import java.util.concurrent.Executor;

public class WithinThreadExecutor implements Executor {
    @Override
    public void execute(Runnable r) {
        r.run();
    }
}
