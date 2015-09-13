package gameori;
public class KnockDoor implements TKnockDoor { /* Fields for the door */
    private boolean locked;
    /* Fields for the counter */
    private int counter;
    private int limit;
    /* Glue Code for TDoor */public boolean getLocked()
    {
        return this.locked;
    }
    /* Glue code for TCounter */public int getCounter()
    {
        return this.counter;
    }
    public void setCounter(int c)
    {
        this.counter = c;
    }
    public int getLimit()
    {
        return this.limit;
    }
    /* Glue code for coin management */public int getDoorMaxCoins()
    {
        return 120;
    }
    public int getCounterMaxCoins()
    {
        return 500;
    } /* Constructor */
    public KnockDoor(boolean l, int li) {
        setCounter(0);
        setLocked(l);
        setLimit(li);
    }
    /* Other helpful methods */
    private void setLocked(boolean l)
    {
        this.locked = l;
    }
    private void setLimit(int l)
    {
        this.limit = l;
    }
}