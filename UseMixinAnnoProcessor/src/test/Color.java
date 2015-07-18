package test;

public class Color {
    int rep;
    public Color(int rep) { this.rep = rep; }
    public Color compose(Color that) { 
        return new Color(this.rep + that.rep); 
    }
}
