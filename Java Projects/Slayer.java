import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@ScriptManifest(author = "PapaH", description = "Bot Template", name = "SLAYER", category = Category.MAGIC, version = 1.0)
public class Slayer extends AbstractScript implements ChatListener{


    State state;

    Area bankEdgeville = new Area(new Tile(3098, 3501), new Tile (3084, 3488));

    private long timeBegan;
    private long timeRan;

    private int beginningXpSlayer;
    private int xpSlayer;
    private int lvlSlayer;

    private String task = "none";
    private int killsLeft;
    private int getKillsLeft;
    private String message1 = null;

    private int slayerXP1 = 0;
    private int SlayerXP2;

    private int slayerGem = 4155;

    private int food = 385;

    private int glory1 = 1706;
    private int glory2 = 1708;
    private int glory3 = 1710;
    private int glory4 = 1712;

    private void teleHome(){
        if (!bankEdgeville.contains(getLocalPlayer())){
            if(Inventory.contains(glory1)){
                Inventory.interact(glory1, "Rub");
                sleepUntil(() -> Widgets.getWidget(219) != null, 5000);
                Widgets.getWidget(219).getChild(1).getChild(1).interact();
                sleepUntil(() -> bankEdgeville.contains(getLocalPlayer()), 5000);
            }else if(Inventory.contains(glory2)){
                Inventory.interact(glory2, "Rub");
                sleepUntil(() -> Widgets.getWidget(219) != null, 5000);
                Widgets.getWidget(219).getChild(1).getChild(1).interact();
                sleepUntil(() -> bankEdgeville.contains(getLocalPlayer()), 5000);
            }else if(Inventory.contains(glory3)){
                Inventory.interact(glory3, "Rub");
                sleepUntil(() -> Widgets.getWidget(219) != null, 5000);
                Widgets.getWidget(219).getChild(1).getChild(1).interact();
                sleepUntil(() -> bankEdgeville.contains(getLocalPlayer()), 5000);
            }else if(Inventory.contains(glory4)){
                Inventory.interact(glory4, "Rub");
                sleepUntil(() -> Widgets.getWidget(219) != null, 5000);
                Widgets.getWidget(219).getChild(1).getChild(1).interact();
                sleepUntil(() -> bankEdgeville.contains(getLocalPlayer()), 5000);
            }
        }
    }

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

    @Override //Paint
    public void onPaint(Graphics g) {
        lvlSlayer = Skills.getRealLevel(Skill.SLAYER);
        g.drawImage(bg, 5, 20, null);
        timeRan = System.currentTimeMillis() - this.timeBegan;
        if ((slayerXP1 != 0)) {
            if (!(slayerXP1 == Skills.getExperience(Skill.SLAYER))) {
                getKillsLeft -= 1;
                slayerXP1 = Skills.getExperience(Skill.SLAYER);
            }
        }else {
            slayerXP1 = Skills.getExperience(Skill.SLAYER);
        }

        g.drawString("Run Time: " + ft( timeRan), 10, 35);
        g.drawString("Slayer Lvl: " + lvlSlayer, 10, 50);
        g.drawString("Task: " + task, 10, 65);
        g.drawString("Kills left: " + killsLeft, 10, 80);
        g.drawString("getKillsLeft: " + getKillsLeft, 10, 95);
        g.drawString(": ", 10, 110);
        g.drawString("Task: " + task, 10, 125);
        g.drawString("State: " + (state), 10, 140);
    }

    private String ft(long duration) {
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

    public void onGameMessage(Message message){
        if (message.getMessage().contains("skeletons")){
            message1 = message.getMessage();
            message1 = message1.replaceAll("[^0-9]", "");
            message1 = message1.replace("1020", "");
            task = "skeletons";
            killsLeft = Integer.parseInt(message1);
            getKillsLeft = killsLeft;
            log("Task: " + task + " | Kills Left: " + killsLeft);
        }
        if (message.getMessage().contains("hobgoblins")){
            message1 = message.getMessage();
            message1 = message1.replaceAll("[^0-9]", "");
            message1 = message1.replace("1020", "");
            task = "hobgoblins";
            killsLeft = Integer.parseInt(message1);
            getKillsLeft = killsLeft;
            log("Task: " + task + " | Kills Left: " + killsLeft);
        }
    }
    public int onLoop() {

        //Determined by which state gets returned by getState() then do that case.
        switch(getState()) {
            case HOBGOBLINS:

            case EAT:
                if (Inventory.contains(food)){
                    Inventory.interact(food, "eat");
                    sleep(500,800);
                }else if (!Inventory.contains(food)){
                    teleHome();
                }
                break;

            case CHECKTASK:
                if(Inventory.interact(4155, "Check")){
                    sleep(400,500);
                    log(task);
                    sleep(400,500);
                }
                break;

            case WAITING:
                log("Have task: " + task);
                sleep(500,800);
                break;

            case BANKING:
                teleHome();
                sleep(500,800);
                Bank.open();
                sleep(2000,3000);
                if (Bank.isOpen()){
                    Bank.depositAllItems();
                    sleepUntil(() -> Inventory.isEmpty(),5000);
                    if (Inventory.count(food) != 10){
                        Bank.withdraw(food, 10);
                        sleepUntil(() -> Inventory.count(food) == 10, 3000);
                        sleep(500,800);
                    }
                    if (!Inventory.contains(glory1) || !Inventory.contains(glory2) || !Inventory.contains(glory3) || !Inventory.contains(glory4)){
                        if (Bank.contains(glory1)){
                            Bank.withdraw(glory1,1);
                            sleep(500,800);
                        }else if(Bank.contains(glory2)){
                            Bank.withdraw(glory2,1);
                            sleep(500,800);
                        }else if(Bank.contains(glory3)){
                            Bank.withdraw(glory3,1);
                            sleep(500,800);
                        }else if(Bank.contains(glory4)){
                            Bank.withdraw(glory4,1);
                            sleep(500,800);
                        }
                    }
                    if (!Inventory.contains(slayerGem)){
                        Bank.withdraw(slayerGem, 1);
                        sleep(500,800);
                    }
                    Bank.close();
                    sleepUntil(() -> !Bank.isOpen(), 2000);
                    sleep(500,800);
                }
                break;
        }
        return 0;
    }

    //State names
    private enum State{
        BANKING, GETTASK, CHECKTASK, WAITING, EAT, HOBGOBLINS
    }

    //Checks if a certain condition is met, then return that state.
    private State getState() {
        if (getLocalPlayer().getHealthPercent() < 50){
            state = State.EAT;
        }else if (!Inventory.contains(food)){
            state = State.BANKING;
        }else if (Inventory.contains(slayerGem) && (task.contentEquals("none"))) {
            log("Task: " + task);
            state = State.CHECKTASK;
            log("CHECKING TASK");
        }else if (task == "hobgoblins"){
            state = State.HOBGOBLINS;
        }else{
            state = State.WAITING;
            log("Task found");
        }
        return state;
    }

    //When script start load this.
    public void onStart() {
        timeBegan = System.currentTimeMillis();
        beginningXpSlayer = Skills.getExperience(Skill.DEFENCE);
        log("Bot Started");
    }

    //When script ends do this.
    public void onExit() {
        log("Bot Ended");
    }

}

