package model;
import java.awt.Color;

public class BonusWord  extends Word{
    public BonusWord(String text, int x) {
        super(text, x, 4, Color.YELLOW); 
    }

    @Override
    public int onTypeSuccess() {
        return 30; 
    }
}
