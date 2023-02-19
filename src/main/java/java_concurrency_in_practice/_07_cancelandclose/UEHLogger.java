package java_concurrency_in_practice._07_cancelandclose;

public class UEHLogger implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //log 记录
    }
}
