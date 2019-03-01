package XO;

import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Scanner;

/*
    Автор: Алексеев Я.О.
    08.05.2018
 */
public class CharField {
    // Символы игроков и пустых клеток
    private final char PLAYER_1_SYMBOL = 'X';
    private final char PLAYER_2_SYMBOL = 'O';
    private final char EMPTY_DOT = '·';
    // Размеры поля
    private final Map map;

    private int SIZE_X;
    private int SIZE_Y;
    private int SCORE_IN_LINE;     //сколько символов подряд нужно для выигрыша

    private Scanner scanner = new Scanner(System.in);
    private final Random rand = new Random();
    private char[][] field;

    private int lastStepX; //координаты последних ходов, от 0
    private int lastStepY;

    private int stepNumber = 0; //номер хода

    public CharField(Map map) {
        this.map = map;
        SIZE_X = map.fieldSizeX;
        SIZE_Y = map.fieldSizeY;
        SCORE_IN_LINE = map.winLength;
        field = setField(SIZE_X, SIZE_Y, EMPTY_DOT);
    }

    // метод установки поля (возвращает поле размером x*y, заполненное symbol
    public static char[][] setField(int y, int x, char symbol) {
        char[][] field = new char[y][x];
        for (int i = 0; i <field.length ; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = symbol;
            }
        }
        return field;
    }

    // метод для печати поля
    public static void printField(char[][] field) {
        for (int i = 0; i <field.length ; i++) {
            for (int j = 0; j < field[0].length; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printField() {  //перегруженный метод для печати поля экземпляра
        printField(field);
    }

    // метод для задания символа в поле
    public void setPoint(int x, int y, char symbol) {
        field[y][x] = symbol;
    }

    // метод для проверки правильности и возможности хода
    public boolean isCellValid(int x, int y) {
        //дополнил код проверкой вылета за границы при вводе, иначе при вводе сверх меры кидает исключение
        if (!isInsideBorder(x,y)) {
            return false;
        }
        if (field[y][x] == PLAYER_1_SYMBOL || field[y][x] == PLAYER_2_SYMBOL){
            return false;
        }
        return true;
    }

    // метод для хода Игрока из консоли (не используется)
    private void userMove() {
        int x;
        int y;
        do {
            System.out.println("Ваш ход! Введите Х, У");
            x = scanner.nextInt()-1;
            y = scanner.nextInt()-1;
        } while (!isCellValid(x,y));

        setPoint(x,y, PLAYER_1_SYMBOL);
        //После хода устанавливаем последние значения хода
        lastStepX = x;
        lastStepY = y;
    }

    //Метод для хода игрока из окна программы
    public void userMove(int x, int y) {
        if (isCellValid(x,y)) {
            stepNumber++;
            setPoint(x,y, PLAYER_1_SYMBOL);
            lastStepX = x;
            lastStepY = y;
        }
    }

    //метод для хода ИИ
    //реализована блокировка ходов игрока1
    public void aiStep() {
        int x;
        int y;
        int[] needCoordinates = new int[2];  //массив с переменными-координатами для блокировки хода игрока (X,Y)
        boolean flagIfPlayer1CanWinByNextMove = false;  //флаг, говорящий, может ли ИИ выиграть следующим ходом
        //Сначала ИИ определяет необходимый ход, блокирующий игрока
        Mark1:
        for (int X = 0; X < field[0].length; X++) {
            for (int Y = 0; Y < field.length ; Y++) {
                if (isCellValid(X,Y)) {
                    flagIfPlayer1CanWinByNextMove = winEstimate(X, Y, needCoordinates);
                    if (flagIfPlayer1CanWinByNextMove) {
                        stepNumber++;
                        setPoint(needCoordinates[0],needCoordinates[1],PLAYER_2_SYMBOL);
                        lastStepX = needCoordinates[0];
                        lastStepY = needCoordinates[1];
                        break Mark1;    //Метка близко, думаю, такое ее название допускается
                    }
                }
            }
        }
        if (!flagIfPlayer1CanWinByNextMove) {   //если необходимости блокирующего хода нет - то рандом
            do {
                x = rand.nextInt(SIZE_X);
                y = rand.nextInt(SIZE_Y);
            } while (!isCellValid(x,y));
            stepNumber++;
            setPoint(x,y,PLAYER_2_SYMBOL);
            lastStepX = x;
            lastStepY = y;
        }
    }

    //Проверка победы.
    public boolean checkWin(int X, int Y) {
        /*Реализована следующим образом - по переменным X,Y начинаем идти влево, вправо и по диагонали до первых
        не совпадающих с X,Y ячейкой, либо до достижения нужного количества символов в ряду.
        Работает на любом количестве ячеек в поле
     */
        return (checkWinLine(1, X,Y) || checkWinLine(2, X,Y) ||
                checkWinLine(3, X,Y) || checkWinLine(4, X,Y) );
    }

    //Дополнительный метод для определения по одной из линий (для упрощения метода checkWin)
    private boolean checkWinLine(int Direction,int X, int Y) {
        //Коды Direction 1 - горизонталь, 2 вертикаль, 3 - главная диагональ, 4 - вспомогательная диагональ
        char targetSymbol = field[Y][X];    //считываем целевой символ по которому будем искать
        int targetPositionX = X;    //переменные для прицела
        int targetPositionY = Y;
        int scoreInLine = 1;    // счетчик количества одинаковых символов в линии подряд
        //Идем по линии в зависимости от направления
        int positionStepY = 0, positionStepX = 0;
        switch (Direction){     //для определения направления указываем прицелу куда перемещаться для поиска
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
        while  (isInsideBorder(targetPositionX + positionStepX, targetPositionY + positionStepY))  {
            //перемещаем прицел в зависимости от направления если нет вылета за границу поля
            if (targetSymbol == field[targetPositionY + positionStepY][targetPositionX + positionStepX] ) {
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
            if  (targetSymbol == field[targetPositionY - positionStepY][targetPositionX - positionStepX]) {
                targetPositionY -= positionStepY;
                targetPositionX -= positionStepX;
                scoreInLine++;
            } else {
                break;
            }
        }

        if (scoreInLine >= SCORE_IN_LINE) { // если в линии достаточное количество символов
            return true;
        }
        return false;   // если не достигли нужного количества
    }

    //доп метод определения адреса ячейки внутри границы игрового поля
    private boolean isInsideBorder (int x, int y) {
        if (x<0 || x >field[0].length-1 || y<0 || y> field.length-1) {
            return false;
        }
        return true;
    }

    //метод определения потенциала (вероятности) выигрыша игрока в зависимости от хода ИИ
    /*Метод принимает координаты возможного хода ИИ (X,Y) и просчитывает, может ли Игрок1 победить на следующий ход
    (true или false), и записывает в массив из 2х элементов коорднаты хода, которым Игрок1 может победить */
    private boolean winEstimate (int X, int Y, int[] needCoordinates) {     //здесь X,Y - возможный ход ИИ
        setPoint(X, Y, PLAYER_2_SYMBOL);
        for (int i = 0; i < field[0].length; i++) {
            for (int j = 0; j < field.length; j++) {
                if (isCellValid(i,j)) {
                    setPoint(i,j,PLAYER_1_SYMBOL);
                    if (checkWin(i, j)) {
                        setPoint(i, j, EMPTY_DOT);    //устанавливаем точку обратно
                        needCoordinates[0] = i;
                        needCoordinates[1] = j;
                        setPoint(X, Y, EMPTY_DOT);
                        return true;   //потенциально можно превратить в byte и оценивать эффективность хода соперника
                                        // но за 1 вечер уже не успеть насочинять такую штуку :)
                    }
                    setPoint(i, j, EMPTY_DOT);  //восстанавливаем точку на поле
                }
            }
        }
        setPoint(X, Y, EMPTY_DOT);
        return false;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public int getLastStepX() {
        return lastStepX;
    }

    public int getLastStepY() {
        return lastStepY;
    }

    public int getSIZE_X() {
        return SIZE_X;
    }

    public int getSIZE_Y() {
        return SIZE_Y;
    }

    public char getChar(int x, int y) {
        return field[y][x];
    }

}































