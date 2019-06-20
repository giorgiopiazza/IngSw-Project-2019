package network.message;

import enumerations.MessageContent;
import enumerations.PlayerColor;
import model.Game;
import utility.GameCostants;

import java.util.ArrayList;
import java.util.Arrays;

public class ColorResponse extends Message {
    private static final long serialVersionUID = -5279461134770266666L;

    private final ArrayList<PlayerColor> colorList;

    public ColorResponse(ArrayList<PlayerColor> colorList) {
        super(GameCostants.GOD_NAME, null, MessageContent.COLOR_RESPONSE);
        this.colorList = colorList;
    }

    public ArrayList<PlayerColor> getColorList() {
        return colorList;
    }

    @Override
    public String toString() {
        return "ColorResponse{" +
                "content=" + getContent() +
                ", colorList=" + (colorList == null ? "null" :Arrays.toString(colorList.toArray())) +
                '}';
    }
}
