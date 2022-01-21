import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
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
import org.dreambot.api.wrappers.interactive.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Chops trees in Ge", name = "PoH Prayer", category = Category.MAGIC, version = 1.0)
public class PrayerPoH extends AbstractScript{

    State state;

    Area EdgevilleBank = new Area(new Tile(3084, 3504), new Tile (3098, 3487));

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
    private int boneID = 532;
    private int herbID = 251;
    private int houseTeleID = 8013;


    private int gloryWallID = 13523;
    private int DoorIDclosed = 13101;
    private int DoorIDopen = 13103;
    private int alterID = 13197;
    private int burnerIDunlit = 13212;
    private int burnerIDlit = 13213;

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
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.PRAYER);
        xpGained = currentXp - beginningXp;
        totalGpGained = xpGained / 52;
        totalGpPerHour = (int)(totalGpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        g.drawImage(bg, 5, 20, null);
        g.drawString("State: " + (state), 10, 125);
        g.drawString("Bones buried: " + (totalGpGained), 10, 110);
        g.drawString("Bones Per Hour: " + (totalGpPerHour), 10, 95);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 80);
        g.drawString("XP Gained: " + xpGained, 10, 65);
        g.drawString("Prayer Lvl: " + Skills.getRealLevel(Skill.PRAYER), 10, 50);
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

            case WIDGETVISIBLE:
                if (Widgets.getWidget(233).getChild(3).interact()) {
                    sleep(1000, 2000);
                }
                sleep(500, 800);
                break;

            case ENTERHOUSEPORTAL:
                if (GameObjects.closest(15478).interact("Home")) {
                    sleep(800, 1000);
                }
                break;

            case TELETOBANK:
                if (GameObjects.closest(gloryWallID).interact("Edgeville")) {
                    sleepUntil(() -> EdgevilleBank.contains(getLocalPlayer()), 5000);
                    sleep(300, 400);
                }
                break;

            case BANK:
                if(!Walking.isRunEnabled() && Walking.getRunEnergy() > 20){
                    Walking.toggleRun();
                }
                if (!Bank.isOpen()) {
                    Bank.open();
                    sleepUntil(() -> Bank.isOpen(), 5000);
                    sleep(300, 500);
                }
                if (Bank.isOpen()) {
                    if (Inventory.count(herbID) < 2) {
                        Bank.withdraw(herbID, 2);
                        sleepUntil(() -> Inventory.count(herbID) == 2, 1000);
                    }
                    if (Inventory.count(herbID) == 2 && Inventory.count(houseTeleID) >= 1 && Inventory.contains(590) && Inventory.emptySlotCount() >= 1) {
                        Bank.withdrawAll(boneID);
                        sleepUntil(() -> Inventory.emptySlotCount() == 0, 1000);
                        sleep(300, 500);
                    } else if (Bank.count(boneID) > 1) {
                        ScriptManager.getScriptManager().stop();
                    }
                    if (Inventory.emptySlotCount() == 0) {
                        Bank.close();
                        sleep(700, 800);
                    }
                }
                if (Inventory.emptySlotCount() == 0) {
                    Inventory.interact(houseTeleID, "Break");
                    sleepUntil(() -> !EdgevilleBank.contains(getLocalPlayer()), 5000);
                    sleep(1200, 1500);
                }
                break;

            case PRAY:
                GameObject OpenDoor = GameObjects.closest(door -> door != null && door.getID() == DoorIDclosed && GameObjects.closest(DoorIDclosed).distance(GameObjects.closest(alterID)) == 5);
                GameObject OpenedDoor = GameObjects.closest(door -> door != null && door.getID() == DoorIDopen && GameObjects.closest(DoorIDopen).distance(GameObjects.closest(alterID)) == 6);
                if ((OpenDoor == null || OpenedDoor == null) && GameObjects.closest(burnerIDlit) == null) {
                    Walking.walk(getLocalPlayer().getX(), (getLocalPlayer().getY() - 5), getLocalPlayer().getZ());
                    sleep(300, 500);
                    sleepUntil(() -> !getLocalPlayer().isMoving(), 1000);
                }
                if (OpenDoor != null && GameObjects.closest(burnerIDlit) == null) {
                    log("Found door");
                    OpenDoor.interact("Open");
                    sleepUntil(() -> GameObjects.closest(DoorIDopen) != null, 5000);
                    sleep(300, 400);
                }
                if (OpenedDoor != null) {
                    if (GameObjects.closest(burnerIDunlit) != null && Inventory.count(herbID) > 0) {
                        GameObjects.closest(burnerIDunlit).interact("Light");
                        sleep(3000, 4000);
                    } else if (GameObjects.closest(burnerIDunlit) == null){
                        if (Inventory.contains(boneID)) {
                            Inventory.interact(boneID, "Use");
                            sleep(400, 500);
                            GameObjects.closest(alterID).interact("Use");
                            sleep(500, 700);
                            sleepUntil(() -> Inventory.count(boneID) == 0 || Widgets.getWidget(233) != null, 60000);
                        }
                    }
                    if (Inventory.count(boneID) == 0) {
                        log("Looking for glory");
                        if (GameObjects.closest(gloryWallID) != null) {
                            log("Found glory");
                            GameObjects.closest(gloryWallID).interact("Edgeville");
                            sleepUntil(() -> EdgevilleBank.contains(getLocalPlayer()), 6000);
                            sleep(300, 500);
                        }
                    }
                }
            }
        return 0;
    }

    //State names
    private enum State{
        TELETOBANK, BANK, PRAY, ENTERHOUSEPORTAL, WIDGETVISIBLE
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (Widgets.getWidget(233) != null){
            state = State.WIDGETVISIBLE;
        } else if (GameObjects.closest(15478) != null) {
            state = State.ENTERHOUSEPORTAL;
        } else if (Inventory.count(boneID) == 0 && GameObjects.closest(gloryWallID) != null){
            state = State.TELETOBANK;
        }else if(EdgevilleBank.contains(getLocalPlayer()) && Inventory.count(boneID) == 0){
            state = State.BANK;
        }else if(GameObjects.closest(alterID) != null || GameObjects.closest(4525) != null){
            state = State.PRAY;
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
        beginningXp = Skills.getExperience(Skill.PRAYER);
        costOfItem = 53;
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
        ScriptManager.getScriptManager().stop();
    }

}
