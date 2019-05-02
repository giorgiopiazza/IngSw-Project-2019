package exceptions.actions;

import enumerations.PossibleAction;
import exceptions.AdrenalinaRuntimeException;

public class IncompatibleActionException extends AdrenalinaRuntimeException {
    public IncompatibleActionException(PossibleAction ia) {
        super("The action: " + ia + " is incompatible with the Action object created");
    }
}
