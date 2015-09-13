package gameori;
public class DoorsRoom {
    private TDoor leftDoor;
    private TDoor rightDoor;
    private TDoor frontDoor; /* Constructor */
    public DoorsRoom(TDoor l, TDoor r,
            TDoor f) {
        leftDoor = l;
        rightDoor = r;
        frontDoor = f;
}
    /* Getters */
    public TDoor getLeftDoor()
    {
        return leftDoor;
    }
    public TDoor getRightDoor()
    {
        return rightDoor;
    }
    public TDoor getFrontDoor()
    {
        return frontDoor;
    }
}
