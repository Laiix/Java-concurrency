package java_concurrency_in_practice._06_taskexecution;

import java.util.List;
import java.util.concurrent.*;

public class Renderer {
    private final ExecutorService executor;
    Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        List<ImageInfo> info = scanForImageInfo(source);
        CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        for (ImageInfo imageInfo : info) {
            completionService.submit(new Callable<ImageData>() {
                @Override
                public ImageData call() throws Exception {
                    return imageInfo.downloadImage();
                }
            });
        }
        renderText(source);

        try {
            for (int t = 0, n=info.size(); t<n; t++) {
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch(InterruptedException e) {
            //重新设置线程的中断状态
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    private void renderImage(ImageData imageData) {

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

    private void renderText(CharSequence source) {
    }

    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        return null;
    }
}
