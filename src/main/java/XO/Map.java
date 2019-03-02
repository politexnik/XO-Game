package XO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//Поле игры
public class Map extends JPanel {

    static final int MODE_HUMAN_VS_AI = 0;     //Human vs AI
    static final int MODE_HUMAN_VS_HUMAN = 1;     //Human vs Human
    static final int MODE_HUMAN_VS_AI_SIMPLE = 2;     //Human vs AI simple
    private static int modeSelected = 0;    //выбранный режим
    //Заполнение поля
    int fieldSizeX;
    int fieldSizeY;
    int winLength;
    // высота и ширина каждой ячейки
    private int cellHeight;
    private int cellWidth;
    // переменные для хода игрока/компьютера
    private int cellX;
    private int cellY;
    //окно завершения игры (ссылка прилетает из GameWindow)
    private static EndGameWindow endGameWindow;

    // если ничего не нарисовано
    private boolean isInitialized = false;
    // если игра закончена
    private boolean isEnded = false;

    //символьный объект для поля игры, в котором прописан движок игры
    private CharField gameField;
    //создаем конструктор и задаем цвет поля
    Map() {
        setBackground(Color.orange);
        //окно завершения игры
        endGameWindow = GameWindow.getEndGameWindow();

        // слушатель для клика по полю
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                update(e);
                if (modeSelected == 0 || modeSelected == 2){ //human_vs_AI or HUMAN_VS_AI_SIMPLE
                    fieldClickedAIMode();
                } else if (modeSelected == 1) { //MODE_HUMAN_VS_HUMAN
                    fieldClickedHumanMode();
                }
            }
        });
    }
    
    // создаем метод который определяет куда чекнули
    private void update(MouseEvent e) {
        // пиксели делим на ширину и высоту
        cellX = e.getX() / cellWidth;
        cellY = e.getY() / cellHeight;
        // после каждого действия перерисовываем
        repaint();
    }

    // метод для рисования нашего поля в целом
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
        if (isInitialized){
            drawField(g, gameField);
        }
    }

    // метод который говорит о типе игры, размеры поля, и выиграшной длине
    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength){
       //заполняем поля при старте новой игры
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        modeSelected = mode;
        gameField = new CharField(this);

        isInitialized = true;   //инициализируем старт игры
        // 28 говорим панели перерисоваться
        repaint();
    }

    // метод для рисования
    private void render(Graphics g) {
        if(!isInitialized) return;

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        // узнаем кол-во ячеек
        cellHeight = panelHeight / fieldSizeY;
        cellWidth = panelWidth / fieldSizeX;

        // горизонтальные полоски
        for(int i = 0; i < fieldSizeY; i++) {
            int y = i * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }

        // вертикальные полоски
        for(int i = 0; i < fieldSizeX; i++) {
            int x = i * cellWidth;
            g.drawLine(x, 0, x, panelHeight);
        }
    }

    // Метод для нарисования креста в ячейке с центром в X, Y
    private void drawX(Graphics g, int cellX, int cellY) {
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

    // Метод для нарисования круга в ячейке с центром в X, Y
    private void drawO(Graphics g, int cellX, int cellY) {
        if (!isInitialized) return;
        if (gameField.getStepNumber()==0) return;  // Если ходов не сделано - то не рисуем
        int x, y; //координаты для рисования
        int borderStep = 10;
        x = cellWidth * cellX + borderStep/2;
        y = cellHeight * cellY + borderStep/2;
        g.drawOval(x, y, cellWidth - borderStep, cellHeight - borderStep);
    }

    //Метод для нарисования всего поля по данным из символьного поля
    private void drawField(Graphics g, CharField gameField) {
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

    private void fieldClickedAIMode(){
        int step = gameField.getStepNumber();
        gameField.userMove(cellX, cellY, gameField.getPLAYER_1_SYMBOL());
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
                if (modeSelected == 0)
                    gameField.aiStep();
                else if (modeSelected == 2)
                    gameField.aiSimpleStep();
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

    private void fieldClickedHumanMode(){
        int step = gameField.getStepNumber();
        //выбираем проставляемый символ в зависимости от четности хода
        char playerSymbol = (step % 2 == 0) ? gameField.getPLAYER_1_SYMBOL(): gameField.getPLAYER_2_SYMBOL();
        gameField.userMove(cellX, cellY, playerSymbol);
        if (step != gameField.getStepNumber()) {   //после хода игрока номер хода должен увеличиться - иначе
            // пусть щелкает мышкой дальше
            //Если ход игрока победный
            if (gameField.checkWin(gameField.getLastStepX(), gameField.getLastStepY())) {

                endGameWindow.gameResult("Player " + playerSymbol + " WIN!");
                isEnded = true;
            }
            //Если ход игрока выходит на ничью (последний ход)
            if ((gameField.getStepNumber() == fieldSizeX * fieldSizeY) && !isEnded) {
                endGameWindow.gameResult("DRAW!");
                isEnded = true;
            }
        }
    }

}