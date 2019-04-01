package model.map;

public class Map {
    public static final int MAX_ROWS = 3;
    public static final int MAX_COLUMNS = 4;
    private Square[][] rooms;

    public Map() {
        rooms = new Square[MAX_ROWS][MAX_COLUMNS];
    }


}
