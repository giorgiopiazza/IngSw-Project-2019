package model.cards;

import enumerations.Ammo;
import exceptions.AdrenalinaException;
import exceptions.cards.RemainingMarksException;
import exceptions.cards.TooManyMarksException;
import model.player.Player;

public class MarkEffect extends Effect {

    private final int marks;
    private final int[] marksDistribution;

    public MarkEffect(Ammo[] cost, Target target, int marks, int[] marksDistribution) {
        super(cost, target);
        this.marks = marks;
        this.marksDistribution = marksDistribution;
    }

    public int getMarks() {
        return this.marks;
    }

    public int[] getMarksDistribution() {
        return this.marksDistribution;
    }

    @Override
    public void execute(Target target, Player markDealer) throws AdrenalinaException {
        int marksCounter = 0;
        for (int i = 0; i < target.getTarget().size(); ++i) {
            target.getTarget().get(i).getPlayerBoard().addMark(markDealer, marksDistribution[i]);
            marksCounter += marksDistribution[i];
        }
        if (marksCounter > marks) throw new TooManyMarksException(marks);
        if (marksCounter < marks) throw new RemainingMarksException(marksCounter);
    }
}
