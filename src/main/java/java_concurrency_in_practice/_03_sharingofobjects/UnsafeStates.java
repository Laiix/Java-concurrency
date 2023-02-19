package java_concurrency_in_practice._03_sharingofobjects;

public class UnsafeStates {
    private String[] states = new String[] {
            "AK", "AK" //....
    };

    public String[] getStates() {
        return states;
    }
}
