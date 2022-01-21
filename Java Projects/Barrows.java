import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
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
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.items.GroundItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


@ScriptManifest(author = "PapaH", description = "Tries barrows or something", name = "Kills Barrows", category = Category.COMBAT, version = 1.0)
public class Barrows extends AbstractScript{

    State state;

    Area attackSpot = new Area(new Tile(1743, 3475), new Tile (1754, 3462));
    Area resetSpot = new Area(new Tile(1715, 3467), new Tile (1712, 3463));
    
    private long timeBegan;
    private long timeRan;

    private int beginningXpRange;
    private int beginningXpDef;
    private int currentXpRange;
    private int currentXpDef;
    private int xpGainedRange;
    private int xpGainedDef;
    private int xpPerHourRange;
    private int xpPerHourDef;

    private int lvlRange;
    private int lvlDef;

    private int reset = 0;

    private int brothersKilled = 0; // How many brothers have been killed this trip.
    private int brotherCave = 0; // What cave tunnel is in. 1:Dharok, 2:Guthan, 3:Karil, 4:Torag, 5:Verac, 6:Ahrim


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
    public void onPaint(Graphics g) {
        lvlRange = Skills.getRealLevel(Skill.RANGED);
        lvlDef = Skills.getRealLevel(Skill.DEFENCE);
        g.drawImage(bg, 5, 20, null);

        xpPerHourRange = (int)( xpGainedRange / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        xpPerHourDef = (int)( xpGainedDef / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;

        currentXpRange = Skills.getExperience(Skill.RANGED);
        currentXpDef = Skills.getExperience(Skill.DEFENCE);
        xpGainedRange = currentXpRange - beginningXpRange;
        xpGainedDef = currentXpDef - beginningXpDef;

        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Range Lvl: " + lvlRange, 10, 50);
        g.drawString("XP Gained: " + xpGainedRange, 10, 65);
        g.drawString("XP / Hour: " + xpPerHourRange, 10, 80);
        g.drawString("Defence Lvl: " + lvlDef, 10, 95);
        g.drawString("Gained: " + xpGainedDef, 10, 110);
        g.drawString("XP / Hour: " + xpPerHourDef, 10, 125);
        g.drawString("State: " + (state), 10, 140);
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
            res = (hours + " : " + minutes + " : " + seconds);
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
                if(Widgets.getWidget(233).getChild(3).interact()){
                    sleep(1000,2000);
                }
                sleep(500,800);
                break;

            case ATTACKING: //If already in combat.
                while (getLocalPlayer().isInCombat()){
                    sleep(500,800);
                    inCombat();
                }
                break;


            case ATTACK: // Open grave, already shoveled in

                break;

            case RESETAREA: //If teleported to Varrock
                Walking.walk(resetSpot.getRandomTile());
                sleep(500,800);
                sleepUntil(() -> !getLocalPlayer().isMoving(), 15000);
                if (resetSpot.contains(getLocalPlayer())){
                    reset = 0;
                }
                break;

            case WALKTOAREA: //After using barrows tele tab
                Walking.walk(attackSpot.getRandomTile());
                sleep(800,1000);
                sleepUntil(() ->!getLocalPlayer().isMoving(), 10000);
                break;
        }
        return 0;
    }

    public void inCombat(){
        if (Skills.getBoostedLevels(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)){
            if(Inventory.contains("Prayer potion (1)")){
                Inventory.interact("Prayer potion (1)", "Drink");
                sleep(400,600);
            }else if (Inventory.contains("Prayer potion (2)")){
                Inventory.interact("Prayer potion (2)", "Drink");
                sleep(400,600);
            }else if (Inventory.contains("Prayer potion (3)")){
                Inventory.interact("Prayer potion (3)", "Drink");
                sleep(400, 600);
            }else if (Inventory.contains("Prayer potion (4)")){
                Inventory.interact("Prayer potion (4)", "Drink");
                sleep(400, 600);
            }else{
                if(Inventory.contains("Teleport")){
                    Inventory.interact("Varrock teleport", "Teleport"); //PLACEHOLDER FIX NAMES
                }
            }
        }
        if (getLocalPlayer().getHealthPercent() < 40){
            log("Need to eat");
            if (Inventory.contains(1993)){
                Inventory.interact(1993, "Eat");
            }else{
                if(Inventory.contains("Teleport")){
                    Inventory.interact("Varrock teleport", "Teleport"); //PLACEHOLDER FIX NAMES
                }
            }
        }
    }

    //State names
    private enum State{
        ATTACKING, ATTACK, WALKTOAREA, WIDGETVISIBLE, RESETAREA, UNDERGROUND
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (Widgets.getWidget(233) != null){
            state = State.WIDGETVISIBLE;

        }else if(getLocalPlayer().isInCombat()) {
            state = State.ATTACKING;

        }else if(getLocalPlayer().isMoving()) { //After using barrows tab
            state = State.WALKTOAREA;

        }else if(reset == 1) {
            state = State.RESETAREA;

        }else if (brothersKilled == 5){ // GO TO CAVE WITH TUNNEL AND FINISH
            state = State.UNDERGROUND;
        }else {
            state = State.ATTACK;
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
        beginningXpRange = Skills.getExperience(Skill.RANGED);
        beginningXpDef = Skills.getExperience(Skill.DEFENCE);
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
