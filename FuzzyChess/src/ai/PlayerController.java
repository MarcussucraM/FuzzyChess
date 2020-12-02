package ai;

import engine.FuzzyChessEngine;
import gui.FuzzyChessDisplay;
import models.BoardPosition;
import models.FuzzyChess;

public class PlayerController extends TeamController{
    @Override
    public void makeMove(FuzzyChessEngine engine, BoardPosition move) {
            boolean moveMade = false;
            FuzzyChess game = engine.getGame();
            FuzzyChessDisplay display = engine.getDisplay();

            if(game.getSelectedPiece() == null) {
                game.selectPiece(move);
            }
            else{
                moveMade = game.makeMove(move);
                //did we select an enemy? - if so - show animation
                if(game.getSelectedEnemyPiece() != null) {
                    display.getAttackPanel().update(game.getSelectedPiece().getid(), game.getSelectedEnemyPiece().getid(), game.getCaptureResult());
                    display.getAttackPanel().rollDice(game.getLastRoll());
                    engine.setInAnimation(true);
                    return;
                }
                if(moveMade) {
                    game.endSubturn();
                }
                engine.updateDisplay();
                game.resetSelectedPieces();

                //show win screen
                if(game.isGameOver()) {
                    System.out.println("Game Over");
                    display.displayWinScreen();
                }
                return;
            }
            engine.updateDisplay();
    }
}
