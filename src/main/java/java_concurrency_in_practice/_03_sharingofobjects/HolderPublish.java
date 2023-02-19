package java_concurrency_in_practice._03_sharingofobjects;

public class HolderPublish {
    //不安全的发布
    public Holder holder;


    public void initialize() {
        holder = new Holder(42);
    }
}
