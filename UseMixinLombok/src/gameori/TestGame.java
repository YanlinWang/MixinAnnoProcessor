package gameori;

public class TestGame {
    public static void main(String[] args) {
        Player player = new Player("Grace");
        TDoor l = new EnchantedDoor(false);
        TDoor r = new ChestedDoor(false);
        TDoor f = new KnockDoor(false, 50);
        DoorsRoom doorsRoom = new DoorsRoom(l, r, f);
        Game game = new Game(player, doorsRoom);
        game.getDoorsRoom().getFrontDoor().open();
    }
}
