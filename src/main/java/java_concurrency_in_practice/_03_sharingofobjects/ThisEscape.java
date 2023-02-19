package java_concurrency_in_practice._03_sharingofobjects;

public class ThisEscape {
    public ThisEscape(EventSource source) {
        source.registerListener(new EventListener() {
            @Override
            public void onEvent(Event e) {
                doSomething(e);
            }
        });
    }



    public static void main(String[] args) {
        EventSource eventSource = new EventSource();
        ThisEscape thisEscape = new ThisEscape(eventSource);
        eventSource.getEventListener().onEvent(new Event());

    }


}
