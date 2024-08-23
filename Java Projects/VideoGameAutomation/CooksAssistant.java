import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(author = "PapaH", description = "Bot Template", name = "Template", category = Category.MAGIC, version = 1.0)
public class CooksAssistant extends AbstractScript{

    State state;


    @Override //Infinite loop
    public int onLoop() {

        //Determined by which state gets returned by getState() then do that case.
        switch(getState()) {

        }
        return 0;
    }

    //State names
    private enum State{

    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {


        return state;
    }

    //When script start load this.
    public void onStart() {
        log("Bot Started");
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
