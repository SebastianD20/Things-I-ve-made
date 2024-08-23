import jdk.nashorn.internal.runtime.Timing;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
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
import org.dreambot.api.wrappers.items.Item;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.dreambot.api.methods.tabs.Tabs.logout;


@ScriptManifest(author = "PapaH", description = "Alch Earth Battlestaffs in GE with restock, Start near south ge clerks",
        name = "GE Alcher", category = Category.MAGIC, version = 1.0)
public class HighAlch extends AbstractScript{

    State state;

    Area antibanArea = new Area(new Tile(3167,3487), new Tile(3162,3487));

    private int logsCut;
    
    private long timeBegan;
    private long timeRan;
    private int beginningXp;
    private int currentXp;
    private int xpGained;
    private int xpPerHour;
    private int totalGpPerHour;
    private int totalGpGained;
    private int magicLvl;

    private int buyamount;
    private int buyRunes;
    private int casts;

    private int startGP = 0;

    private String item2alch = "Earth battlestaff";
    private int itemPrice = 9100;
    private String rune = "Nature rune";
    private int runePrice = 225;
    private int highAlchPrice = 9300;

    private int restockAmount = 500;

    private int profitPer = 161;

    private int antiBanPercent =  5;
    private int antiBan;

    private int restock = 0;


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
        magicLvl = Skills.getRealLevel(Skill.MAGIC);
        g.drawImage(bg, 5, 20, null);
        totalGpPerHour = ((int)(totalGpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D)));
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.MAGIC);
        xpGained = currentXp - beginningXp;
        casts = xpPerHour/65;
        logsCut = xpGained/65;
        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Lvl: " + magicLvl + " | Casts: " + logsCut, 10, 50);
        g.drawString("Casts Per Hour: " + (casts), 10, 65);
        g.drawString("XP Gained: " + xpGained, 10, 80);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 95);
        g.drawString("GP Gained: " + (totalGpGained), 10, 110);
        g.drawString("GP Per Hour: " + (totalGpPerHour), 10, 125);
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
            case ALCHING:
                if(!Inventory.slotContains(11,item2alch) && Inventory.contains(item2alch)) {
                    if (!Tabs.isOpen(Tab.INVENTORY)){
                        Tabs.open(Tab.INVENTORY);
                    }
                    sleep(500,1000);
                    Mouse.move(Inventory.slotBounds(Inventory.slot(item2alch)));
                    Mouse.drag(Inventory.slotBounds(11));
                    sleep(500,600);
                }
                if (!Tabs.isOpen(Tab.MAGIC)) {
                    Tabs.open(Tab.MAGIC);
                    sleep(200, 300);
                }
                sleep(100, 200);
                if (Inventory.contains(item2alch)) {
                    Magic.castSpell(Normal.HIGH_LEVEL_ALCHEMY);
                    sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY),2000);
                    sleep(100,1000);
                }
                if (!Tabs.isOpen(Tab.INVENTORY)){
                    Tabs.open(Tab.INVENTORY);
                }
                if (Inventory.contains(item2alch)) {
                    Inventory.get(item2alch).interact();
                    sleepUntil(() -> Tabs.isOpen(Tab.MAGIC), 2000);
                    sleep(100,1000);
                } else {
                    Magic.deselect();
                }
                sleep(650, 700);
                antiBan = (int)(Math.random()*(1000));
                log(antiBan);
                if (antiBan<=antiBanPercent){
                    sleep(2000,4000);
                    Tabs.open(Tab.SKILLS);
                    sleep(2000,4000);
                    Skills.hoverSkill(Skill.MAGIC);
                    sleep(3000,20000);
                    Walking.walk(antibanArea.getRandomTile());
                    sleep(1000,3000);
                }
                break;


            case CLOSEGE:
                GrandExchange.close();
                sleep(500, 800);
                if (!Tabs.isOpen(Tab.INVENTORY)){
                    Tabs.open(Tab.INVENTORY);
                    sleep(500,1000);
                }
                break;


            case OPENGE:
                while (Magic.isSpellSelected()){
                    Magic.deselect();
                    sleep(500,800);
                }
                GrandExchange.open();
                sleep(500, 800);
                break;


            case BUYINGMORE:
                sleep(1000, 2000);
                if (startGP == 0) {
                    startGP = Inventory.count(995);
                }
                totalGpGained = Inventory.count(995) - startGP;

                buyamount = Inventory.count(995) / 9300;
                if (buyamount > restockAmount) {
                    buyamount = restockAmount;
                }
                sleep(1000, 2000);
                if (!Inventory.contains(item2alch)) {
                    if (GrandExchange.buyItem(item2alch, buyamount, itemPrice)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 30000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                if (Inventory.count(item2alch) > Inventory.count(rune)) {
                    buyRunes = (Inventory.count(item2alch) - Inventory.count(rune));
                    sleep(100, 200);
                    if (GrandExchange.buyItem(rune, buyRunes, runePrice)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 30000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                if (Inventory.contains(item2alch) && Inventory.contains(rune)) {
                }
                break;

            case QUIT:
                /*sleep(1000,2000);
                if (!Tabs.isOpen(Tab.LOGOUT)){
                    Tabs.open(Tab.LOGOUT);
                    sleepUntil(() -> Tabs.isOpen(Tab.LOGOUT), 2000);
                }
                if (Tabs.isOpen(Tab.LOGOUT)){
                    if (Widgets.getWidget(182) != null){
                        Widgets.getWidget(182).getChild(9).interact();
                        sleepUntil(() -> Widgets.getWidget(162) == null, 2000);
                        sleep(1000,2000);
                    }
                }
                if (!getLocalPlayer().exists()) {
                    ScriptManager.getScriptManager().stop();
                }
                 */
                logout();
                ScriptManager.getScriptManager().stop();
                break;


        }
        return 0;
    }

    //State names
    private enum State{
        ALCHING, BUYINGMORE, OPENGE, CLOSEGE, QUIT
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if(Inventory.contains(item2alch) && Inventory.contains(rune) && !GrandExchange.isOpen() && !Bank.isOpen()){
            state = State.ALCHING;
        }else if(Inventory.contains(item2alch) && Inventory.contains(rune) && GrandExchange.isOpen() && !Bank.isOpen()){
            state = State.CLOSEGE;
        }else if((!Inventory.contains(item2alch) || !Inventory.contains(rune)) && GrandExchange.isOpen() && !Bank.isOpen() && restock == 1) {
            state = State.BUYINGMORE;
        }else if(!GrandExchange.isOpen() && !Bank.isOpen() && restock == 1) {
            state = State.OPENGE;
        }else if (restock == 0){
            state = State.QUIT;
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
        beginningXp = Skills.getExperience(Skill.MAGIC);
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
        ScriptManager.getScriptManager().stop();
    }

}
