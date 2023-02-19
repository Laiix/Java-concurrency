package java_concurrency_in_practice._03_sharingofobjects;

public class Holder {
    private int n;

    public Holder(int n) {
        this.n = n;
    }

    public void assertSanity() {
        if(n!=n)
            throw new AssertionError("This statement is false.");
    }
}
