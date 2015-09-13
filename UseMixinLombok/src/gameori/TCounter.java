package gameori;
import java.lang.Math;
import static java.lang.System.out;
/* Provides a counter that after
a limit releases coins */public interface TCounter {
    /* State references */
    public int getCounter();
    public void setCounter(int c);
    public int getLimit();
    /* Coin management */
    public int getCounterMaxCoins();
    default void incrementCounter() {
        setCounter(getCounter() + 1);
    }
    default void decrementCounter() {
        setCounter(getCounter() - 1);
    }
    default boolean hasReachedLimit() {
        return getCounter() >= getLimit();
    }
    default int releaseCoins() {
        double rnd = Math.random();
        int cns = (int) (rnd * getCounterMaxCoins()) + 1;
        out.println("You got " + cns + " coins.");
        return cns;
    }
}
