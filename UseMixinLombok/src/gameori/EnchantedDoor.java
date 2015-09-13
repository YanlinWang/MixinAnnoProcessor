package gameori;
public class EnchantedDoor implements TEnchantedDoor {
    /* Fields for the door */
    private boolean locked;
    /* Glue Code for TDoor */
    public boolean getLocked() {
        return locked;
    }
    /* Glue code for coin management */
    public int getDoorMaxCoins() {
        return 120;
    }
    public int getEnchantMaxCoins() {
        return 150;
    }
    /* Constructor */
    public EnchantedDoor(boolean l) {
        setLocked(l);
    }
    /* Other helpful methods */
    public void setLocked(boolean l) {
        this.locked = l;
    }
}