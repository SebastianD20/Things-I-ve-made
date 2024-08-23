import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.ScriptManifest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


@ScriptManifest(author = "PapaH", description = "Bury big bones in Ge", name = "bury bones", category = Category.PRAYER, version = 1.0)
public class Burybones extends AbstractScript{

    State state;

    private int logsCut;
    private int logsCutHour;

    private long timeBegan;
    private long timeRan;

    private int beginningXp;
    private int currentXp;
    private int xpGained;
    private int xpPerHour;

    private int magicLvl;
    private int bonesLeft;
    private int bonesLeftBank = -1;
    private float timeTillDone;

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
        magicLvl = Skills.getRealLevel(Skill.PRAYER);
        g.drawImage(bg, 5, 20, null);

        xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));
        timeRan = System.currentTimeMillis() - this.timeBegan;
        currentXp = Skills.getExperience(Skill.PRAYER);
        xpGained = currentXp - beginningXp;
        logsCut = xpGained / 15;
        logsCutHour = (int)( logsCut / ((System.currentTimeMillis() - this.timeBegan) / 3600000.0D));

        bonesLeft = bonesLeftBank + Inventory.count("Big bones");

        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Prayer Lvl: " + magicLvl, 10, 50);
        g.drawString("Bones Buried: " + logsCut, 10, 65);
        g.drawString("Bones Per Hour: " + logsCutHour, 10, 80);
        g.drawString("XP Gained: " + xpGained, 10, 95);
        g.drawString("XP Per Hour: " + xpPerHour, 10, 110);
        g.drawString("Bones Remaining: " + bonesLeft, 10, 125);
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
            case ENCHANTING:
                if (!Tabs.isOpen(Tab.INVENTORY)) {
                    Tabs.open(Tab.INVENTORY);
                    sleep(200, 300);
                }
                if (Inventory.contains("Big bones")) {
                    Inventory.get("Big bones").interact();
                    sleep(500,600);
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
                break;

            case BANKING:
                if(!Inventory.isEmpty()){
                    Bank.depositAllItems();
                    sleep(500,800);
                }
                if (Bank.contains("Big bones")){
                    bonesLeftBank = Bank.count("Big bones");
                    Bank.withdrawAll("Big bones");
                    sleep(500,800);
                }else {
                    ScriptManager.getScriptManager().stop();
                }
                sleep(200,300);
                break;

            case CLOSEBANK:
                while(Bank.isOpen()){
                    Bank.close();
                    sleep(500,800);
                }
                break;
        }
        return 0;
    }

    //State names
    private enum State{
        BANKING, OPENBANK, CLOSEBANK, ENCHANTING
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if(Inventory.contains("Big bones") && !Bank.isOpen()){
            state = State.ENCHANTING;
        }else if(!Inventory.contains("Big bones") && !Bank.isOpen()){
            state = State.OPENBANK;
        }else if(!Inventory.contains("Big bones") && Bank.isOpen()){
            state = State.BANKING;
        }else if(Inventory.contains("Big bones") && Bank.isOpen()){
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
        beginningXp = Skills.getExperience(Skill.PRAYER);
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}
