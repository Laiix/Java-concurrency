package java_concurrency_in_practice._03_sharingofobjects;

public class VolatileUsage {
    volatile boolean asleep;

    public void usage() {
        while(!asleep)
            countSomeSheep();
    }

    private void countSomeSheep() {
        // do something
    }
}
