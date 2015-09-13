package gameori;
import java.lang.Math;
import static java.lang.System.out;
/* Provides an enchantment that
can give or take coins */ public interface TEnchantment {
    /* Coin management */
    public int getEnchantMaxCoins(); /** An enchantment can give coins
(max getEnchantMaxCoins()) or
remove coins (max -getEnchantMaxCoins()) **/ 
    default int applyEnchantment() {
        out.println("\nThis is an enchantment!");
        out.print("\"If the luck is up, ");
        out.println("of coins you’ll have a cup,");
        out.print("but if no luck you got, ");
        out.println("you are gonna lose a lot.\"");
        int max = getEnchantMaxCoins();
    double rnd = Math.random();
    int cns = -max + (int)(rnd*((max*2)+1)); if(cns >= 0) {
        out.print("Ohoh! You got "+cns);
        out.println(" coins!"); }
    else {
        out.print("You lost "+Math.abs(cns)); out.println(" coins!");
    }
    return cns; }
}
