package XO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameWindow extends JFrame {

    //размеры окон и начальные координаты
    private static final int WIN_HEIGHT = 555; // высота окна
    private static final int WIN_WIDTH = 507; // ширина окна
    private static final int WIN_POS_X = 800; // начальная координата
    private static final int WIN_POS_Y = 300; // начальная координата

    //константы классов
    private static StartNewGameWindow startNewGameWindow;
    private static EndGameWindow endGameWindow;
    private static Map field;

    GameWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(WIN_POS_X, WIN_POS_Y, WIN_WIDTH, WIN_HEIGHT);        //размеры
        setTitle("XnO Game");
        setResizable(false);

        //стартовое окно и завершение игры
        startNewGameWindow = new StartNewGameWindow(this);
        endGameWindow = new EndGameWindow(this);

        //панель сетка
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));

        //-----------------------------
        //Кнопка старт игры
        JButton btnNewGame = new JButton("Start new game");
        //обработчик событий
        btnNewGame.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                startNewGameWindow.setVisible(true);
            }
        });
        // добавляем кнопку в панель
        bottomPanel.add(btnNewGame);
        //------------------------------

        //кнопка выход из игры
        JButton btnExit = new JButton("Exit game");
        // обработчик событий кнопки
        btnExit.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // добавляем кнопку в панель
        bottomPanel.add(btnExit);
        //--------------------------------

        // добавление поля в окно
        field = new Map();
        add(field, BorderLayout.CENTER);

        // добавление панели к окну
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    //старт игры будет происходить из дополнительного окна, а этот метод в основом окне, связываем два этих окна
    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength) {
        field.startNewGame(mode, fieldSizeX, fieldSizeY, winLength);
    }

    //геттер для GameWindow для передачи в Map
    public static EndGameWindow getEndGameWindow() {
        return endGameWindow;
    }


}
