package java_concurrency_in_practice._07_cancelandclose;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class LogService2 {
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final Writer writer;

    public LogService2(Writer writer) {
        this.writer = writer;
    }

    public void start() {}

    public void stop() throws IOException, InterruptedException {
        try {
            exec.shutdown();
            exec.awaitTermination(1000, TimeUnit.SECONDS);
        } finally {
            writer.close();
        }
    }

    public void log(String msg) {
        try {
            exec.execute(new WriterTask(msg));
        } catch (RejectedExecutionException ignored) {

        }
    }
}
