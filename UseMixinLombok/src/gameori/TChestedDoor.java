package gameori;
/* Puts together a door and a chest */ public interface TChestedDoor
extends TDoor, TChest {
    /** When you open a chested door,
you also get the prize from the chest.
This overrides the TDoor’s open(). **/
    default int open() {
        int coins = TDoor.super.open(); if(coins > 0) //if the door is open
            coins += openChest();
        return coins;
    }
    /* Alias for open() from TChest */
    default int openChest() {
        return TChest.super.open();
    }
}
