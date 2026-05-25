package model;
import java.awt.Color;

public class NormalWord extends Word {
    public NormalWord(String text, int x) {
        super(text, x, 2, Color.CYAN); 
    }

    @Override
    public int onTypeSuccess() {
        return 10; 
    }
}
