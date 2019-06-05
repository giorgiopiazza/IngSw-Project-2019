package model.player;

import java.io.Serializable;
import java.util.Objects;

public class KillShot implements Serializable {
    private static final long serialVersionUID = 7176239169510722056L;

    private final String killer;
    private final int points;

    public KillShot(String killer, int points) {
        this.killer = killer;
        this.points = points;
    }

    public String getKiller() {
        return killer;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KillShot killShot = (KillShot) o;
        return points == killShot.points &&
                Objects.equals(killer, killShot.killer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(killer, points);
    }
}
