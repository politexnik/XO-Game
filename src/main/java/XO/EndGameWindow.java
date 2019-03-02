package XO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndGameWindow extends JFrame {
    private static final int WIN_HEIGHT = 400; // высота окна
    private static final int WIN_WIDTH = 400; // ширина окна
    private static final int WIN_POS_X = 800; // начальная координата
    private static final int WIN_POS_Y = 300; // начальная координата

    GameWindow gameWindow;

    private JTextField textMatchResult = new JTextField("Match result:");
    //Заготовка под рестарт игры.
    //private JButton restartGameButton = new JButton("Restart");
    //private JPanel bottomPanel = new JPanel(new FlowLayout());

    EndGameWindow (GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WIN_POS_X, WIN_POS_Y, WIN_WIDTH, WIN_HEIGHT);

        setTitle("Game Result");
        setLayout(new GridLayout (1,1));
        textMatchResult.setHorizontalAlignment(0);
        textMatchResult.setEditable(false);
        add(textMatchResult);
//        restartGameButton.addActionListener(new ActionListener() {
//            //@Override
//            public void actionPerformed(ActionEvent e) {
//                GameWindow.startNewGameWindow.setVisible(true);
//                EndGameWindow.this.setVisible(false);
//            }
//        });

//        bottomPanel.add(restartGameButton);
//        add(bottomPanel);

        setResizable(false);
        setVisible(false);
    }

    public void gameResult (String result) {
        textMatchResult.setText(result);
        setVisible(true);
    }


}
