package exceptions.actions;

import enumerations.PossibleAction;
import exceptions.AdrenalinaRuntimeException;

public class IncompatibleActionException extends AdrenalinaRuntimeException {
    private static final long serialVersionUID = -3750021511299325464L;

    public IncompatibleActionException(PossibleAction ia) {
        super("The action: " + ia + " is incompatible with the Action object created");
    }
}
