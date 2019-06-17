package network.message;

import enumerations.MessageContent;
import model.cards.WeaponCard;
import model.player.PlayerPosition;

import java.util.ArrayList;
import java.util.Arrays;

public class MovePickRequest extends ActionRequest {
    private static final long serialVersionUID = 8063700627094470135L;

    private final WeaponCard addingWeapon;
    private final WeaponCard discardingWeapon;

    public MovePickRequest(String username, String token, PlayerPosition senderMovePosition, ArrayList<Integer> paymentPowerups, WeaponCard addingWeapon, WeaponCard discardingWeapon) {
        super(username, token, MessageContent.MOVE_PICK, senderMovePosition, paymentPowerups);

        this.addingWeapon = addingWeapon;
        this.discardingWeapon = discardingWeapon;
    }

    public WeaponCard getAddingWeapon() {
        return this.addingWeapon;
    }

    public WeaponCard getDiscardingWeapon() {
        return this.discardingWeapon;
    }

    @Override
    public String toString() {
        return "MovePickRequest{" +
                "senderUsername=" + getSenderUsername() +
                ", content=" + getContent() +
                ", senderMovePosition=" + getSenderMovePosition() +
                ", paymentPowerups=" + (getPaymentPowerups() == null ? "null" : Arrays.toString(getPaymentPowerups().toArray())) +
                ", addingWeapon=" + addingWeapon +
                ", discardingWeapon=" + discardingWeapon +
                '}';
    }
}
