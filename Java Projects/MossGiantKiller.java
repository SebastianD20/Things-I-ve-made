import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
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
import org.dreambot.api.wrappers.widgets.WidgetChild;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Kills moss giants whereve", name = "Moss giant Slayer", category = Category.COMBAT, version = 1.0)
public class MossGiantKiller extends AbstractScript{

    State state;
    Area safeSpot = new Area(new Tile(2549, 3415), new Tile(2550,3416));
    Area attackSpot = new Area(new Tile(2549, 3415), new Tile (2555, 3408));

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

    private int Lvl;

    private int lootOn = 0;


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
        Lvl = Skills.getRealLevel(Skill.RANGED);
        g.drawImage(bg, 5, 20, null);
        gpGained = logsCut * costOfItem;
        totalGpGained = gpGained / 1000;
        gpPerHour = (int)(gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalGpPerHour = gpPerHour / 1000;
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.RANGED);
        xpGained = currentXp - beginningXp;
        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Lvl: " + Lvl + " | Casts: " + logsCut, 10, 50);
        g.drawString("Casts Per Hour: ", 10, 65);
        g.drawString("XP Gained: " + xpGained, 10, 80);
        g.drawString("GP Gained: " + (totalGpGained) + " k", 10, 95);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 110);
        g.drawString("GP Per Hour: " + (totalGpPerHour) + " k", 10, 125);
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
                Widgets.getWidget(233).getChild(3).interact();
                sleep(500,800);
                break;

            case ATTACKING:
                while (getLocalPlayer().isInCombat()){
                    sleep(500,800);
                    if (Inventory.contains("Iron knife")){
                        log("Wield knife");
                        Inventory.interact("Iron knife", "Wield");
                    }
                    log("In combat");
                }
                break;

            case LOOT:
                for (GroundItem groundItem : GroundItems.all("Feather", "Bones", "Iron knife")){
                    if (groundItem!=null){
                        groundItem.interact("Take");
                        sleep(500,800);
                        sleepUntil(() ->!getLocalPlayer().isMoving(),10000);
                        sleep(300,500);
                    }
                }
                sleep(500,800);
                if (Inventory.contains("Bones")){
                    log("Bury bones");
                    Inventory.interact("Bones", "Bury");
                }
                sleep(500,800);
                break;

            case ATTACK:
                if (Players.all().size() == 1){
                    if (attackSpot.contains(NPCs.closest("Moss giant"))){
                        log("Attack Moss giant");
                        NPCs.closest("Moss giant").interact("Attack");
                    }else{
                        log("No moss giant near by");
                    }
                    sleep(500,800);
                }else{
                    sleep(1000,2000);
                    log("Player Nearby");
                }

                break;

            case WALKTOAREA:
                Walking.walk(safeSpot.getRandomTile());
                sleep(800,1000);
                sleepUntil(() ->!getLocalPlayer().isMoving(), 10000);
                sleep(100,300);
        }
        return 0;
    }

    //State names
    private enum State{
        ATTACKING, ATTACK, LOOT, WALKTOAREA, WIDGETVISIBLE
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (Widgets.getWidget(233) != null){
            state = State.WIDGETVISIBLE;
        }else if(!safeSpot.contains(getLocalPlayer())){
            state = State.WALKTOAREA;
        }else if(getLocalPlayer().isInCombat()) {
            state = State.ATTACKING;
        }else if (lootOn == 1 && safeSpot.contains(GroundItems.closest("Feather", "Bones", "Iron knife"))) {
            state = State.LOOT;
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
        beginningXp = Skills.getExperience(Skill.RANGED);
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
