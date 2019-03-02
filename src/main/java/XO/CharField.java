package XO;

import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Scanner;


public class CharField {
    // Символы игроков и пустых клеток
    private static final char PLAYER_1_SYMBOL = 'X';
    private static final char PLAYER_2_SYMBOL = 'O';
    private static final char EMPTY_DOT = '·';

    // Размеры поля
    private int SIZE_X;
    private int SIZE_Y;
    private int SCORE_IN_LINE;     //сколько символов подряд нужно для выигрыша

    private final Random rand = new Random();
    private char[][] field;

    private int lastStepX; //координаты последних ходов, от 0
    private int lastStepY;

    private int stepNumber = 0; //номер хода

    CharField(Map map) {
        SIZE_X = map.fieldSizeX;
        SIZE_Y = map.fieldSizeY;
        SCORE_IN_LINE = map.winLength;
        field = setField(SIZE_Y, SIZE_X);
    }

    // метод установки поля (возвращает поле размером x*y, заполненное symbol
    private static char[][] setField(int y, int x) {
        char[][] field = new char[y][x];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = EMPTY_DOT;
            }
        }
        return field;
    }

    // метод для печати поля
    public static void printField(char[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    // метод для задания символа в поле
    private void setPoint(int x, int y, char symbol) {
        field[y][x] = symbol;
    }

    // метод для проверки правильности и возможности хода
    private boolean isCellValid(int x, int y) {
        //проверка вылета за границы при вводе
        if (!isInsideBorder(x, y)) {
            return false;
        }
        //Если ячейка уже занята или нет
        return !(field[y][x] == PLAYER_1_SYMBOL || field[y][x] == PLAYER_2_SYMBOL);
    }

    //Метод для хода игрока из окна программы
    void userMove(int x, int y, char playerSymbol) {
        if (isCellValid(x, y)) {
            stepNumber++;
            setPoint(x, y, playerSymbol);
            lastStepX = x;
            lastStepY = y;
        }
    }

    //метод для хода ИИ
    //реализована блокировка ходов игрока1
    void aiStep() {
        //Проверка, можно ли выиграть следующим ходом. Если можно - ИИ выигрывает
        if (tryToWinMove(PLAYER_2_SYMBOL))
            return;
        //Затем, если не выиграть следующим ходом, ИИ определяет необходимый ход, блокирующий игрока
        if (tryToBlockMove(PLAYER_2_SYMBOL, PLAYER_1_SYMBOL))
            return;
        //если необходимости блокирующего хода нет - то рандомный ход
        randomMove(PLAYER_2_SYMBOL);
    }

    void aiSimpleStep() {
        randomMove(PLAYER_2_SYMBOL);    //для возможного последующего развития простой ход ИИ запихнут в отдельный метод
    }

    //Проверка победы.
    boolean checkWin(int X, int Y) {
        /*Реализована следующим образом - по переменным X,Y начинаем идти влево, вправо и по диагонали до первых
        не совпадающих с X,Y ячейкой, либо до достижения нужного количества символов в ряду.
        Работает на любом количестве ячеек в поле
     */
        return (checkWinLine(1, X, Y) || checkWinLine(2, X, Y) ||
                checkWinLine(3, X, Y) || checkWinLine(4, X, Y));
    }

    //Дополнительный метод для определения по одной из линий (для упрощения метода checkWin)
    private boolean checkWinLine(int Direction, int X, int Y) {
        //Коды Direction 1 - горизонталь, 2 вертикаль, 3 - главная диагональ, 4 - вспомогательная диагональ
        char targetSymbol = field[Y][X];    //считываем целевой символ по которому будем искать
        int targetPositionX = X;    //переменные для прицела
        int targetPositionY = Y;
        int scoreInLine = 1;    // счетчик количества одинаковых символов в линии подряд
        //Идем по линии в зависимости от направления
        int positionStepY = 0, positionStepX = 0;
        switch (Direction) {     //для определения направления указываем прицелу куда перемещаться для поиска
            case 1: {
                positionStepX = 1;  //если горизонталь - то изменяем только Х
                break;
            }
            case 2: {
                positionStepY = 1;  //если вертикаль - то изменяем только Y
                break;
            }
            case 3: {
                positionStepX = 1;  //если главная диагональ - то X и Y
                positionStepY = 1;
                break;
            }
            case 4: {
                positionStepX = -1; //если вспомогательная диагональ - то X- Y+
                positionStepY = 1;
                break;
            }
        }
        //цикл поиска в прямом направлении
        while (isInsideBorder(targetPositionX + positionStepX, targetPositionY + positionStepY)) {
            //перемещаем прицел в зависимости от направления если нет вылета за границу поля
            if (targetSymbol == field[targetPositionY + positionStepY][targetPositionX + positionStepX]) {
                targetPositionY += positionStepY;
                targetPositionX += positionStepX;
                scoreInLine++;
            } else {
                break;
            }
        }
        //ставим прицел обратно на начало движения
        targetPositionX = X;
        targetPositionY = Y;
        //цикл поиска в обратном направлении
        while (isInsideBorder(targetPositionX - positionStepX, targetPositionY - positionStepY)) {
            if (targetSymbol == field[targetPositionY - positionStepY][targetPositionX - positionStepX]) {
                targetPositionY -= positionStepY;
                targetPositionX -= positionStepX;
                scoreInLine++;
            } else {
                break;
            }
        }
        // если в линии достаточное количество символов -> true, // если не достигли нужного количества -> false
        return (scoreInLine >= SCORE_IN_LINE);
    }

    //метод определения адреса ячейки внутри границы игрового поля
    private boolean isInsideBorder(int x, int y) {
        return !(x < 0 || x > field[0].length - 1 || y < 0 || y > field.length - 1);
    }

    //метод определения выигрыша игрока следующим ходом в зависимости от хода ИИ
    /*Метод принимает координаты возможного хода ИИ (X,Y) и просчитывает, может ли Игрок1 победить на следующий ход
    (true или false), и записывает в массив из 2х элементов координаты хода, которым Игрок1 может победить
    player1Symbol - игрок, чей ход
    player2Symbol -игрок*/
    private boolean winEstimate(int X, int Y, char playerSymbol, char playerBlockedSymbol, int[] needCoordinates) {     //здесь X,Y - возможный ход ИИ
        setPoint(X, Y, playerSymbol);   //ход игрока1
        //ищем, может ли playerBlocked выиграть следующим ходом
        for (int i = 0; i < field[0].length; i++) {
            for (int j = 0; j < field.length; j++) {
                if (isCellValid(i, j)) {
                    setPoint(i, j, playerBlockedSymbol);
                    if (checkWin(i, j)) {
                        setPoint(i, j, EMPTY_DOT);    //устанавливаем точку обратно
                        needCoordinates[0] = i;
                        needCoordinates[1] = j;
                        setPoint(X, Y, EMPTY_DOT);
                        return true;
                    }
                    setPoint(i, j, EMPTY_DOT);  //восстанавливаем точку на поле
                }
            }
        }
        setPoint(X, Y, EMPTY_DOT);
        return false;
    }

    //метод для победы следующим ходом или блокировки победы следующим ходом противника
    //player1Symbol - игрок, чей ход
    //player2Symbol - игрок, чью победу смотрим (при блокировке - противник, при победе AI - сам AI)
    private boolean tryToWinMove(char playerSymbol) {
        for (int X = 0; X < field[0].length; X++) {
            for (int Y = 0; Y < field.length; Y++) {
                if (isCellValid(X, Y)) {
                    setPoint(X, Y, playerSymbol);
                    if (checkWin(X, Y)) {
                        stepNumber++;
                        lastStepX = X;
                        lastStepY = Y;
                        return true;
                    }
                    else {
                        setPoint(X, Y, EMPTY_DOT);
                    }
                }
            }
        }
        return false;
    }

    //метод для победы следующим ходом или блокировки победы следующим ходом противника
    //player1Symbol - игрок, чей ход
    //player2Symbol - игрок, чью победу смотрим (при блокировке - противник, при победе AI - сам AI)
    private boolean tryToBlockMove(char player1Symbol, char player2Symbol) {
        int[] needCoordinates = new int[2];  //массив с переменными-координатами для хода  (X,Y)
        boolean CanWinByNextMove;
        for (int X = 0; X < field[0].length; X++) {
            for (int Y = 0; Y < field.length; Y++) {
                if (isCellValid(X, Y)) {
                    CanWinByNextMove = winEstimate(X, Y, player1Symbol, player2Symbol, needCoordinates);
                    if (CanWinByNextMove) {
                        stepNumber++;
                        setPoint(needCoordinates[0], needCoordinates[1], player1Symbol);
                        lastStepX = needCoordinates[0];
                        lastStepY = needCoordinates[1];
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void randomMove(char playerSymbol) {
        int x, y;
        do {
            x = rand.nextInt(SIZE_X);
            y = rand.nextInt(SIZE_Y);
        } while (!isCellValid(x, y));
        stepNumber++;
        setPoint(x, y, playerSymbol);
        lastStepX = x;
        lastStepY = y;
    }

    int getStepNumber() {
        return stepNumber;
    }

    int getLastStepX() {
        return lastStepX;
    }

    int getLastStepY() {
        return lastStepY;
    }

    int getSIZE_X() {
        return SIZE_X;
    }

    int getSIZE_Y() {
        return SIZE_Y;
    }

    char getChar(int x, int y) {
        return field[y][x];
    }

    char getPLAYER_1_SYMBOL() {
        return PLAYER_1_SYMBOL;
    }

    char getPLAYER_2_SYMBOL() {
        return PLAYER_2_SYMBOL;
    }
}































