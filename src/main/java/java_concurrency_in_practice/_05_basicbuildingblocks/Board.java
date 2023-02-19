package java_concurrency_in_practice._05_basicbuildingblocks;

public class Board {
    private int x;
    private int y;
    private int value;
    public void commitNewValues() {
    }

    public Board getSubBoard(int count, int index) {
        //返回子问题
        return new Board();
    }

    public boolean hasConverged() {
        return false;
    }

    public int getMaxX() {
        return x;
    }

    public int getMaxY() {
        return y;
    }

    public void setNewValue(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public void waitForConvergence() {
    }
}
