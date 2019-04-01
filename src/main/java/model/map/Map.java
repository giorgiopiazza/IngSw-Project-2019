package model.map;

public class Map {
    /**
     * Maximum number of map lines: 3
     */
    public static final int MAX_ROWS = 3;
    /**
     * Maximum number of map columns: 4
     */
    public static final int MAX_COLUMNS = 4;
    private Square[][] rooms;

    public Map() {
        rooms = new Square[MAX_ROWS][MAX_COLUMNS];
    }

    /**
     * Create a new map using the <code>rooms</code> matrix passed if the maximum size is respected MAX_ROWS x MAX_COLUMNS
     *
     * @param rooms matrix containing the map rooms, maximum size MAX_ROWS x MAX_COLUMNS
     * @return true if the parameter respects the MAX_ROWS x MAX_COLUMNS dimension, otherwise false
     */
    public boolean fillMap(Square[][] rooms) {
        int width = rooms.length;
        int height = rooms[0].length;

        if(width > MAX_ROWS) return false;
        if(height > MAX_COLUMNS) return false;

        for(int i=0;i<MAX_ROWS;i++) {
            for(int j=0;j<MAX_COLUMNS;j++)
                this.rooms[i][j] = null;
        }

        for(int i=0;i<width;i++) {
            System.arraycopy(rooms[i], 0, this.rooms[i], 0, height);
        }

        return true;
    }

}
