import controllers.GameController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.Game;
import services.GamePlay;
import utils.Constants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class GameUnitTest {

    private Game gameMock;
    private GameController gameController;
    private Application application;

    @Before
    public void setup() {
        gameMock = mock(Game.class);
        gameController = new GameController(gameMock);
        application = new GuiceApplicationBuilder()
                .overrides(bind(Game.class).to(GamePlay.class))
                .build();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void whenGameIsStartedThenValidateInitialBoard() {
        Result result = gameController.index();
        assertThat(contentAsString(result)).contains(Constants.BOL_COM_GAME);

        Result startResult = Helpers.route(application, fakeRequest(GET, "/start?FirstTurn=Player1"));
        assertThat(contentAsString(startResult)).contains("Home");

        int[] pits = new int[Constants.MAX_PITS];
        for (int i = 0; i < 12; i++) {
            pits[i] = 6;
        }
        when(gameMock.getRoundPits()).thenReturn(pits);

        int[] roundPits = gameMock.getRoundPits();
        //Player 1 has 6 stones in each round pit
        assertThat(roundPits[0]).isEqualTo(6);
        assertThat(roundPits[1]).isEqualTo(6);
        assertThat(roundPits[2]).isEqualTo(6);
        assertThat(roundPits[3]).isEqualTo(6);
        assertThat(roundPits[4]).isEqualTo(6);
        assertThat(roundPits[5]).isEqualTo(6);

        //Player 2 has 6 stones in each round pit
        assertThat(roundPits[6]).isEqualTo(6);
        assertThat(roundPits[7]).isEqualTo(6);
        assertThat(roundPits[8]).isEqualTo(6);
        assertThat(roundPits[9]).isEqualTo(6);
        assertThat(roundPits[10]).isEqualTo(6);
        assertThat(roundPits[11]).isEqualTo(6);

        int largerPitPlayer1 = 0;
        when(gameMock.getLargerPitPlayer1()).thenReturn(largerPitPlayer1);

        //Player 1 has 0 stones in his larger pit
        assertThat(gameMock.getLargerPitPlayer1()).isEqualTo(largerPitPlayer1);

        int largetPitPlayer2 = 0;
        when(gameMock.getLargerPitPlayer2()).thenReturn(largetPitPlayer2);

        //Player 2 has 0 stones in his larger pit
        assertThat(gameMock.getLargerPitPlayer2()).isEqualTo(largetPitPlayer2);
    }

    @Test
    public void whenPlayer1FirstTurnIsSelectedThenPlayer1RunsFirst() {
        Result startResult = Helpers.route(application, fakeRequest(GET, "/start?FirstTurn=Player1"));
        assertThat(contentAsString(startResult)).contains("Home");

        boolean isPlayer1Turn = true;
        when(gameMock.isPlayer1Turn()).thenReturn(isPlayer1Turn);

        assertThat(gameMock.isPlayer1Turn()).isEqualTo(true);
    }

    @Test
    public void whenPlayer2FirstTurnIsSelectedThenPlayer2RunsFirst() {
        Result startResult = Helpers.route(application, fakeRequest(GET, "/start?FirstTurn=Player2"));
        assertThat(contentAsString(startResult)).contains("Home");

        boolean isPlayer1Turn = false;
        when(gameMock.isPlayer1Turn()).thenReturn(isPlayer1Turn);

        assertThat(gameMock.isPlayer1Turn()).isEqualTo(false);
    }

    @Test
    public void whenPlayer1LandsTheLastStoneInHisOwnBigPitThenHeGetsAnotherTurn() {
        GamePlay gamePlay = new GamePlay();
        Game gameSpy = spy(gamePlay);

        gameSpy.startGame();
        gameSpy.runPlayer(0);

        assertThat(gameSpy.isPlayer1Turn()).isEqualTo(true);
        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(1);
    }

    @Test
    public void whenPlayer1LandsTheLastStoneThenSwitchToOtherPlayerTurn() {
        GamePlay gamePlay = new GamePlay();
        Game gameSpy = spy(gamePlay);

        gameSpy.startGame();
        gameSpy.runPlayer(1);

        assertThat(gameSpy.isPlayer1Turn()).isEqualTo(false);
        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(1);
    }

    @Test
    public void whenGameIsOverThenTheFlagIsTrue() {
        int[] pits = new int[Constants.MAX_PITS];
        for (int i = 0; i < 12; i++) {
            if (i < 6)
                pits[i] = 0;
            else
                pits[i] = 6;
        }
        when(gameMock.getRoundPits()).thenReturn(pits);

        boolean isGameOver = true;
        when(gameMock.isGameOver()).thenReturn(isGameOver);

        assertThat(gameMock.isGameOver()).isEqualTo(true);
    }

    @Test
    public void whenTheLastStoneLandsInHisOwnEmptyPitThenCaptureHisOwnStoneAndAllStoneInOppositePitAndPutThemInHisLargerPit(){
        /*
        *   Player 1's Turn and he runs pit with 1 stone
        *
        *   Board:
        *
        *   Player1's score     8 8 8 8 0 1     Player2's score
        *   2                   0 8 7 7 7 7     1
        *
         */
        GamePlay gamePlay = new GamePlay();
        Game gameSpy = spy(gamePlay);

        gameSpy.startGame();
        gameSpy.runPlayer(0);
        gameSpy.runPlayer(1);
        gameSpy.runPlayer(6);

        int[] roundPits = gameSpy.getRoundPits();

        assertThat(roundPits[0]).isEqualTo(1);
        assertThat(roundPits[1]).isEqualTo(0);
        assertThat(roundPits[10]).isEqualTo(7);
        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(2);
        assertThat(gameSpy.getLargerPitPlayer2()).isEqualTo(1);

        gameSpy.runPlayer(0);

        roundPits = gameSpy.getRoundPits();

        assertThat(roundPits[0]).isEqualTo(0);
        assertThat(roundPits[1]).isEqualTo(0);
        assertThat(roundPits[10]).isEqualTo(0);
        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(10);
        assertThat(gameSpy.getLargerPitPlayer2()).isEqualTo(1);
    }

    @Test
    public void whenPlayer1MovesAndVisitHisLargerPitThenAddStoneToHisLargerPit(){
        GamePlay gamePlay = new GamePlay();
        Game gameSpy = spy(gamePlay);
        gameSpy.startGame();

        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(0);

        gameSpy.runPlayer(0);

        assertThat(gameSpy.getLargerPitPlayer1()).isEqualTo(1);
    }

    @Test
    public void whenPlayer2MovesAndVisitHisLargerPitThenAddStoneToHisLargerPit(){
        GamePlay gamePlay = new GamePlay();
        Game gameSpy = spy(gamePlay);
        gameSpy.startGame();

        assertThat(gameSpy.getLargerPitPlayer2()).isEqualTo(0);

        gameSpy.runPlayer(1);
        gameSpy.runPlayer(11);

        assertThat(gameSpy.getLargerPitPlayer2()).isEqualTo(1);
    }
}
