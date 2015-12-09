package futurework;

//BEGIN_STATE_INIT
interface Box { 
    default int val() { return 0; } //provided
    void val(int _val); //provided
    static Box of() { return new Box() { //generated
        int val = Box.super.val();
        public int val() { return val; }
        public void val(int _val) { val = _val; }
    };} }
//END_STATE_INIT

public class TestStateInitialization {
}
