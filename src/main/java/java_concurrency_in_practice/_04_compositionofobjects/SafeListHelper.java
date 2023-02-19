package java_concurrency_in_practice._04_compositionofobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SafeListHelper<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<E>());

    public boolean putIfAbsent(E x) {
        synchronized (list) {
            boolean absent = !list.contains(x);
            if(absent)
                list.add(x);
            return absent;
        }
    }
}
