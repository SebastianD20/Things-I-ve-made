import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Trains construction with the butler", name = "Construction Butler", category = Category.CONSTRUCTION, version = 1.0)
public class ConstructionButler extends AbstractScript{

    State state;


    private int logsCut = 0;
    
    private long timeBegan;
    private long timeRan;

    private int beginningXp;
    private int currentXp;
    private int xpGained;
    private int xpPerHour;

    private int gpPerHour;
    private int totalGpPerHour;
    private int costOfItem;
    private int gpGained;
    private int totalGpGained;

    //Variables
    private int plankID = 8778;
    private int plankIDnoted = 8779;
    private int itemToBuildID = 15403;
    private int itemToDestroyID = 13566;
    private int plankPerBuild = 8;

    private int serventID = 227;

    private int portalID = 4525;

    private final Image bg = getImage("https://i.ibb.co/WBVfzLJ/fff-text-2.png");
    private Image getImage(String url)
    {
        try
        {
            return ImageIO.read(new URL(url));
        }
        catch (IOException e) {}
        return null;
    }


    @Override // Paint
    public void onPaint(Graphics g){
        gpGained = logsCut * costOfItem;
        totalGpGained = gpGained / 1000;
        gpPerHour = (int)(gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalGpPerHour = gpPerHour / 1000;
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.CONSTRUCTION);
        xpGained = currentXp - beginningXp;
        g.drawImage(bg, 5, 20, null);
        g.drawString("State: " + (state), 10, 125);
        g.drawString("GP Gained: " + (totalGpGained) + " k", 10, 110);
        g.drawString("GP Per Hour: " + (totalGpPerHour) + " k", 10, 95);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 80);
        g.drawString("XP Gained: " + xpGained, 10, 65);
        g.drawString("Construction Lvl: " + Skills.getRealLevel(Skill.CONSTRUCTION), 10, 50);
        g.drawString("Run Time: " + ft( timeRan), 10, 35);
    }

    private String ft(long duration)
    {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);
        } else {
            res = (days + " : " + hours + " : " + minutes + " : " + seconds);
        }
        return res;
    }

    @Override //Infinite loop
    public int onLoop() {

        //Determined by which state gets returned by getState() then do that case.
        switch(getState()) {

            case CUTTINGWOOD:
                log("Building Thing");
                if (!Tabs.isOpen(Tab.INVENTORY)){
                    Tabs.open(Tab.INVENTORY);
                    sleep(300,500);
                }
                if (GameObjects.closest(itemToDestroyID) != null){
                    log("destroy a thing");
                    if(GameObjects.closest(itemToDestroyID).interact("Remove")) {
                        sleepUntil(() -> Widgets.getWidget(219) != null, 1000);
                        sleep(300, 400);
                    }
                    if (Widgets.getWidget(219).getChild(1).getChild(1).interact()) {
                        sleep(1200, 1250);
                    }
                }

                if (GameObjects.closest(itemToBuildID) != null && Inventory.count(plankID) >= plankPerBuild) {
                    log("build a thing");
                    if (GameObjects.closest(itemToBuildID).interact("Build")) {
                        sleepUntil(() -> Widgets.getWidget(458) != null, 1000);
                        sleep(300, 400);
                    }
                    if (Widgets.getWidget(458).getChild(5).interact()) {
                        sleep(1200, 1250);
                    }
                }
                if (Inventory.count(plankIDnoted) + Inventory.count(plankID) < plankPerBuild) {
                    ScriptManager.getScriptManager().stop();
                }

                break;
            case BANK:
                if (NPCs.all(serventID) != null) {
                    if (NPCs.closest(serventID).distance() > 4){
                        if (!Tabs.isOpen(Tab.OPTIONS)){
                            Tabs.open(Tab.OPTIONS);
                            sleep(800,1000);
                        }
                        if(Widgets.getWidget(116).getChild(108).interact()){
                            sleep(500,800);
                        }
                        if (Widgets.getWidget(116).getChild(74).getChild(1) != null){
                            Widgets.getWidget(116).getChild(74).getChild(1).interact();
                            sleep(300,500);
                            sleepUntil(() -> Widgets.getWidget(370) != null, 1000);
                            if(Widgets.getWidget(370).getChild(19).getChild(0).interact()) {
                                sleep(300, 500);
                            }
                            if(Widgets.getWidget(370).getChild(21).interact()) {
                                sleep(300, 500);
                            }
                            if(!Tabs.isOpen(Tab.INVENTORY)) {
                                Tabs.open(Tab.INVENTORY);
                                sleep(500,800);
                            }
                        }
                    }else if (Widgets.getWidget(219) == null){
                        NPCs.closest(serventID).interact("Talk-to");
                        sleep(300,500);
                    }
                    if (Widgets.getWidget(231) != null){
                        if(Widgets.getWidget(231).getChild(4).interact()) {
                            sleep(600, 700);
                            sleepUntil(() -> Widgets.getWidget(219) != null, 1000);
                            if (Widgets.getWidget(219).getChild(1).getChild(1).interact()) {
                                sleep(600, 700);
                            }
                        }
                    }
                    if (Widgets.getWidget(219) != null){
                        Widgets.getWidget(219).getChild(1).getChild(1).interact();
                        sleep(600,700);
                    }
                    if (Widgets.getWidget(231) != null) {
                        Widgets.getWidget(231).getChild(5).interact();
                        sleep(600,700);
                    }
                }

                break;

            case ENTERPORTAL:
                if (GameObjects.closest(15478).interact("Build mode")){
                    sleep(1000,1200);
                }
                break;

            case SLEEP:
                sleep(500,800);
                break;
        }
        return 0;
    }

    //State names
    private enum State{
        CUTTINGWOOD, BANK, ENTERPORTAL, SLEEP
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (GameObjects.closest(15478) != null){
            state = State.ENTERPORTAL;
        }else if((Inventory.emptySlotCount() >= 1) && NPCs.closest(serventID) != null){
            log("In house");
            state = State.BANK;
        }else {
            log("Exchanging notes");
            state = State.CUTTINGWOOD;
        }
        return state;
    }

    //When script start load this.
    public void onStart() {
        log("Bot Started");
        if(!Tabs.isOpen(Tab.INVENTORY)){
            sleep(1000,2500);
            Tabs.open(Tab.INVENTORY);
            sleep(1000,2500);
        }
        timeBegan = System.currentTimeMillis();
        beginningXp = Skills.getExperience(Skill.CONSTRUCTION);
        costOfItem = 53;
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
