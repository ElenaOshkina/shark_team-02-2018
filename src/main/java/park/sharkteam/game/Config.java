package park.sharkteam.game;

public class Config {
    // Число линий
    public static final int LINES_NUM = 5;
    // Число линий
    public static final int PLAYERS_NUM = 2;

    // конец карты по оси X
    public static final long LEFT_MAP_EDGE = (long) 20;
    // позиция по оси X, при достижении которой метеориты удаляются
    public static final long RIGHT_MAP_EDGE = (long) 650;
    // позиция по оси X, на которой создаются метеориты
    public static final long CREATE_LINES_POSITION = (long) 700;

    // расстояние между линиями
    public static final long  LINES_DISTANCE = (long) 100;
    // позиция по оси X, на которой создаются снаряды
    public static final long CREATE_SHELL_POSITION = (long) 150;

    // начальная позиция игрока
    public static final long PLAYER_POSITION = (long) 0;
    public static final long PLAYER_HITBOX = (long) 60;
    public static final long SHELL_HITBOX = (long) 60;

    // габариты (для отправки данных на фронт)
    public static final long LINE_LENGTH = 80;
    public static final long INDENTATION = 100;

    // скорость движения
    public static final long METEOR_SPEED = (long) 15;
    public static final long SHELL_SPEED = (long) 15;

    // Для линии:
    public static final int SHELL_CODE = 3;
    public static final int HP_CODE = 2;
    public static final int METEOR_CODE = 1;

    public static final String METEOR_JSON_CODE = "meteor";
    public static final String HP_JSON_CODE = "hp_supply";
    public static final String SHELL_JSON_CODE = "shell_supply";
    // Начальные значения:
    public static final int START_HP_VALUE = 10;
    public static final int START_SHELLS_VALUE = 5;
    public static final int START_LINE = 2;
    // Коды действия игрока:
    public static final String UP_ACTION = "UP";
    public static final String DOWN_ACTION = "DOWN";
    public static final String FIRE_ACTION = "FIRE";

    // Максимальное число патронов на карте
    public static final int MAX_SHELLS_COUNT = 3;

}
