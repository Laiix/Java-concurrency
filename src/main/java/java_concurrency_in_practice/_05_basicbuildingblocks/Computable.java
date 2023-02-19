package java_concurrency_in_practice._05_basicbuildingblocks;

public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
