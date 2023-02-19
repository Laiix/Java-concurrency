package java_concurrency_in_practice._03_sharingofobjects;

public class EventListener {
    public void onEvent(Event e) {
        doSomething(e);
    }

    void doSomething(Event e) {
        System.out.println(this.getClass());
    }

}
