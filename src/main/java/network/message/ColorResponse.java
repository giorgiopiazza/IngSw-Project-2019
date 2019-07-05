package network.message;

import enumerations.MessageContent;
import enumerations.PlayerColor;
import utility.GameConstants;
import utility.NullObjectHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Message class of response to a color request with a list of free colors
 */
public class ColorResponse extends Message {
    private static final long serialVersionUID = -5279461134770266666L;

    private final ArrayList<PlayerColor> colorList;

    public ColorResponse(List<PlayerColor> colorList) {
        super(GameConstants.GOD_NAME, null, MessageContent.COLOR_RESPONSE);
        this.colorList = NullObjectHelper.getNotNullArrayList(colorList);
    }

    public List<PlayerColor> getColorList() {
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
