package model;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;
    
    public boolean isOpposite(Direction other) {
        if (this == UP && other == DOWN) return true;
        if (this == DOWN && other == UP) return true;
        if (this == LEFT && other == RIGHT) return true;
        if (this == RIGHT && other == LEFT) return true;
        return false;
    }
}
