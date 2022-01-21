import jdk.nashorn.internal.ir.IfNode;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
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
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.model.EntityModel;
import org.dreambot.api.wrappers.items.GroundItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Kills sand crabs or whatever", name = "Kills Sand Crabs", category = Category.COMBAT, version = 1.0)
public class SandCrabs extends AbstractScript{

    State state;

    Area attackSpot = new Area(new Tile(1743, 3475), new Tile (1754, 3462));
    Area resetSpot = new Area(new Tile(1706, 3467), new Tile (1710, 3463));
    Area bankSpot = new Area(new Tile(1718, 3467), new Tile (1721, 3463));
    
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

    private int arrowsStarting;
    private int arrowsUsed;

    private int reset = 0;


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
        g.drawString("Range Lvl: " + lvlRange + " | Arrows Used: " + arrowsUsed, 10, 50);
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

            case ATTACKING:
                //if (getLocalPlayer().isInCombat()){
                    if (Combat.getSpecialPercentage() >= 80 && Equipment.contains("Magic shortbow") && !Combat.isSpecialActive()){
                        if (!Tabs.isOpen(Tab.COMBAT)){
                            Tabs.open(Tab.COMBAT);
                            sleep(300,500);
                            Combat.toggleSpecialAttack(true);
                            sleep(500,800);
                            Tabs.open(Tab.INVENTORY);
                            sleep(500,800);
                        }
                    }

                    sleep(500,800);

                    if (getLocalPlayer().getHealthPercent() < 50){
                        log("Need to eat");
                        if (Inventory.contains(1993)){
                            Inventory.interact(1993, "Drink");
                        }
                    }
                    log("In combat");

                    arrowsUsed = arrowsStarting - Equipment.count("Rune arrow");

                break;

            case BANK:
                if (Inventory.count(1993) == 0){
                    Walking.walk(bankSpot);
                    sleep(500,800);
                    sleepUntil(()-> !getLocalPlayer().isMoving(), 5000);
                    if (bankSpot.contains(getLocalPlayer()) && !Bank.isOpen()){
                        Bank.open();
                        sleep(500,800);
                    }
                    if (Bank.isOpen() && !Inventory.contains(1993)){
                        Bank.depositAllItems();
                        sleep(500,800);
                        Bank.withdraw(1993, 10);
                        sleep(500,800);
                    }
                }
                break;

            case ATTACK:

                /*for (GroundItem groundItem : GroundItems.all("Rune arrow")) {
                    if (attackSpot.contains(groundItem)) {
                        log("Found item");
                        if (groundItem != null) {
                            log("Found arrows");
                            int arrows = Inventory.count("Rune arrow");
                            groundItem.interact("Take");
                            sleep(300, 400);
                            sleepUntil(() -> arrows < Inventory.count("Rune arrow"), 2000);
                            sleep(300, 400);
                        }
                    }
                }*/
                if (Inventory.contains("Rune arrow")){
                    Inventory.interact("Rune arrow", "Wield");
                    sleep(400,500);
                }

                log("No items");
                //NPC SandCrab = NPCs.closest(crab -> crab != null && crab.getName() == "Sand Crab" && attackSpot.contains(crab));
                if (attackSpot.contains(NPCs.closest("Sand Crab")) && !NPCs.closest("Sand Crab").isInCombat()){
                //if(SandCrab != null && !getLocalPlayer().isInCombat()){
                    log("Attack Sand Crab");
                    NPCs.closest("Sand Crab").interact("Attack");
                }else if(attackSpot.contains(NPCs.closest("Sandy rocks")) && !getLocalPlayer().isInCombat()) {
                    log("WE FOUND ROCKS");
                    sleep(1000,1500);
                    if(Walking.walk(NPCs.closest("Sandy rocks"))) {
                        sleep(300, 400);
                        sleepUntil(() -> !getLocalPlayer().isMoving(), 5000);
                        sleep(3000, 4000);
                    }
                    if (!attackSpot.contains(NPCs.closest("Sand Crab"))) {
                        reset = 1;
                    }
                }else if (!getLocalPlayer().isInCombat()){
                    if(!Walking.isRunEnabled() && Walking.getRunEnergy() > 20){
                        Walking.toggleRun();
                    }
                    Walking.walk(attackSpot.getRandomTile());
                    sleep(2000,3000);
                }
                sleep(500,800);
                break;

            case RESETAREA:
                Walking.walk(resetSpot.getRandomTile());
                sleep(500, 600);
                sleepUntil(() -> !getLocalPlayer().isMoving(), 15000);
                if (resetSpot.contains(getLocalPlayer())){
                    reset = 0;
                }
                break;

            case WALKTOAREA:
                Walking.walk(attackSpot.getRandomTile());
                sleep(500, 600);
                sleepUntil(() ->!getLocalPlayer().isMoving(), 10000);
                break;
        }
        return 0;
    }

    //State names
    private enum State{
        ATTACKING, ATTACK, WALKTOAREA, WIDGETVISIBLE, RESETAREA, BANK
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (Widgets.getWidget(233) != null) {
            state = State.WIDGETVISIBLE;
        }else if( Inventory.count(1993) == 0 || Inventory.isFull()){
            state = State.BANK;
        }else if(!attackSpot.contains(getLocalPlayer()) && reset ==0) {
            state = State.WALKTOAREA;
        }else if(reset == 1){
            state = State.RESETAREA;
        }else if(getLocalPlayer().isInCombat() && attackSpot.contains(getLocalPlayer())) {
            state = State.ATTACKING;
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
        arrowsStarting = Equipment.count("Rune arrow");
        timeBegan = System.currentTimeMillis();
        beginningXpRange = Skills.getExperience(Skill.RANGED);
        beginningXpDef = Skills.getExperience(Skill.DEFENCE);
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
