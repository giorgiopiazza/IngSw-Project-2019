package enumerations;

public enum Properties {
    VISIBLE("visible"), CONCATENATED_VISIBLE("concatenatedVisible"), INLINE("inLine"),
    DISTANCE("distance"), MIN_DISTANCE("minDistance"), SAME_POSITION("samePosition"),
    TARGET_NUM("targetNum"), MAX_TARGET_NUM("maxTargetNum"), MOVE_INLINE("moveInLine"),
    MOVE("move"), MOVE_TARGET("moveTarget"), MAX_MOVE_TARGET("maxMoveTarget"),
    MOVE_TARGET_BEFORE("moveTargetBefore"), MOVE_TO_LAST_TARGET("moveToLastTarget"),
    DAMAGE_DISTRIBUTION("damageDistribution"), MARK_DISTRIBUTION("markDistribution");

    private String jKey;

    Properties(String jKey) {
        this.jKey = getJKey();
    }

    public String getJKey() {
        return this.jKey;
    }
}
