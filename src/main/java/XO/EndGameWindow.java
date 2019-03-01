package XO;

import javax.swing.*;
import java.awt.*;

public class EndGameWindow extends JFrame {
    private static final int WIN_HEIGHT = 400; // высота окна
    private static final int WIN_WIDTH = 400; // ширина окна
    private static final int WIN_POS_X = 800; // начальная координата
    private static final int WIN_POS_Y = 300; // начальная координата

    GameWindow gameWindow;

    private JTextField textMatchResult = new JTextField("Match result:");

    EndGameWindow (GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        // 4 при закрытии окна прекрашение работы программы
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 6 устанавливаем размеры
        setBounds(WIN_POS_X, WIN_POS_Y, WIN_WIDTH, WIN_HEIGHT);

        // 2 задаем заголовок
        setTitle("Game Result");
        setLayout(new GridLayout (1,1));
        textMatchResult.setHorizontalAlignment(0);
        add(textMatchResult);

        // 7 запрещаем изменение размеров окна
        setResizable(false);
        setVisible(false);
    }

    public void gameResult (String result) {
        textMatchResult.setText(result);
        setVisible(true);
    }


}
