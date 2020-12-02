package engine;

import java.awt.event.*;

import javax.swing.SwingUtilities;

import ai.AiController;
import ai.PlayerController;
import ai.TeamController;
import gui.FuzzyChessDisplay;
import models.BoardPosition;
import models.FuzzyChess;

public class FuzzyChessEngine implements ActionListener{
	private FuzzyChess game;
	private FuzzyChessDisplay display;
	private boolean inAnimation;
	TeamController player = new PlayerController();
	TeamController ai = new AiController();
	
	public FuzzyChessEngine() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				game = new FuzzyChess(player,ai);
				display = new FuzzyChessDisplay();
				updateDisplay();
				registerControls();
				inAnimation = false;
			}
		});
	}
	
	//called by dice roll animation thread when finished
	public void callbackUpdate() {
		inAnimation = false;
		game.endSubturn();
		updateDisplay();
		game.resetSelectedPieces();
		
		//show win screen
		if(game.isGameOver()) {
			System.out.println("Game Over");
			display.displayWinScreen();
		}
	}
	
	public void updateDisplay() {
		//status
		display.getStatusPanel().setTurnText(game.getTurn());
		display.getStatusPanel().setMoveText(game.getSubTurn());
		//if turn = players - disable button - otherwise enable
		//board
		display.getGamePanel().updateBoard(game.getBoard().getBoardState(), game.getBoard().getBoardColors());
		//captures
		display.getCapturePanel1().update(game.getPlayers()[0].getCaptures());
		display.getCapturePanel2().update(game.getPlayers()[1].getCaptures());
		//attack
		char attackerID = game.getSelectedPiece() == null ? 'x' : game.getSelectedPiece().getid();
		char defenderID = game.getSelectedEnemyPiece() == null ? 'x' : game.getSelectedEnemyPiece().getid();
		display.getAttackPanel().update(attackerID, defenderID, game.getCaptureResult());
		//menu
		display.getDevModeMenuItem().setSelected(game.isDevMode());
		//endgame
		if(game.isGameOver()) {
			display.displayWinScreen();
		}
	}
	
	public void registerControls() {
		display.getAttackPanel().setCallBackRef(this);
		display.getGamePanel().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dealWithClick(e);
			}
			
			public void mouseMoved(MouseEvent e) {
				dealWithMouseMovement(e);
			}
		});
		
		display.getNewGameMenuItem().addActionListener(this);
		display.getDevModeMenuItem().addActionListener(this);
		display.getHowToPlayMenuItem().addActionListener(this);
		display.getStatusPanel().getEndTurnButton().addActionListener(this);
	}
	
	public void dealWithClick(MouseEvent e) {
		if(!inAnimation) { //if we're in an animation lock controls
			game.getCurrentPlayer().makeMove(this,BoardPosition.convert(e.getX(), e.getY()));
		}
	}
	
	public void dealWithMouseMovement(MouseEvent e) {
		display.getGamePanel().setToolTipText(BoardPosition.convert(e.getX(), e.getY()).toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == display.getNewGameMenuItem()) {
			game = new FuzzyChess(player,ai);
			display.reset();
			updateDisplay();
		}
		else if(e.getSource() == display.getHowToPlayMenuItem()) {
			//display pdf somehow - or create own rule page
		}
		else if(e.getSource() == display.getDevModeMenuItem()) {
			game.toggleDevMode(); //need to make this a checkbox
		}
		else if(e.getSource() == display.getStatusPanel().getEndTurnButton()) {
			if(!inAnimation) {
				game.endTurn();
				game.resetSelectedPieces();
				updateDisplay();
			}
		}
	}

	public void setInAnimation(boolean inAnimation) {
		this.inAnimation = inAnimation;
	}
	public boolean isInAnimation(){
		return inAnimation;
	}

	public FuzzyChess getGame(){
		return game;
	}

	public FuzzyChessDisplay getDisplay(){
		return display;
	}
}
