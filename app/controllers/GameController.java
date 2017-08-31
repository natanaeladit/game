package controllers;

import models.GameState;
import play.mvc.Controller;
import play.mvc.Result;
import services.Game;
import utils.Constants;
import views.html.game;
import views.html.gameindex;
import views.html.gamemain;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameController extends Controller {

    private final Game gameSvc;
    private final GameState gameState;

    private void updateGameState() {
        gameState.isGameOver = gameSvc.isGameOver();
        gameState.isPlayer1Turn = gameSvc.isPlayer1Turn();
        gameState.largerPitPlayer1 = gameSvc.getLargerPitPlayer1();
        gameState.largerPitPlayer2 = gameSvc.getLargerPitPlayer2();
        gameState.roundPits = gameSvc.getRoundPits();
    }

    private boolean isIndexValid(int index) {
        boolean isValid = false;
        // TODO validate player's turn
        if (index >= 0 && index <= 11 && !gameState.isGameOver) {
            isValid = true;
        }
        return isValid;
    }

    @Inject
    public GameController(Game game) {
        this.gameSvc = game;
        gameState = new GameState();
    }

    public Result index() {
        return ok(gameindex.render(Constants.BOL_COM_GAME));
    }

    public Result startGame() {
        if (request().queryString().containsKey(Constants.FIRST_TURN)) {
            String[] queryStrings = request().queryString().get(Constants.FIRST_TURN);
            gameSvc.startGame();
            if (queryStrings[0].equalsIgnoreCase(Constants.PLAYER_1)) {
                gameSvc.setIsPlayer1Turn(true);
            } else if (queryStrings[0].equalsIgnoreCase(Constants.PLAYER_2)) {
                gameSvc.setIsPlayer1Turn(false);
            }
            updateGameState();
        }
        return ok(gamemain.render(Constants.BOL_COM_GAME, game.render(gameState)));
    }

    public Result run(int index) {
        if (isIndexValid(index)) {
            gameSvc.runPlayer(index);
            updateGameState();
        }
        return ok(gamemain.render(Constants.BOL_COM_GAME, game.render(gameState)));
    }
}
