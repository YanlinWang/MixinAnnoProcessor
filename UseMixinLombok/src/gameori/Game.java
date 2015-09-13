package gameori;

public class Game {
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
