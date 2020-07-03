package GameFramework;

import java.awt.event.KeyEvent;
import javax.swing.*;

public class Binds extends InputMap
{
    public Binds(JPanel object)
    {
        // InputMap stuff
        InputMap inputMap = object.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "MOVE_LEFT");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "MOVE_RIGHT");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "MOVE_UP");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "MOVE_DOWN");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "ENTER");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "ESCAPE");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false), "DEVTOOLS");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0, false), "ZOOM_IN");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0, false), "ZOOM_OUT");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "DEBUG");
    }
}