package gameori;
/* Puts together a door and a counter */
import static java.lang.System.out;
public interface TKnockDoor extends TDoor, TCounter {
    /** Every know makes the counter increment.
     * If the limit is reached, more coins are released. **/
    default int knock() {
        int coins = TDoor.super.knock(); incrementCounter(); if(hasReachedLimit()) {
        out.print("Ohh! A special drop for you!");
        coins += releaseCoins();
        } else {
            //Let’s give a suggestion to the player 
            out.print("Don’t challenge me... ");
            int c = getLimit();
            String sug = "never knock a door ";
            sug = sug + "more then " + c + " times.";
            out.println(sug);
        }
        return coins;
    }
}
