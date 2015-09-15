package game;

import static java.lang.System.out;
import lombok.Mixin;

@Mixin interface TDoor {
    public boolean Locked();
    public int DoorMaxCoins();

    default boolean isLocked() {
        return Locked();
    }
    default int open() {
        if (!isLocked()) {
            out.println("The door has been opened!");
            double rnd = Math.random();
            int cns = (int) (rnd * DoorMaxCoins()) + 1;
            out.println("You got " + cns + " coins.");
            return cns;
        } else {
            out.println("This door is locked.");
            return -1;
        }
    }
    default int knock() {
        out.print("Door says: ");
        out.print("How you dare, ");
        out.println("I am the one who knocks!");
        int c = (Math.random() < 0.8) ? 0 : 1;
        if (c > 0)
            out.println("Ow! You got a free coin!");
        return c;
    }
}

@Mixin
interface TCounter {
    /* State references */
    public int Counter();
    public void Counter(int c);
    public int Limit();
    /* Coin management */
    public int CounterMaxCoins();
    default void incrementCounter() {
        Counter(Counter() + 1);
    }
    default void decrementCounter() {
        Counter(Counter() - 1);
    }
    default boolean hasReachedLimit() {
        return Counter() >= Limit();
    }
    default int releaseCoins() {
        double rnd = Math.random();
        int cns = (int) (rnd * CounterMaxCoins()) + 1;
        out.println("You got " + cns + " coins.");
        return cns;
    }
}

interface TChest {
    /* Coin management */
    public int ChestMaxCoins(); /* Opens the chest */
    default int open() {
        out.print("The chest is now opened!");
        double rnd = Math.random();
        int c = (int) (rnd * ChestMaxCoins());
        out.print("You got " + c);
        out.println(" coins from the chest.");
        return c;
    }
}
interface TEnchantment {
    /* Coin management */
    public int EnchantMaxCoins();
    /** An enchantment can give coins
    (max getEnchantMaxCoins()) or
    remove coins (max -getEnchantMaxCoins()) **/
    default int applyEnchantment() {
        out.println("\nThis is an enchantment!");
        out.print("\"If the luck is up, ");
        out.println("of coins you’ll have a cup,");
        out.print("but if no luck you got, ");
        out.println("you are gonna lose a lot.\"");
        int max = EnchantMaxCoins();
        double rnd = Math.random();
        int cns = -max + (int) (rnd * ((max * 2) + 1));
        if (cns >= 0) {
            out.print("Ohoh! You got " + cns);
            out.println(" coins!");
        }
        else {
            out.print("You lost " + Math.abs(cns));
            out.println(" coins!");
        }
        return cns;
    }
}

@Mixin
interface TEnchantedDoor extends TDoor, TEnchantment {
    /** When you open an enchanted door,
    you break the enchantment and so
    you apply it. **/
    default int open() {
        int coins = TDoor.super.open();
        if (coins > 0) //if the door is open
            coins += applyEnchantment();
        return coins;
    }
}
@Mixin
interface TChestedDoor
        extends TDoor, TChest {
    /** When you open a chested door,
    you also get the prize from the chest.
    This overrides the TDoor’s open(). **/
    default int open() {
        int coins = TDoor.super.open();
        if (coins > 0) //if the door is open
            coins += openChest();
        return coins;
    }
    /* Alias for open() from TChest */
    default int openChest() {
        return TChest.super.open();
    }
}
@Mixin
interface TKnockDoor extends TDoor, TCounter {
    /** Every know makes the counter increment.
     * If the limit is reached, more coins are released. **/
    default int knock() {
        int coins = TDoor.super.knock();
        incrementCounter();
        if (hasReachedLimit()) {
            out.print("Ohh! A special drop for you!");
            coins += releaseCoins();
        } else {
            //Let’s give a suggestion to the player 
            out.print("Don’t challenge me... ");
            int c = Limit();
            String sug = "never knock a door ";
            sug = sug + "more then " + c + " times.";
            out.println(sug);
        }
        return coins;
    }
}

class Player {
    private final String nickname;
    private int bag = 150; //contains the coins /* Constructor */
    public Player(String n) {
        nickname = n;
    }
    /* Setters and getters */public int getCoins()
    {
        return bag;
    }
    public String getNickname()
    {
        return this.nickname;
    }
    /* Helpful methods */
    public void addInBag(int amount)
    {
        this.bag += amount;
    }
    public void removeFromBag(int amount)
    {
        this.bag -= amount;
    }
    public String toString() {
        String s = "I’m " + getNickname();
        s += " and i’ve got " + getCoins();
        s += " coins in my bag.";
        return s;
    }
}

class DoorsRoom {
    private TDoor leftDoor;
    private TDoor rightDoor;
    private TDoor frontDoor; /* Constructor */
    public DoorsRoom(TDoor l, TDoor r,
            TDoor f) {
        leftDoor = l;
        rightDoor = r;
        frontDoor = f;
    }
    /* Getters */
    public TDoor getLeftDoor()
    {
        return leftDoor;
    }
    public TDoor getRightDoor()
    {
        return rightDoor;
    }
    public TDoor getFrontDoor()
    {
        return frontDoor;
    }
}

/* TODO: 
 * option1: find a cool solution. maybe use flag.
 * option2: limitation: mutable fields with initialization.. can still use classes
 */
interface TGame {
    boolean Flag();
    void Flag(boolean f);
    default String Version() {
        if (Flag())
            return "0.0";
        else
            return null;
    }
    void Version(String v);

}

class Game {
    private Player player;
    private DoorsRoom doorsRoom;
    private final String version = "0.0"; /* Constructor */
    public Game(Player p, DoorsRoom dr) {
        player = p;
        doorsRoom = dr;
    }
    /* Setters and getters */public Player getPlayer()
    {
        return player;
    }
    public DoorsRoom getDoorsRoom()
    {
        return doorsRoom;
    }
}

public class TestGame {
    public static void main(String[] args) {
        Player player = new Player("Grace");
        TDoor l = TDoor.of(false, 200);
        TDoor r = TEnchantedDoor.of(false, 10, 100);  //120 150
        TDoor f = TKnockDoor.of(true, 0, 100, 200, 200);

        DoorsRoom doorsRoom = new DoorsRoom(l, r, f);
        Game game = new Game(player, doorsRoom);
        game.getDoorsRoom().getLeftDoor().open();
        game.getDoorsRoom().getRightDoor().open();
        game.getDoorsRoom().getFrontDoor().open();
    }
}
