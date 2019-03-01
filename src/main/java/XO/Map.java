package XO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Map extends JPanel {

    public static final int MODE_H_V_A = 0;
    public static final int MODE_H_V_H = 1;

    // 24.1 чтобы заполнить поле
    int[][] field;
    int fieldSizeX;
    int fieldSizeY;
    int winLength;
    // 25 высота и ширина каждой ячейки
    int cellHeight;
    int cellWidth;
    // переменные для хода игрока/компьютера
    int cellX;
    int cellY;
    //окно завершения игры (ссылка прилетает из GameWindow)
    private static EndGameWindow endGameWindow;

    // 27 если ничего не нарисовано
    boolean isInitialized = false;
    // если игра закончена
    boolean isEnded = false;

    //добавляем символьный объект для поля игры
    CharField gameField;
    // 10 создаем конструктор и задаем цвет поля
    Map() {
        setBackground(Color.orange);
        //окно завершения игры
        endGameWindow = GameWindow.getEndGameWindow();

        // 30 создаем слушателя шелчка мышки
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                update(e);
                int step = gameField.getStepNumber();
                    gameField.userMove(cellX, cellY);
                if (step != gameField.getStepNumber()) {   //после хода игрока номер хода должен увеличиться - иначе
                    // пусть щелкает мышкой дальше
                    //Если ход игрока победный
                    if (gameField.checkWin(gameField.getLastStepX(), gameField.getLastStepY())) {
                        endGameWindow.gameResult("Player WIN!");
                        isEnded = true;
                    }
                    //Если ход игрока выходит на ничью (последний ход)
                    if ((gameField.getStepNumber() == fieldSizeX * fieldSizeY) && !isEnded) {
                        endGameWindow.gameResult("DRAW!");
                        isEnded = true;
                    }
                    //Если игра не закончена - ход компьютера
                    if (!isEnded) {
                        gameField.aiStep();
                    }
                    //Аналогичная проверка на победу и ничью
                    if ((gameField.checkWin(gameField.getLastStepX(), gameField.getLastStepY())) && !isEnded) {
                        endGameWindow.gameResult("Win Skynet!");
                        isEnded = true;
                    }
                    if ((gameField.getStepNumber() == fieldSizeX * fieldSizeY) && !isEnded) {   // проверка свободных клеток
                        endGameWindow.gameResult("DRAW");
                        isEnded = true;
                    }
                }
            }
        });
    }
    
    // 31 создаем метод который определяет куда чекнули
    void update(MouseEvent e) {
        // пиксели делим на ширину и высоту
        cellX = e.getX() / cellWidth;
        cellY = e.getY() / cellHeight;
        // после каждого действия перерисовываем
        repaint();
    }

    // 24 метод для рисования нашего поля в целом
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
        if (isInitialized){
            drawField(g, gameField);
        }
    }

    // 11 создаем метод который говорит о типе игры, размеры поля, и выиграшная длина
    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength){
       //25 запоняем поля при старте новой игры
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        gameField = new CharField(this);

        isInitialized = true;   //инициализируем старт игры
        // 28 говорим панели перерисоваться
        repaint();
    }

    // 24.1 метод для рисования
    void render(Graphics g) {
        if(!isInitialized) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // узнаем кол-во ячеек
        cellHeight = panelHeight / fieldSizeY;
        cellWidth = panelWidth / fieldSizeX;

        // 26 отрисовываем по Y (горизонтальные полоски)
        for(int i = 0; i < fieldSizeY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }

        // 29 отрисовываем по X (вертикальные полоски)
        for(int i = 0; i < fieldSizeX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, panelHeight);
        }
    }

    // 32 Метод для нарисования креста в ячейке с центром в X, Y
    void drawX(Graphics g, int cellX, int cellY) {
        if (!isInitialized) return;
        if (gameField.getStepNumber()==0) return;  // Если ходов не сделано - то не рисуем
        int x1, x2, y1, y2; //координаты для рисования
        int borderStep = 10;
        x1 = cellWidth * cellX + borderStep;
        x2 = cellWidth * (cellX +1) - borderStep;
        y1 = cellHeight * cellY + borderStep;
        y2 = cellHeight * (cellY +1) - borderStep;
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y1, x1, y2);
    }

    // 33 Метод для нарисования круга в ячейке с центром в X, Y
    void drawO(Graphics g, int cellX, int cellY) {
        if (!isInitialized) return;
        if (gameField.getStepNumber()==0) return;  // Если ходов не сделано - то не рисуем
        int x, x2, y, y2; //координаты для рисования
        int borderStep = 10;
        x = cellWidth * cellX + borderStep/2;
        y = cellHeight * cellY + borderStep/2;
        g.drawOval(x, y, cellWidth - borderStep, cellHeight - borderStep);
    }

    //Метод для нарисования всего поля по данным из символьного поля
    void drawField(Graphics g, CharField gameField) {
        //проходим по массиву CharField и рисуем крестики и нолики
        for (int Y = 0; Y < gameField.getSIZE_Y(); Y++) {
            for (int X = 0; X < gameField.getSIZE_X(); X++) {
                if (gameField.getChar(X, Y) == 'X') {
                    drawX(g, X, Y);
                }
                if (gameField.getChar(X, Y) == 'O') {
                    drawO(g, X, Y);
                }
            }
        }
    }

}