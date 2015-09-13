package gameori;
/* Puts together a door and an enchantment */
public interface TEnchantedDoor extends TDoor, TEnchantment {
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