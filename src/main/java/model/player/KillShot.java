package model.player;

import java.util.Objects;

public class KillShot {

    private Player killer;
    private boolean overKill;

    public KillShot(Player killer) {
        this.killer = killer;
        this.overKill = false;
    }

    public KillShot(Player killer, boolean overKill) {
        this.killer = killer;
        this.overKill = overKill;
    }

    public Player getKiller() {
        return killer;
    }

    public void setKiller(Player killer) {
        this.killer = killer;
    }

    public boolean isOverKill() {
        return overKill;
    }

    public void setOverKill(boolean overKill) {
        this.overKill = overKill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KillShot)) return false;
        KillShot killShot = (KillShot) o;
        return isOverKill() == killShot.isOverKill() &&
                Objects.equals(getKiller(), killShot.getKiller());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKiller(), isOverKill());
    }
}
