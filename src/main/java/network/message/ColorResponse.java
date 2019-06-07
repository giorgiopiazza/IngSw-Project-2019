package network.message;

import enumerations.MessageContent;
import enumerations.PlayerColor;
import model.Game;

import java.util.ArrayList;

public class ColorResponse extends Message {
    private static final long serialVersionUID = -5279461134770266666L;

    private final ArrayList<PlayerColor> colorList;

    public ColorResponse(ArrayList<PlayerColor> colorList) {
        super(Game.GOD, null, MessageContent.COLOR_RESPONSE);
        this.colorList = colorList;
    }

    public ArrayList<PlayerColor> getColorList() {
        return colorList;
    }
}
