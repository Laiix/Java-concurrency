package java_concurrency_in_practice._03_sharingofobjects;

import java.util.HashSet;
import java.util.Set;

public class PublishUsage {
    public static Set<Secret> knownSecrets;

    public void initialize() {
        knownSecrets = new HashSet<>();
    }
}
