package services;

public interface Game {
    void startGame();
    boolean isPlayer1Turn();
    void setIsPlayer1Turn(boolean isPlayer1Turn);
    int[] getRoundPits();
    int getLargerPitPlayer1();
    int getLargerPitPlayer2();
    void runPlayer(int index);
    boolean isGameOver();
    int getStones();
}
