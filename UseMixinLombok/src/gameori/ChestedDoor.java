package gameori;
public class ChestedDoor implements TChestedDoor {
    /* Fields for the door */private boolean locked;
    /* Glue Code for TDoor */public boolean getLocked()
    {
        return this.locked;
    }

    /* Glue code for coin management */public int getDoorMaxCoins()
    {
        return 120;
    }
    public int getChestMaxCoins()
    {
        return 250;
    }
    /* Constructor */
    public ChestedDoor(boolean l)
    {
        setLocked(l);
    }
    /* Other helpful methods */public void setLocked(boolean l)
    {
        this.locked = l;
    }
}