package gameori;
import java.lang.Math;
import static java.lang.System.out;
/* Provides a chest that contains coins */public interface TChest {
    /* Coin management */
    public int getChestMaxCoins(); /* Opens the chest */
    default int open() {
        out.print("The chest is now opened!");
        double rnd = Math.random();
        int c = (int) (rnd * getChestMaxCoins());
        out.print("You got " + c);
        out.println(" coins from the chest.");
        return c;
    }
}