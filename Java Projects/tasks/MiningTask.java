package src.tasks;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;

public class MiningTask extends TaskNode {

    @Override
    public boolean accept() {
        // If our inventory isn't full and we're not mining, we should start
        return !Inventory.isFull() && !isMining();
    }

    @Override
    public int execute() {
        GameObject rock = getClosestRock();

        // If there aren't any available rocks near us, we should just wait until one's available
        if (rock == null) return Calculations.random(500, 1000);

        if (rock.interact("Mine")) { // If we successfully click on the rock
            sleepUntil(this::isMining, 2500); // Wait until we're mining, with a max wait time of 2,500ms (2.5 seconds)
        }

        return Calculations.random(500, 1000);
    }

    /**
     * Finds the closest acceptable rock that we can mine
     *
     * @return The closest GameObject that has the name 'Rocks', with the action 'Mine', and non-null model colors
     */
    private GameObject getClosestRock() {
        return GameObjects.closest(object -> object.getName().equalsIgnoreCase("Rocks") && object.hasAction("Mine") && object.getModelColors() != null);
    }

    /**
     * For part 1, we'll consider our player doing any animation as mining
     * This will be improved/changed in a future part
     *
     * @return true if the player is mining, otherwise false
     */
    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }

}
