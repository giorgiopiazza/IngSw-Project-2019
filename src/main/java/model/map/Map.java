package model.map;

import enumerations.Color;
import enumerations.SquareAdjacency;
import exceptions.map.MapUnknowException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Map {
    /**
     * Maximum number of map lines: 3
     */
    public static final int MAX_ROWS = 3;
    /**
     * Maximum number of map columns: 4
     */
    public static final int MAX_COLUMNS = 4;
    /**
     * Map of type:
     * B B B
     * R R R Y
     *   W W Y
     */
    public static final int MAP_1 = 1;
    /**
     * Map of type:
     * B B B G
     * R R Y Y
     *   W Y Y
     */
    public static final int MAP_2 = 2;
    /**
     * Map of type:
     * R B B G
     * R M Y Y
     * W W Y Y
     */
    public static final int MAP_3 = 3;
    /**
     * Map of type:
     * R B B
     * R M M Y
     * W W W Y
     */
    public static final int MAP_4 = 4;

    private Square[][] rooms;

    public Map(int mapType) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("json/maps.json");
        JSONArray array = new JSONArray(new JSONTokener(is));
        JSONObject mapObject = null;

        Square[][] map;
        map = new Square[MAX_ROWS][MAX_COLUMNS];

        switch (mapType) {
            case MAP_1:
                for (int i=0;i<array.length();i++) {
                    if(!array.isNull(i) && array.getJSONObject(i).getInt("id") == MAP_1) {
                        mapObject = array.getJSONObject(i);
                    }
                }
                break;

            case MAP_2:
                for (int i=0;i<array.length();i++) {
                    if(!array.isNull(i) && array.getJSONObject(i).getInt("id") == MAP_2) {
                        mapObject = array.getJSONObject(i);
                    }
                }
                break;

            case MAP_3:
                for (int i=0;i<array.length();i++) {
                    if(!array.isNull(i) && array.getJSONObject(i).getInt("id") == MAP_3) {
                        mapObject = array.getJSONObject(i);
                    }
                }
                break;

            case MAP_4:
                for (int i=0;i<array.length();i++) {
                    if(!array.isNull(i) && array.getJSONObject(i).getInt("id") == MAP_4) {
                        mapObject = array.getJSONObject(i);
                    }
                }
                break;

            default:
                throw new MapUnknowException();
        }

        if (mapObject == null) throw new MapUnknowException();

        JSONArray matrix = mapObject.getJSONArray("map");

        for (int i=0;i<matrix.length();i++) {
            JSONArray row = matrix.getJSONArray(i);
            for (int j=0;j<row.length();j++) {
                if(row.isNull(j)) {
                    map[i][j] = null;
                } else {
                    JSONObject square = row.getJSONObject(j);
                    if (square.getBoolean("isSpawn")) {
                        map[i][j] = new SpawnSquare(
                                Color.valueOf(square.getString("color")),
                                SquareAdjacency.valueOf(square.getString("north")),
                                SquareAdjacency.valueOf(square.getString("east")),
                                SquareAdjacency.valueOf(square.getString("south")),
                                SquareAdjacency.valueOf(square.getString("west"))
                        );
                    } else {
                        map[i][j] = new CardSquare(
                                Color.valueOf(square.getString("color")),
                                SquareAdjacency.valueOf(square.getString("north")),
                                SquareAdjacency.valueOf(square.getString("east")),
                                SquareAdjacency.valueOf(square.getString("south")),
                                SquareAdjacency.valueOf(square.getString("west"))
                        );
                    }
                }
            }
        }

        this.rooms = map;
    }

    /**
     * Create a new map using the <code>rooms</code> matrix passed if the maximum size is respected MAX_ROWS x MAX_COLUMNS
     *
     * @param rooms matrix containing the map rooms, maximum size MAX_ROWS x MAX_COLUMNS
     * @return true if the parameter respects the MAX_ROWS x MAX_COLUMNS dimension, otherwise false
     */
    private boolean fillMap(Square[][] rooms) {
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

    /**
     * Returns the square specifying his coordinates
     *
     * @param x the X of the square
     * @param y the Y of the square
     * @return the Square whose coordX is x and coordY is y
     */
    public Square getSquare(int x, int y) {
        return rooms[x][y];
    }

    /**
     * Method to obtain all the players who are in the specified position
     *
     * @param pos the position in which there are the Players returned
     * @return the players who are in the position pos
     */
    public Player[] getPlayersInSquare(PlayerPosition pos) {
        Game game = Game.getInstance();
        List<Player> players = new ArrayList<>();

        for (Player p : game.getPlayers()) {
            if (p.getPosition().equals(pos)) {
                players.add(p);
            }
        }

        if (game.isTerminatorPresent()) {
            Player term = game.getTerminator();
            if (term.getPosition().equals(pos)) {
                players.add(term);
            }
        }

        return players.toArray(new Player[0]);
    }

    /**
     * Method to obtain all the players who are in the specified room
     *
     * @param roomColor the Color of the room in which there are the players returned
     * @return the ArrayList of players who are in the room of color roomColor
     */
    public List<Player> getPlayersInRoom(Color roomColor) {
        Game game = Game.getInstance();
        List<Player> players = new ArrayList<>();

        for (Player p: game.getPlayers()) {
            if(getSquare(p.getPosition().getCoordX(), p.getPosition().getCoordY()).getColor().equals(roomColor)) {
                players.add(p);
            }
        }

        if(game.isTerminatorPresent()) {
            Player term = game.getTerminator();
            if(getSquare(term.getPosition().getCoordX(), term.getPosition().getCoordY()).getColor().equals(roomColor)) {
                players.add(term);
            }
        }

        return players;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append('\n');

        for (int i=0;i<MAX_ROWS;i++) {
            for (int j=0;j<MAX_COLUMNS;j++) {
                if (rooms[i][j] == null) {
                    buffer.append(" \t");
                } else {
                    buffer.append(' ');
                    buffer.append(rooms[i][j].getColor());
                }
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }
}
