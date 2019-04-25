package model.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import enumerations.Color;
import enumerations.SquareAdjacency;
import exceptions.AdrenalinaRuntimeException;
import exceptions.file.JsonFileNotFoundException;
import exceptions.map.MapUnknowException;
import model.Game;
import model.player.Player;
import model.player.PlayerPosition;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String PATH = "/json/maps.json";


    public Map(int mapType) {
        InputStream is = Map.class.getResourceAsStream(PATH);

        if(is == null) throw new JsonFileNotFoundException("File " + PATH + " not found");

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(new InputStreamReader(is)).getAsJsonArray();

        JsonObject mapObject = null;

        Square[][] map;
        map = new Square[MAX_ROWS][MAX_COLUMNS];

        switch (mapType) {
            case MAP_1:
                for (int i=0;i<array.size();i++) {
                    if(!array.get(i).isJsonNull() && array.get(i).getAsJsonObject().get("id").getAsInt() == MAP_1) {
                        mapObject = array.get(i).getAsJsonObject();
                    }
                }
                break;

            case MAP_2:
                for (int i=0;i<array.size();i++) {
                    if(!array.get(i).isJsonNull() && array.get(i).getAsJsonObject().get("id").getAsInt() == MAP_2) {
                        mapObject = array.get(i).getAsJsonObject();
                    }
                }
                break;

            case MAP_3:
                for (int i=0;i<array.size();i++) {
                    if(!array.get(i).isJsonNull() && array.get(i).getAsJsonObject().get("id").getAsInt() == MAP_3) {
                        mapObject = array.get(i).getAsJsonObject();
                    }
                }
                break;

            case MAP_4:
                for (int i=0;i<array.size();i++) {
                    if (!array.get(i).isJsonNull() && array.get(i).getAsJsonObject().get("id").getAsInt() == MAP_4) {
                        mapObject = array.get(i).getAsJsonObject();
                    }
                }
                break;

            default:
                throw new MapUnknowException();
        }

        if (mapObject == null) throw new MapUnknowException();

        JsonArray matrix = mapObject.get("map").getAsJsonArray();

        for (int i=0;i<matrix.size();i++) {
            JsonArray row = matrix.get(i).getAsJsonArray();
            for (int j=0;j<row.size();j++) {
                if(row.get(j).isJsonNull()) {
                    map[i][j] = null;
                } else {
                    JsonObject square = row.get(j).getAsJsonObject();
                    if (square.get("isSpawn").getAsBoolean()) {
                        map[i][j] = new SpawnSquare(
                                Color.valueOf(square.get("color").getAsString()),
                                SquareAdjacency.valueOf(square.get("north").getAsString()),
                                SquareAdjacency.valueOf(square.get("east").getAsString()),
                                SquareAdjacency.valueOf(square.get("south").getAsString()),
                                SquareAdjacency.valueOf(square.get("west").getAsString())
                        );
                    } else {
                        map[i][j] = new CardSquare(
                                Color.valueOf(square.get("color").getAsString()),
                                SquareAdjacency.valueOf(square.get("north").getAsString()),
                                SquareAdjacency.valueOf(square.get("east").getAsString()),
                                SquareAdjacency.valueOf(square.get("south").getAsString()),
                                SquareAdjacency.valueOf(square.get("west").getAsString())
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
