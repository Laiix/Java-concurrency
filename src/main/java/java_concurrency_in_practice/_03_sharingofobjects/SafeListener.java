package java_concurrency_in_practice._03_sharingofobjects;

public class SafeListener {
    private final EventListener listener;

    private SafeListener() {
        this.listener = new EventListener() {
            @Override
            public void onEvent(Event e) {
                doSomething(e);
            }
        };
    }

    public static SafeListener newInstance(EventSource source) {
        SafeListener safe = new SafeListener();
        source.registerListener(safe.listener);
        return safe;
    }
}
