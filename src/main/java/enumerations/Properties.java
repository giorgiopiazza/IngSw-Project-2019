package enumerations;

public enum Properties {
    VISIBLE("visible"),
    CONCATENATED_VISIBLE("concatenatedVisible"),
    INLINE("inLine"),
    DISTANCE("distance"),
    MIN_DISTANCE("minDistance"),
    SAME_POSITION("samePosition"),
    TARGET_NUM("targetNum"),
    MAX_TARGET_NUM("maxTargetNum"),
    MOVE_INLINE("moveInLine"),
    MOVE("move"),
    MOVE_IN_MIDDLE("moveInMiddle"),
    MOVE_TARGET("moveTarget"),
    MAX_MOVE_TARGET("maxMoveTarget"),
    MOVE_TARGET_BEFORE("moveTargetBefore"),
    MOVE_TO_LAST_TARGET("moveToLastTarget"),
    DAMAGE_DISTRIBUTION("damageDistribution"),
    MARK_DISTRIBUTION("markDistribution"),
    TP("tp");

    private String jKey;

    Properties(String jKey) {
        this.jKey = jKey;
    }

    public String getJKey() {
        return this.jKey;
    }
}
