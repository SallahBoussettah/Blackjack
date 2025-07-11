import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private BlackJackModel model;
    private BlackJackUI view;
    
    public BlackJack() {
        model = new BlackJackModel();
        view = new BlackJackUI(model);
    }
}
