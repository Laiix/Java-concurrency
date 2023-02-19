package java_concurrency_in_practice._04_compositionofobjects;

import net.jcip.annotations.Immutable;

@Immutable
public class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
