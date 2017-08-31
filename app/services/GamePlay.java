package services;

import utils.Constants;

import javax.inject.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GamePlay implements Game {

    private int[] roundPits;
    private int largerPitPlayer1;
    private int largerPitPlayer2;

    private boolean isPlayer1Turn;

    private List<Integer> player1Indexes;
    private List<Integer> player2Indexes;

    public GamePlay() {
        player1Indexes = new ArrayList<>();
        player1Indexes.add(0);
        player1Indexes.add(1);
        player1Indexes.add(2);
        player1Indexes.add(3);
        player1Indexes.add(4);
        player1Indexes.add(5);

        player2Indexes = new ArrayList<>();
        player2Indexes.add(6);
        player2Indexes.add(7);
        player2Indexes.add(8);
        player2Indexes.add(9);
        player2Indexes.add(10);
        player2Indexes.add(11);
    }

    @Override
    public void startGame() {
        /*  Index of the round pits in the Array
        *   5 | 4 | 3 | 2 |  1 |  0     -> Player 1
        *   6 | 7 | 8 | 9 | 10 | 11     -> Player 2
        * */
        roundPits = new int[Constants.MAX_PITS];
        for (int i = 0; i < Constants.MAX_PITS; i++) {
            roundPits[i] = Constants.ROUND_PIT_INITIAL_STONES;
        }

        largerPitPlayer1 = Constants.LARGER_PIT_INITIAL_STONES;
        largerPitPlayer2 = Constants.LARGER_PIT_INITIAL_STONES;

        isPlayer1Turn = true;
    }

    @Override
    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    @Override
    public void setIsPlayer1Turn(boolean isPlayer1Turn) {
        this.isPlayer1Turn = isPlayer1Turn;
    }

    @Override
    public int[] getRoundPits() {
        return roundPits;
    }

    @Override
    public int getLargerPitPlayer1() {
        return largerPitPlayer1;
    }

    @Override
    public int getLargerPitPlayer2() {
        return largerPitPlayer2;
    }

    @Override
    public void runPlayer(int index) {
        int stones = roundPits[index];
        if (stones > 0) {
            roundPits[index] = 0;
            run(index, stones);
        }
    }

    @Override
    public boolean isGameOver() {
        boolean isGameOver = false;
        if (roundPits[0] == 0 && roundPits[1] == 0 && roundPits[2] == 0 &&
                roundPits[3] == 0 && roundPits[4] == 0 && roundPits[5] == 0) {
            isGameOver = true;
        } else if (roundPits[6] == 0 && roundPits[7] == 0 && roundPits[8] == 0 &&
                roundPits[9] == 0 && roundPits[10] == 0 && roundPits[11] == 0) {
            isGameOver = true;
        }
        if (isGameOver) {
            gameOver();
        }
        return isGameOver;
    }

    @Override
    public int getStones() {
        int total = 0;
        for (int i = 0; i < 12; i++) {
            total = total + roundPits[i];
        }
        total = total + largerPitPlayer1 + largerPitPlayer2;
        return total;
    }

    private void gameOver() {
        largerPitPlayer1 = largerPitPlayer1 + roundPits[5] + roundPits[4] + roundPits[3] +
                roundPits[2] + roundPits[1] + roundPits[0];
        largerPitPlayer2 = largerPitPlayer2 + +roundPits[6] + roundPits[7] + roundPits[8] +
                roundPits[9] + roundPits[10] + roundPits[11];
        for (int i = 0; i < 12; i++) {
            roundPits[i] = 0;
        }
    }

    private void switchPlayerTurn() {
        isPlayer1Turn = isPlayer1Turn ? false : true;
    }

    private void run(int startIndex, int stones) {
        int nextPit = startIndex + 1;
        do {
            if (nextPit == 6) {
                if (isPlayer1Turn) {
                    largerPitPlayer1 = largerPitPlayer1 + 1;
                    stones--;
                }
                if (stones > 0) {
                    roundPits[6] = roundPits[6] + 1;
                    stones--;
                    nextPit++;
                    if (stones == 0) {
                        checkCaptureStones(nextPit - 1);
                        switchPlayerTurn();
                    }
                }
            } else if (nextPit == 12) {
                if (!isPlayer1Turn) {
                    largerPitPlayer2 = largerPitPlayer2 + 1;
                    stones--;
                }
                if (stones > 0) {
                    roundPits[0] = roundPits[0] + 1;
                    stones--;
                    nextPit = 1;
                    if (stones == 0) {
                        checkCaptureStones(nextPit - 1);
                        switchPlayerTurn();
                    }
                }
            } else {
                roundPits[nextPit] = roundPits[nextPit] + 1;
                stones--;
                nextPit++;
                if (stones == 0) {
                    checkCaptureStones(nextPit - 1);
                    switchPlayerTurn();
                }
            }
        } while (stones > 0);
    }

    private void checkCaptureStones(int lastPitIndex) {
        if (roundPits[lastPitIndex] == 1) {
            if (isPlayer1Turn && player1Indexes.contains(lastPitIndex)) {
                switch (lastPitIndex) {
                    case 0:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[11];
                        roundPits[11] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 1:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[10];
                        roundPits[10] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 2:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[9];
                        roundPits[9] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 3:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[8];
                        roundPits[8] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 4:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[7];
                        roundPits[7] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 5:
                        largerPitPlayer1 = largerPitPlayer1 + roundPits[lastPitIndex] + roundPits[6];
                        roundPits[6] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    default:
                        break;
                }
            } else if (!isPlayer1Turn && player2Indexes.contains(lastPitIndex)) {
                switch (lastPitIndex) {
                    case 6:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[5];
                        roundPits[5] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 7:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[4];
                        roundPits[4] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 8:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[3];
                        roundPits[3] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 9:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[2];
                        roundPits[2] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 10:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[1];
                        roundPits[1] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    case 11:
                        largerPitPlayer2 = largerPitPlayer2 + roundPits[lastPitIndex] + roundPits[0];
                        roundPits[0] = 0;
                        roundPits[lastPitIndex] = 0;
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
