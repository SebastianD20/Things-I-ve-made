package tasks;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.TaskNode;

public class Skeletons extends TaskNode {

    @Override
    public boolean accept() {
        // If our inventory is full, we should execute this task
        return Inventory.isEmpty();
    }

    @Override
    public int execute() {
        // This method will drop any items in our inventory that have 'ore' anywhere in its name
        Inventory.dropAll(item -> item.getName().contains("ore"));

        return Calculations.random(300, 600);
    }
}
