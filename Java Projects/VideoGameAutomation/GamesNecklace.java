import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.magic.Spell;
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
import org.dreambot.api.wrappers.interactive.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Enchants Sapphire necklaces in Ge", name = "GE Games Necklace", category = Category.MAGIC, version = 1.0)
public class GamesNecklace extends AbstractScript{

    State state;

    private int logsCut = 0;
    private int totalGP = 0;
    
    private long timeBegan;
    private long timeRan;

    private int beginningXp;
    private int currentXp;
    private int xpGained;
    private int xpPerHour;

    private int totalGpGained;
    private int startingGP = 0;

    private int magicLvl;

    private int buyamount;
    private int buyRunes;
    private int timeToGe = 0;
    private int casts;

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
        casts = (int)(logsCut / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.MAGIC);
        xpGained = currentXp - beginningXp;
        logsCut = xpGained/15;
        totalGpGained = totalGP - startingGP;
        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Lvl: " + magicLvl + " | Casts: " + logsCut, 10, 50);
        g.drawString("Casts Per Hour: " + (casts), 10, 65);
        g.drawString("XP Gained: " + xpGained, 10, 80);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 95);
        g.drawString("Starting GP: " + (startingGP/1000) + "k", 10, 110);
        g.drawString("Last GP check: " + (totalGP/1000) + "k", 10, 125);
        g.drawString("Profit: " + (totalGpGained/1000) + " k", 10, 140);
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
            case CLOSEFILTER:
                if (!Tabs.isOpen(Tab.MAGIC)){
                    Tabs.open(Tab.MAGIC);
                    sleep(400,500);
                }
                if (Widgets.getWidget(218).getChild(197).getChild(4).interact()){
                    sleep(500,800);
                }
                break;

            case ENCHANTING:
                if (!Tabs.isOpen(Tab.MAGIC)) {
                    Tabs.open(Tab.MAGIC);
                    sleep(200, 300);
                }
                sleep(100, 200);
                if (Inventory.contains("Sapphire necklace")) {
                    Magic.castSpell(Normal.LEVEL_1_ENCHANT);
                    }
                sleep(100, 300);
                sleepUntil(() -> Tabs.isOpen(Tab.INVENTORY), 5000);
                sleep(100, 300);
                if (!Tabs.isOpen(Tab.INVENTORY)){
                    Tabs.open(Tab.INVENTORY);
                }
                if (Inventory.contains("Sapphire necklace")) {
                    Inventory.get("Sapphire necklace").interact();
                } else {
                    Magic.deselect();
                }
                sleep(600, 800);
                break;
            case CLOSEGE:
                GrandExchange.close();
                sleep(500, 800);
                break;
            case OPENGE:
                GrandExchange.open();
                sleep(500, 800);
                break;
            case BUYINGMORE:
                sleep(1000, 2000);
                int amount = Inventory.count(3854);
                if (Inventory.contains("Games necklace(8)")) {
                    if (GrandExchange.sellItem("Games necklace(8)", amount, 800)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 15000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                totalGP = Inventory.count(995);
                if (startingGP == 0){
                    startingGP = totalGP;
                }
                buyamount = Inventory.count(995) / 800;
                if (buyamount > 500){
                    buyamount = 500;
                }
                sleep(1000, 2000);
                if (!Inventory.contains("Sapphire necklace")) {
                    if (GrandExchange.buyItem("Sapphire necklace", buyamount, 600)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                if (Inventory.count("Sapphire necklace") > Inventory.count("Cosmic rune") ) {
                    buyRunes = (Inventory.count("Sapphire necklace") - Inventory.count("Cosmic rune"));
                    sleep(100,200);
                    if (GrandExchange.buyItem("Cosmic rune", buyRunes, 180)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                if (Inventory.contains("Sapphire necklace") && Inventory.contains("Cosmic rune")) {
                    timeToGe = 0;
                }
                break;
            case OPENBANK:
                while (Magic.isSpellSelected()){
                    Magic.deselect();
                    sleep(500,800);
                }
                while(!Bank.isOpen()) {
                    Bank.open();
                    sleep(800, 1000);
                }
            case BANKING:
                if((Bank.contains("Sapphire necklace") || Inventory.contains("Sapphire necklace")) && Inventory.contains(995)){
                    while (Inventory.contains(995)){
                        if(Bank.depositAll(995)){
                            sleep(500,800);
                        }
                    }
                }
                while (Inventory.contains(1657)) {
                    if (Bank.depositAll(1657)) {
                        sleep(500, 800);
                    }
                }
                while (Inventory.contains(3853)) {
                    if (Bank.depositAll(3853)) {
                        sleep(500, 800);
                    }
                }
                while (Bank.contains("Cosmic rune")){
                    if (Bank.withdrawAll("Cosmic rune")){
                        sleep(500,800);
                    }
                }
                if (Bank.withdrawAll(1656)){
                    sleep(800,1000);
                }
                if(!Bank.contains("Sapphire necklace") && !Inventory.contains("Sapphire necklace")){
                    if(Bank.contains("Games necklace(8)")) {
                        Bank.setWithdrawMode(BankMode.NOTE);
                        sleep(500,800);
                        Bank.withdrawAll("Games necklace(8)");
                    }
                    if(Bank.contains(995)) {
                        sleep(500,800);
                        Bank.withdrawAll(995);
                    }
                    sleep(500,800);
                    timeToGe = 1;
                }
                sleep(500,800);
            case CLOSEBANK:
                while(Bank.isOpen()){
                    Bank.close();
                    sleep(500,800);
                }
        }
        return 0;
    }

    //State names
    private enum State{
        BANKING, OPENBANK, CLOSEBANK, ENCHANTING, BUYINGMORE, OPENGE, CLOSEGE, CLOSEFILTER
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if(Widgets.getWidget(218).getChild(194).getChild(7)!=null){
            state = State.CLOSEFILTER;
        }else if(Inventory.contains(1656) && Inventory.contains("cosmic rune") && !GrandExchange.isOpen() && !Bank.isOpen()){
            state = State.ENCHANTING;
        }else if(Inventory.contains(1656) && Inventory.contains("cosmic rune") && GrandExchange.isOpen() && !Bank.isOpen()){
            state = State.CLOSEGE;
        }else if((!Inventory.contains(1656) || !Inventory.contains("cosmic rune")) && GrandExchange.isOpen() && !Bank.isOpen() && (timeToGe == 1)) {
            state = State.BUYINGMORE;
        }else if(!GrandExchange.isOpen() && !Bank.isOpen() && (timeToGe == 1)) {
            state = State.OPENGE;
        }else if(!Inventory.contains(1656) && !Bank.isOpen() && (timeToGe == 0)){
            state = State.OPENBANK;
        }else if(!Inventory.contains(1656) && Inventory.contains("Cosmic rune") && Bank.isOpen()){
            state = State.BANKING;
        }else if(Inventory.contains(1656) && Inventory.contains("cosmic rune") && !GrandExchange.isOpen() && Bank.isOpen()){
            state = State.CLOSEBANK;
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
    }

}
