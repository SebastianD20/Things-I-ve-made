import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Chops trees in Ge", name = "GETrees", category = Category.MAGIC, version = 1.0)
public class GEWoodcutting extends AbstractScript{

    State state;

    Area cutTrees = new Area(new Tile(3160,3449), new Tile(3150,3463));
    Area banking = new Area(new Tile(3159,3491), new Tile(3162,3487));


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
        g.drawImage(bg, 345, 350, null);
        gpGained = logsCut * costOfItem;
        totalGpGained = gpGained / 1000;
        gpPerHour = (int)(gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalGpPerHour = gpPerHour / 1000;
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.WOODCUTTING);
        xpGained = currentXp - beginningXp;
        g.drawString("GP Gained: " + (totalGpGained) + " k", 350, 440);
        g.drawString("GP Per Hour: " + (totalGpPerHour) + " k", 350, 425);
        g.drawString("XP Per Hour: " + xpPerHour, 350, 410);
        g.drawString( "lvl: " + Skills.getRealLevel(Skill.WOODCUTTING) + " | XP Gained: " + xpGained, 350, 395);
        g.drawString("Logs Cut: " + logsCut, 350, 380);
        g.drawString("Run Time: " + ft( timeRan), 350, 365);
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
                log("Cutting Trees");
                if (!getLocalPlayer().isAnimating() && !getLocalPlayer().isMoving()) {
                    if (cutTrees.contains(GameObjects.closest("Tree")) && GameObjects.closest("Tree") != null){
                        int currentLogs = Inventory.count("Logs");
                        GameObjects.closest("Tree").interact("Chop down");
                        sleep(3000, 4000);
                        sleepUntil(() -> !getLocalPlayer().isAnimating(), 15000);
                        sleep(500, 800);
                        if (Inventory.count("Logs") > currentLogs) {
                            return logsCut += 1;
                        }
                    }else{
                        Walking.walk(cutTrees.getCenter());
                        sleep(1000,2000);
                    }
                }
                sleep(500,600);
                break;
            case RUNNINGTOBANK:
                log("Running to Bank");
                if(!Walking.isRunEnabled()){
                    Walking.toggleRun();
                }
                Walking.walk(banking.getRandomTile());
                sleep(500, 700);
                sleepUntil(() -> !getLocalPlayer().isMoving(), 15000);
                sleep(500,600);
                break;
            case BANKINGWOOD:
                if (Bank.openClosest()) {
                    sleep(500, 1000);
                    if (Bank.isOpen()) {
                        Bank.depositAllItems();
                        sleepUntil(() -> !Inventory.isFull(), 3000);
                        sleep(500,1000);
                    }
                }
                break;
            case RUNNINGTOWOOD:
                log("Running to Wood");
                Walking.walk(cutTrees.getRandomTile());
                sleep(500, 700);
                sleepUntil(() -> !getLocalPlayer().isMoving(), 15000);
                sleep(500,1000);
                break;
        }
        return 0;
    }

    //State names
    private enum State{
        CUTTINGWOOD, BANKINGWOOD, RUNNINGTOWOOD, RUNNINGTOBANK
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if(!cutTrees.contains(getLocalPlayer()) && !Inventory.isFull()){
            state = State.RUNNINGTOWOOD;
        }else if(cutTrees.contains(getLocalPlayer()) && !Inventory.isFull()){
            state = State.CUTTINGWOOD;
        }else if(!banking.contains(getLocalPlayer()) && Inventory.isFull()){
            state = State.RUNNINGTOBANK;
        }else if(banking.contains(getLocalPlayer()) && Inventory.isFull()){
            state = State.BANKINGWOOD;
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
        beginningXp = Skills.getExperience(Skill.WOODCUTTING);
        costOfItem = 53;
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
