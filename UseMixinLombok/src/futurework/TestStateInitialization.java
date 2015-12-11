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

interface X {
    /** provided **/
    //@Default(3)
    int val();
    X val(int val);
    /** generated **/
    static X of() { return new X() {
        int _val = 3;
        public int val() { return _val; }
        public X val(int val) { _val = val; return this; }
    };}
}

public class TestStateInitialization {
}

