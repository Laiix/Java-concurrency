package java_concurrency_in_practice._03_sharingofobjects;

public class EventSource {
    private EventListener eventListener;
    public void registerListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

}
