package java_concurrency_in_practice._08_useofthreadpools;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadDeadlock {
    ExecutorService exec = Executors.newSingleThreadExecutor();

    public class RenderPageTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            Future<String> header, footer;
            header = exec.submit(new LoadFileTask("header.html"));
            footer = exec.submit(new LoadFileTask("footer.html"));
            String page = renderBody();
            //将发生死锁 —— 由于任务在等待子任务的结果
            return header.get() + page + footer.get();
        }

        private String renderBody() {
            return null;
        }
    }

    public class LoadFileTask implements Callable<String> {
        private String page;
        public LoadFileTask(String page) {
            this.page = page;
        }

        @Override
        public String call() throws Exception {
            return null;
        }
    }
}
