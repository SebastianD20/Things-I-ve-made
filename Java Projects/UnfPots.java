import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.chatbox.ChatboxMessage;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Makes unf pots in Ge and restocks", name = "GE Unf Pots", category = Category.MONEYMAKING, version = 1.0)
public class UnfPots extends AbstractScript{

    State state;

    private int logsCut = 0;
    private int totalGP = 0;
    
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

    private int magicLvl;

    private int buyamount;
    private int buyRunes;
    private int timeToGe = 0;
    private int casts;


    private String item1 = "Grimy ranarr";
    private String item1noted = "Grimy ranarr noted";
    private String item2 = "Vial of water";
    private String finishedItem = "Ranarr potion (unf)";

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
        gpGained = logsCut * costOfItem;
        totalGpGained = gpGained / 1000;
        gpPerHour = (int)(gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        casts = (int)(logsCut / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
        totalGpPerHour = gpPerHour / 1000;
        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.MAGIC);
        xpGained = currentXp - beginningXp;
        logsCut = xpGained/15;
        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Lvl: " + magicLvl + " | Casts: " + logsCut, 10, 50);
        g.drawString("Casts Per Hour: " + (casts), 10, 65);
        g.drawString("XP Gained: " + xpGained, 10, 80);
        g.drawString("GP Gained: " + (totalGpGained) + " k", 10, 95);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 110);
        g.drawString("GP Per Hour: " + (totalGpPerHour) + " k", 10, 125);
        g.drawString("Last GP check: " + (totalGP/1000) + "k", 10, 140);
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
            case ENCHANTING:

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
                int amount = Inventory.count(finishedItem);
                if (Inventory.contains("Games necklace(8)")) {
                    if (GrandExchange.sellItem(finishedItem, amount, 800)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 15000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                totalGP = Inventory.count(995);
                buyamount = Inventory.count(995) / 7800;
                if (buyamount > 500){
                    buyamount = 500;
                }
                sleep(1000, 2000);
                if (!Inventory.contains(item1)) {
                    if (GrandExchange.buyItem(item1, buyamount, 7800)) {
                        if (sleepUntil(GrandExchange::isReadyToCollect, 60000)) {
                            sleep(500, 800);
                            GrandExchange.collect();
                            sleep(500, 800);
                        }
                    }
                }
                sleep(1000, 2000);
                if (Inventory.contains(item1)) {
                    timeToGe = 0;
                }
                break;

            case OPENBANK:
                while(!Bank.isOpen()) {
                    Bank.open();
                    sleep(800, 1000);
                }

            case BANKING:
                if((Bank.contains(item1) || Inventory.contains(item2)) && Inventory.contains(995)){
                    while (Inventory.contains(995)){
                        if(Bank.depositAll(995)){
                            sleep(500,800);
                        }
                    }
                }
                while (Inventory.contains(finishedItem)) {
                    if (Bank.depositAll(finishedItem)) {
                        sleep(500, 800);
                    }
                }
                while (Inventory.contains(item1noted)) {
                    if (Bank.depositAll(item1noted)) {
                        sleep(500, 800);
                    }
                }
                while (Inventory.count(item2) == 0 && Bank.count(item2) > 0){
                    if (Bank.withdraw(item2, 14)){
                        sleep(500,800);
                    }
                }
                while (Inventory.count(item1) == 0 && Bank.count(item1) > 0){
                    if (Bank.withdraw(item2, 14)){
                        sleep(500,800);
                    }
                }
                if(!Bank.contains(item1) && !Inventory.contains(item1)){
                    if(Bank.contains(finishedItem)) {
                        Bank.setWithdrawMode(BankMode.NOTE);
                        sleep(500,800);
                        Bank.withdrawAll(finishedItem);
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
        BANKING, OPENBANK, CLOSEBANK, ENCHANTING, BUYINGMORE, OPENGE, CLOSEGE
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if(Inventory.contains(1656) && Inventory.contains("cosmic rune") && !GrandExchange.isOpen() && !Bank.isOpen()){
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
        costOfItem = 150;
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
