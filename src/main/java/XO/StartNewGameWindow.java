package XO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StartNewGameWindow extends JFrame{

    // родительская переменная нового окна
    private final GameWindow gameWindow;

    // объявляем параметры окна
    private static final int WIN_HEIGHT = 230; // высота
    private static final int WIN_WIDTH = 350; // ширина
    private static final int MIN_WIN_LEN = 3; // минимальная выйграшная длина
    private static final int MIN_FIELD_SIZE = 3; // минимальный размер поля
    private static final int MAX_FIELD_SIZE = 10; // максимальный размер поля
    private static final int MAX_WIN_LEN = 10; // максимальная выигрышная длина
    private static final String STR_WIN_LEN = "Winning Length: "; // сообщение о победе
    private static final String STR_FIELD_SIZE = "Field Size: "; // сообщение о размере поля

    private JRadioButton jrbHumVsAi = new JRadioButton("Human vs. AI", true); // указываем что это кнопка
    // выбрана при старте
    private JRadioButton jrbHumVsAiSimple = new JRadioButton("Human vs. AI simple");
    private JRadioButton jrbHumVsHum = new JRadioButton("Human vs. Human");
    // радио кнопки нужно добавить в группу, чтобы можно было выбрать только 1
    private ButtonGroup gameMode = new ButtonGroup();

    // создаем слайдер
    private JSlider slFieldSize;
    private JSlider slWinLength;

    // создаем окошко в центре нашего первого окна(только пустой конструктор)
    StartNewGameWindow(GameWindow gameWindow) {
        //  даем параметры первого окна
        this.gameWindow = gameWindow;
        // задаем размеры
        setSize(WIN_WIDTH, WIN_HEIGHT);
        // вычисляем центр поля с помощью класса прямоугольник Rectangle
        Rectangle gameWindowBounds = gameWindow.getBounds();
        // центр стороны первого окна - половина стороны от второго окна, чтобы установить окно поцентру
        int posX = (int)gameWindowBounds.getCenterX() - WIN_WIDTH / 2;
        int posY = (int)gameWindowBounds.getCenterY() - WIN_HEIGHT / 2;
        // координаты
        setLocation(posX, posY);
        // заголовок
        setTitle("New game parameters");

        //  задаем 10 строчек и 1 колонка
        setLayout(new GridLayout(11, 1));

        //  настроечный контрол
        addGameControlsMode();
        //  слайдеры
        addGameControlsFieldWinLenght();

        JButton btnStartGame = new JButton("Start a game");
        btnStartGame.addActionListener(new ActionListener() {
            //@Override
            public void actionPerformed(ActionEvent e) {
                btnStartGameClick();
            }
        });
        add(btnStartGame);
    }

    private void btnStartGameClick() {
        int gameMode;
        if(jrbHumVsAi.isSelected())
            gameMode = Map.MODE_HUMAN_VS_AI;
        else if(jrbHumVsHum.isSelected())
            gameMode = Map.MODE_HUMAN_VS_HUMAN;
        else if(jrbHumVsAiSimple.isSelected())
            gameMode = Map.MODE_HUMAN_VS_AI_SIMPLE;
        else
            throw new RuntimeException("No buttons selected");

        int fieldSize = slFieldSize.getValue();
        int winLength = slWinLength.getValue();
        gameWindow.startNewGame(gameMode, fieldSize, fieldSize, winLength);
        setVisible(false);
    }

    // создаем контрол
    private void addGameControlsMode() {
        add(new JLabel("Choose gaming mode:"));
        // добавляем радио кнопки и заголовок
        gameMode.add(jrbHumVsAi);
        gameMode.add(jrbHumVsHum);
        gameMode.add(jrbHumVsAiSimple);
        add(jrbHumVsHum);
        add(jrbHumVsAi);
        add(jrbHumVsAiSimple);
    }

    // создаем слайдер
    private void addGameControlsFieldWinLenght() {
        // лайбел будет менять поэтому размер + число
        add(new JLabel("Choose field size:"));
        final JLabel lblFieldSize = new JLabel(STR_FIELD_SIZE + MIN_FIELD_SIZE);
        add(lblFieldSize);


        slFieldSize = new JSlider(MIN_FIELD_SIZE, MAX_FIELD_SIZE, MIN_FIELD_SIZE);
        // вешаем слушателя
        slFieldSize.addChangeListener(new ChangeListener() {
            //@Override
            public void stateChanged(ChangeEvent e) {
                // это потом, берем текущее значение
                int currentFieldSize = slFieldSize.getValue();
                // меняем значение на лейбле
                lblFieldSize.setText(STR_FIELD_SIZE + currentFieldSize);
                // меняем значение максимальной выйграшной длины
                // выйграшная длина не может быть больше размеров поля
                slWinLength.setMaximum(currentFieldSize);
            }
        });
        // добавляем слайдер
        add(slFieldSize);

        add(new JLabel("Choose winning length:"));
        final JLabel lblWinLen = new JLabel(STR_WIN_LEN + MIN_WIN_LEN);
        add(lblWinLen);

        // заполняем слайдер (минимальное значение, максимальное и текущее)
        slWinLength = new JSlider(MIN_WIN_LEN, MAX_WIN_LEN, MIN_WIN_LEN);
        // вешаем слушателя
        slWinLength.addChangeListener(new ChangeListener() {
            //@Override
            public void stateChanged(ChangeEvent e) {
                // отображем изменение нашего слайдера
                lblWinLen.setText(STR_WIN_LEN + slWinLength.getValue());
            }
        });
        // добавляем слайдер
        add(slWinLength);
    }

}
