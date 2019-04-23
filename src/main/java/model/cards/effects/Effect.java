package model.cards.effects;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Effect {
    private Map<String, String> properties;

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void addProperties(Map<String, String> addingMap, String separator) {
        Set<String> addKeys = addingMap.keySet();
        Iterator<String> addIterator = addKeys.iterator();

        this.properties.put(separator, separator);
        while(addIterator.hasNext()) {
            String tempKey = addIterator.next();
            String tempValue = addingMap.get(tempKey);
            this.properties.put(tempKey, tempValue);
        }
    }

    /**
     * Method that executes the effect of a Weapon dealing, marking or moving TargetPlayers
     *
     * @param command that will be executed
     */
    public abstract void execute(String command);

    /**
     * Executes the target validation of the command
     * @param command sent
     * @return {@code true} if the command is valid, {@code false} otherwise
     */
    public abstract boolean validate (String command);
}
