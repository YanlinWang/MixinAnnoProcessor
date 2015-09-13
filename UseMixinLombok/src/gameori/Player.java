package gameori;
public class Player {
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
