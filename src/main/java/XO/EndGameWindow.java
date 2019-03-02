package XO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class EndGameWindow extends JFrame {
    private static final int WIN_HEIGHT = 400; // высота окна
    private static final int WIN_WIDTH = 400; // ширина окна
    private static final int WIN_POS_X = 800; // начальная координата
    private static final int WIN_POS_Y = 300; // начальная координата

    private GameWindow gameWindow;

    private JTextField textMatchResult = new JTextField("Match result:");

    EndGameWindow (GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WIN_POS_X, WIN_POS_Y, WIN_WIDTH, WIN_HEIGHT);

        setTitle("Game Result");
        setLayout(new GridLayout (1,1));
        textMatchResult.setHorizontalAlignment(0);
        textMatchResult.setEditable(false);
        add(textMatchResult);

        setResizable(false);
        setVisible(false);
    }

    void gameResult (String result) {
        textMatchResult.setText(result);
        setVisible(true);
    }


}
