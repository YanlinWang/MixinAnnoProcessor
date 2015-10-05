package game;

import static java.lang.System.out;
import lombok.Mixin;

@Mixin interface TDoor {
    public boolean locked();
    public int doorMaxCoins();

    default boolean isLocked() {
        return locked();
    }
    default int open() {
        if (!isLocked()) {
            out.println("The door has been opened!");
            double rnd = Math.random();
            int cns = (int) (rnd * doorMaxCoins()) + 1;
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
    public int counter();
    public void counter(int c);
    public int limit();
    /* Coin management */
    public int counterMaxCoins();
    default void incrementCounter() {
        counter(counter() + 1);
    }
    default void decrementCounter() {
        counter(counter() - 1);
    }
    default boolean hasReachedLimit() {
        return counter() >= limit();
    }
    default int releaseCoins() {
        double rnd = Math.random();
        int cns = (int) (rnd * counterMaxCoins()) + 1;
        out.println("You got " + cns + " coins.");
        return cns;
    }
}

interface TChest {
    /* Coin management */
    public int chestMaxCoins(); /* Opens the chest */
    default int open() {
        out.print("The chest is now opened!");
        double rnd = Math.random();
        int c = (int) (rnd * chestMaxCoins());
        out.print("You got " + c);
        out.println(" coins from the chest.");
        return c;
    }
}
interface TEnchantment {
    /* Coin management */
    public int enchantMaxCoins();
    /** An enchantment can give coins
    (max getEnchantMaxCoins()) or
    remove coins (max -getEnchantMaxCoins()) **/
    default int applyEnchantment() {
        out.println("\nThis is an enchantment!");
        out.print("\"If the luck is up, ");
        out.println("of coins you’ll have a cup,");
        out.print("but if no luck you got, ");
        out.println("you are gonna lose a lot.\"");
        int max = enchantMaxCoins();
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
            int c = limit();
            String sug = "never knock a door ";
            sug = sug + "more then " + c + " times.";
            out.println(sug);
        }
        return coins;
    }
}

class PlayerOri {
    private final String nickname;
    private int bag = 150; //contains the coins 
    // Constructor
    public PlayerOri(String n) {
        nickname = n;
    }
    // Setters and getters 
    public int getCoins()
    {
        return bag;
    }
    public String getNickname()
    {
        return this.nickname;
    }
    // Helpful methods
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

@Mixin
interface Player {
    int coins();
    String nickname();
    void coins(int Coins);
    default void addInBad(int amount) {
        coins(coins() + amount);
    }
    default void removeFromBag(int amount) {
        coins(coins() - amount);
    }
    default String toS() {
        String s = "I’m " + nickname();
        s += " and i’ve got " + coins();
        s += " coins in my bag.";
        return s;
    }
    //model mutable field with initialization.
    static Player of(String nickname) {
        return of(150, nickname); //TODO: discuss fields' order
    }
}

@Mixin interface DoorsRoom {
    TDoor leftDoor();
    TDoor rightDoor();
    TDoor frontDoor();
}

@Mixin interface Game {
    Player player();
    DoorsRoom doorsRoom();
    // model immutable field with default method
    default String version() {
        return "0.0";
    }
}

public class TestGame {
    public static void main(String[] args) {
        /*
        Player player = new Player("Grace");
        TDoor l = TDoor.of(false, 200);
        TDoor r = TEnchantedDoor.of(false, 10, 100);  //120 150
        TDoor f = TKnockDoor.of(true, 0, 100, 200, 200);

        DoorsRoom doorsRoom = new DoorsRoom(l, r, f);
        Game game = new Game(player, doorsRoom);
        game.getDoorsRoom().getLeftDoor().open();
        game.getDoorsRoom().getRightDoor().open();
        game.getDoorsRoom().getFrontDoor().open();
        */

        Player player = Player.of("Grace");
        TDoor l = TDoor.of(false, 200);
        TDoor r = TEnchantedDoor.of(10, false, 100);
        TDoor f = TKnockDoor.of(true, 0, 100, 200, 200);
        DoorsRoom doorsRoom = DoorsRoom.of(l, r, f);
        Game game = Game.of(doorsRoom, player);
        game.doorsRoom().leftDoor().open();
        game.doorsRoom().rightDoor().open();
        game.doorsRoom().frontDoor().open();

    }
}
