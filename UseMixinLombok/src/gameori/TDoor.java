package gameori;
import static java.lang.System.out;

/* Defines a base-door with
no particular features */
public interface TDoor {
    /* State references */
    public boolean getLocked();
    /* Coin management */
    public int getDoorMaxCoins();
    /* Tells if the door is locked or not */
    default boolean isLocked()
    {
        return getLocked();
    }
    /* Tries to open the door */
    default int open() {
        if (!isLocked()) {
            out.println("The door has been opened!");
            double rnd = Math.random();
            int cns = (int) (rnd * getDoorMaxCoins()) + 1;
            out.println("You got " + cns + " coins.");
            return cns;
        }
        else {
            out.println("This door is locked.");
            return -1;
        }
    }
    /* Performs a knock on the door */
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