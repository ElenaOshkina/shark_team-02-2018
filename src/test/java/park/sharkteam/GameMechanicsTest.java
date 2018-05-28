package park.sharkteam;

import org.junit.Test;
import park.sharkteam.game.Config;
import park.sharkteam.game.Game;
import park.sharkteam.game.gameentities.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class GameMechanicsTest {

    public int getMeteorsNum(Line line, int playerNum){
        int meteors = 0;
        for(int i = 0; i < Config.LINES_NUM; i++ ){
            if(line.getObject(playerNum, i) == Config.METEOR_CODE) {
                meteors++;
            }
        }
        return meteors;
    }

    @Test
    public void creatingLineTest(){
        Game game = new Game();
        int curPlayerIndex = 0;

        //игра только создана - создана одна линия
        assertEquals(game.getLines().size(), 1);
        Line line = game.getLines().get(0);

        assertEquals( (long) line.getPosition(), Config.CREATE_LINES_POSITION);

        //Линии чередуются - у одного игрока с метеорами, у другого - без них
        for(int i = 0; i < Config.PLAYERS_NUM; i++){
            if (curPlayerIndex == i) {
                assertTrue(getMeteorsNum(line, i) > 0);
            } else {
                assertEquals(getMeteorsNum(line, 1), 0);
            }
        }

        long creationLineDistance = Config.CREATE_LINES_POSITION - Config.RIGHT_MAP_EDGE + Config.LINES_DISTANCE;
        game.updateForTest(creationLineDistance / Config.METEOR_SPEED + 1);
        //Cоздана новая линия метеоритов
        assertEquals(game.getLines().size(), 2);
        line = game.getLines().get(1);
        //Линии чередуются - у одного игрока с метеорами, у другого - без них
        for(int i = 0; i < Config.PLAYERS_NUM; i++){
            if (curPlayerIndex != i) {
                assertTrue(getMeteorsNum(line, i) > 0);
            } else {
                assertEquals(getMeteorsNum(line, i), 0);
            }
        }
    }

    @Test
    public void actionTest(){
        Game game = new Game();
        int curPlayerIndex = 0;
        Player player = game.getPlayer(0);

        int prevLine = player.getLine();

        //двигаем вверх до упора
        while( player.getLine() != Config.LINES_NUM - 1 ){
            game.moveUser(curPlayerIndex, Config.UP_ACTION);
        }
        game.moveUser(curPlayerIndex, Config.UP_ACTION);
        assertEquals(player.getLine(), Config.LINES_NUM - 1);

        //двигаем вниз до упора
        while( player.getLine() != 0 ){
            game.moveUser(curPlayerIndex, Config.DOWN_ACTION);
        }
        game.moveUser(curPlayerIndex, Config.DOWN_ACTION);
        assertEquals(player.getLine(), 0);

        // Превысим число возможных на карте патронов
        player.updateShells(Config.MAX_SHELLS_COUNT + 1);

        for(int i = 0; i < Config.MAX_SHELLS_COUNT; i++){
            game.shoot(curPlayerIndex);
        }
        //пытаемся выстрелить еще раз, превысив лимит допустимого числа патронов на карте
        game.shoot(curPlayerIndex);

        assertEquals(game.getShells().size(), Config.MAX_SHELLS_COUNT);
        assertEquals(player.getShells(), Config.START_SHELLS_VALUE + 1);

        for (Shell shell : game.getShells()){
            assertEquals( shell.getLine(),  player.getLine());
        }
    }

    @Test
    public void collisionTestWithMeteor() {
        Game game = new Game();
        int curPlayerIndex = 0;
        Line line = game.getLines().get(0);

        int meteorIndex = 0;

        for (int i = 0; i < Config.LINES_NUM; i++) {
            if (line.getObject(curPlayerIndex, i) == Config.METEOR_CODE) {
                meteorIndex = i;
            }
        }

        Player player = game.getPlayer(curPlayerIndex);

        while (player.getLine() != meteorIndex) {
            if (player.getLine() < meteorIndex) {
                game.moveUser(0, Config.UP_ACTION);
            }
            else {
                game.moveUser(0, Config.DOWN_ACTION);
            }
        }
        long distanceToFirstLine = Config.CREATE_LINES_POSITION - Config.PLAYER_POSITION - Config.PLAYER_HITBOX;
        long timeBeforeCollision = distanceToFirstLine / Config.METEOR_SPEED + 1;

        int healthPointsBeforeCollision = player.getHealthPoints();
        game.updateForTest(timeBeforeCollision);

        assertTrue(healthPointsBeforeCollision > player.getHealthPoints());
    }

    @Test
    public void getCollisionWithShellSupply(){
        Game game = new Game();
        int curPlayerIndex = 0;
        Line line = game.getLines().get(0);
        Player player = game.getPlayer(curPlayerIndex);

        //Ставим припасы на пути игрока
        line.replaceObject(curPlayerIndex, Config.START_LINE, Config.SHELL_CODE);

        long distanceToFirstLine = Config.CREATE_LINES_POSITION - Config.PLAYER_POSITION - Config.PLAYER_HITBOX;
        long timeBeforeCollision = distanceToFirstLine / Config.METEOR_SPEED + 1;

        int shellsBeforeCollision = player.getShells();
        game.updateForTest(timeBeforeCollision);
        assertEquals(shellsBeforeCollision + 1, player.getShells());
    }

    @Test
    public void getCollisionWithHPSupply(){
        Game game = new Game();
        int curPlayerIndex = 0;
        Line line = game.getLines().get(0);
        Player player = game.getPlayer(curPlayerIndex);

        //Ставим припасы на пути игрока
        line.replaceObject(curPlayerIndex, Config.START_LINE, Config.HP_CODE);

        long distanceToFirstLine = Config.CREATE_LINES_POSITION - Config.PLAYER_POSITION - Config.PLAYER_HITBOX;
        long timeBeforeCollision = distanceToFirstLine / Config.METEOR_SPEED + 1;

        int hpBeforeCollision = player.getHealthPoints();
        game.updateForTest(timeBeforeCollision);
        assertEquals( hpBeforeCollision + 1, (int) player.getHealthPoints());
    }

    @Test
    public void movingMeteorsTest(){
        Game game = new Game();
        int curPlayerIndex = 0;
        int enemyPlayerIndex = 1;
        Line line = game.getLines().get(0);
        Player player = game.getPlayer(curPlayerIndex);

        int meteorIndex = -1;

        for(int i = 0; i < Config.LINES_NUM; i++) {
            if(line.getObject(curPlayerIndex, i) == Config.METEOR_CODE ){
                meteorIndex = i;
            }
        }

        while (player.getLine() != meteorIndex) {
            if (player.getLine() < meteorIndex) {
                game.moveUser(0, Config.UP_ACTION);
            }
            else {
                game.moveUser(0, Config.DOWN_ACTION);
            }
        }

        game.shoot(curPlayerIndex);
        assertEquals(player.getShells(), Config.START_SHELLS_VALUE - 1);

        long distanceShellMeteor = Config.CREATE_LINES_POSITION - Config.CREATE_SHELL_POSITION - Config.SHELL_HITBOX;
        long timeBeforeShellMEteorCollision = distanceShellMeteor / (Config.METEOR_SPEED + Config.SHELL_SPEED) + 1;

        // Снаряд сталкивается с метеоритом и выпихивает его на дорожку другого игрока
        assertEquals(line.getObject(curPlayerIndex, meteorIndex), Config.METEOR_CODE);
        assertEquals(line.getObject(enemyPlayerIndex, meteorIndex), 0);
        game.updateForTest(timeBeforeShellMEteorCollision);
        assertEquals(line.getObject(enemyPlayerIndex, meteorIndex), Config.METEOR_CODE);
    }

    @Test
    public void scenarioGameTest(){
        //первый игрок выстреливает в меторит - второй в него врезается.
        //второй игрок врезается в еще один метеорит, игра окончена.
        Game game = new Game();
        int player1Idx = 0, player2Idx = 1;
        Line line = game.getLines().get(0);
        Player player_1 = game.getPlayer(player1Idx),
                player_2 = game.getPlayer(player2Idx);

        int meteorIndex = 0;

        for (int i = 0; i < Config.LINES_NUM; i++) {
            if (line.getObject(player1Idx, i) == Config.METEOR_CODE) {
                meteorIndex = i;
            }
        }

        // подводим обоих игроков под метеорит
        while (player_1.getLine() != meteorIndex) {
            if (player_1.getLine() < meteorIndex) {
                game.moveUser(player1Idx, Config.UP_ACTION);
            } else {
                game.moveUser(player1Idx, Config.DOWN_ACTION);
            }
        }

        while (player_2.getLine() != meteorIndex) {
            if (player_2.getLine() < meteorIndex) {
                game.moveUser(player2Idx, Config.UP_ACTION);
            } else {
                game.moveUser(player2Idx, Config.DOWN_ACTION);
            }
        }
        player_1.updateHealthPoints(2 - Config.START_HP_VALUE);
        player_2.updateHealthPoints(2 - Config.START_HP_VALUE);
        assertEquals(player_2.getHealthPoints(), 2);

        //стреляем по первому метеориту
        game.shoot(player1Idx);

        //теперь первый игрок движется по чистому пути, второй -  должен будет встреться c метеоритoм

        long distanceShellMeteor = Config.CREATE_LINES_POSITION - Config.CREATE_SHELL_POSITION - Config.SHELL_HITBOX;
        long timeBeforeShellMeteorCollision = distanceShellMeteor / (Config.METEOR_SPEED + Config.SHELL_SPEED) + 1;

        // Снаряд сталкивается с метеоритом и выталкивает его на дорожку другого игрока
        game.updateForTest(timeBeforeShellMeteorCollision);
        assertEquals(line.getObject(player2Idx, meteorIndex), Config.METEOR_CODE);
        assertEquals(line.getObject(player1Idx, meteorIndex), 0);

        // тем временем создается вторая линия
        assertEquals(game.getLines().size(), 2);


        //Сталкиваем игрока со первой линией
        long distanceToCollisionFirstLine = line.getPosition() - Config.PLAYER_HITBOX ;
        long timeBeforeCollisionFirstLine = (distanceToCollisionFirstLine / Config.METEOR_SPEED) + 1;
        game.updateForTest(timeBeforeCollisionFirstLine);

        // у первого игрока - путь чист, а у второго - на пути метеорит
        assertEquals(player_1.getHealthPoints(), 2);
        assertEquals(player_2.getHealthPoints(), 1);

        // теперь мы должны дождаться удаления линии, с которой мы столкнули второго игрока
        long distanceToDestroyFirstLine = line.getPosition() - Config.LEFT_MAP_EDGE ;
        long timeBeforeDestroyingFirstLine = (distanceToDestroyFirstLine / Config.METEOR_SPEED) + 1;
        int prevLinesNum = game.getLines().size();
        game.updateForTest(timeBeforeDestroyingFirstLine);


        assertTrue(
                (prevLinesNum > game.getLines().size()) ||
                        (prevLinesNum == game.getLines().size() && game.getLines().get(prevLinesNum - 1).getPosition() == Config.CREATE_LINES_POSITION)
        );

        // получаем следующую линию - с ней столкнется игрок 2
        line = game.getLines().get(0);

        for (int i = 0; i < Config.LINES_NUM; i++) {
            if (line.getObject(player2Idx, i) == Config.METEOR_CODE) {
                meteorIndex = i;
            }
        }

        //перемещаем игрока 2 под метеорит
        while (player_2.getLine() != meteorIndex) {
            if (player_2.getLine() < meteorIndex) {
                game.moveUser(player2Idx, Config.UP_ACTION);
            } else {
                game.moveUser(player2Idx, Config.DOWN_ACTION);
            }
        }
        long distanceToSecondLine = line.getPosition() - Config.PLAYER_HITBOX;
        long timeBeforeSecondCollision = (distanceToSecondLine / Config.METEOR_SPEED ) + 1;

        game.updateForTest(timeBeforeSecondCollision);
        //второй игрок столкнулся - игра окончена
        assertTrue(game.isFinished());
        assertEquals(player_2.getHealthPoints(), 0);
    }
}