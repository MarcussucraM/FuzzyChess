package models;


//NEED TO LOSE REFERENCE TO SELECTED PEICE AND ENEMY PEICE - GUI BREAKS

import ai.TeamController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


/* FuzzyChess
 * Author: Marcus Kruzel
 * Version: 1.0
 * Last Updated Date: 10/17/20
 * Contains main logic for game
 */
public class FuzzyChess {
	private TeamController[] players;
	private Corp currentEnemyCorp;
	private GameBoard board;
	private int turn;
	private int subturn;
	private boolean gameOver;

	private ChessPiece selectedPiece;
	private ChessPiece selectedEnemy;
	private ArrayList<BoardPosition> possibleMoves;
	private ArrayList<BoardPosition> possibleCaptures;

	//use for capture panel display
	private Random dice;
	private boolean diceOffset;
	private int lastRoll;
	private String captureResult;
	
	//if enabled - all rolls = 6
	private boolean devMode;

	public FuzzyChess(TeamController player1,TeamController player2) {
		players = new TeamController[]{player1,player2};
		gameOver = false;
		board = new GameBoard();
		initCorps();
		currentEnemyCorp = null;
		turn = 0;
		subturn = 0;
		selectedPiece = null;
		selectedEnemy = null;
		dice = new Random();
		dice.setSeed((long)Math.random() * 100000);
		captureResult = "";
		devMode = false;
		board.updateBoardColors(getCurrentCorp().getMemberPositions(), null, null);
	}

	//corps grab pieces from the board
	//this is ugly code too - but w/e
	private void initCorps() {
		char[][] gameBoard = board.getBoardState();
		Corp p1_king_corp = new Corp(null);
		Corp p2_king_corp = new Corp(null);
		Corp p1_lbishop_corp = new Corp(p1_king_corp);
		Corp p2_lbishop_corp = new Corp(p2_king_corp);
		Corp p1_rbishop_corp = new Corp(p1_king_corp);
		Corp p2_rbishop_corp = new Corp(p2_king_corp);

		for (int i = 0; i < gameBoard.length; i++) {
			for (int j = 0; j < gameBoard[0].length; j++) {
				char id = gameBoard[i][j];
				BoardPosition curPosition = new BoardPosition(j, i);
				
				// kings corp
				if (j >= 3 && j < 5) {
					if (id == 'p') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_king_corp.addMember(piece);
					} else if (id == 'P') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_king_corp.addMember(piece);
					}
				}
				if (id == 'k' || id == 'q' || id == 'r') {
					ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
					p2_king_corp.addMember(piece);
				}
				if (id == 'K' || id == 'Q' || id == 'R') {
					ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
					p1_king_corp.addMember(piece);
				}
				
				// left bishop corp
				if (j < 3) {
					if (id == 'p' || id == 'b' || id == 'n') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_lbishop_corp.addMember(piece);
					}
					if (id == 'P' || id == 'B' || id == 'N') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_lbishop_corp.addMember(piece);
					}
				}

				// right bishop corp
				if (j >= 5) {
					if (id == 'p' || id == 'b' || id == 'n') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.DOWN);
						p2_rbishop_corp.addMember(piece);
					} else if (id == 'P' || id == 'B' || id == 'N') {
						ChessPiece piece = new ChessPiece(curPosition, id, ChessPiece.UP);
						p1_rbishop_corp.addMember(piece);
					}
				}
			}
		}
		players[0].getCorps().add(p1_king_corp);
		players[0].getCorps().add(p1_lbishop_corp);
		players[0].getCorps().add(p1_rbishop_corp);
		players[1].getCorps().add(p2_king_corp);
		players[1].getCorps().add(p2_lbishop_corp);
		players[1].getCorps().add(p2_rbishop_corp);
	}
	
	/* as of now - corps go in order
	 * king - lbishop - rbishop */
	public Corp getCurrentCorp() {
		return players[turn].getCorps().get(subturn);
	}

	/*select for movement/capturing - update board colors when selected
	* when a piece is selected its possible moves and possible captures
	* are obtained for later when making a move*/
	public boolean selectPiece(BoardPosition selectedPosition) {
		if(board.isInBounds(selectedPosition)) {
			selectedPiece = getCurrentCorp().getMemberAt(selectedPosition);			
			if(selectedPiece != null) {
				possibleMoves = board.getMovementPositions(selectedPiece);
				possibleCaptures = board.getCapturePositions(selectedPiece,peekNextPlayer().getCorps());
				board.updateBoardColors(getCurrentCorp().getMemberPositions(), possibleMoves, possibleCaptures);
				return true;
			}
		}
		return false;
	}
	
	
	/*gain reference to selectedEnemy and its corp so we can remove it if
	 * successful roll for capture -- maybe get rid of --*/
	private boolean selectEnemyPiece(BoardPosition selectedPosition) {
		ArrayList<Corp> enemyCorps = peekNextPlayer().getCorps();
		for(Corp enemyCorp : enemyCorps) {
			//if corp is not active then its remaining pieces are in the kings corp..
			if(!enemyCorp.isActive()) continue;
			currentEnemyCorp = enemyCorp;
			selectedEnemy = enemyCorp.getMemberAt(selectedPosition);
			if(selectedEnemy != null) return true;
		}
		return false;
	}
	
	public boolean makeMove(BoardPosition newPosition) {
		// check to make sure there is a selected piece
		boolean moveMade = false;
		if(selectedPiece != null) {
			BoardPosition oldPosition = new BoardPosition(selectedPiece.getPosition().getX(), selectedPiece.getPosition().getY());
			if(possibleMoves.contains(newPosition)) {
				movePiece(oldPosition, newPosition);
				moveMade = true;
			}
			if(possibleCaptures.contains(newPosition)) {
				selectEnemyPiece(newPosition);
				if(capturePiece()) {
					movePiece(oldPosition, newPosition);
				}
				moveMade = true;
			}
		}
		board.updateBoardColors(getCurrentCorp().getMemberPositions(), null, null);
		return moveMade;
	}
	
	//resets the selected pieces in the game
	//call if makeMove returns false
	public void resetSelectedPieces() {
		captureResult = "";
		selectedPiece = null;
		selectedEnemy = null;
	}

	private void movePiece(BoardPosition oldPosition, BoardPosition newPosition) {
		selectedPiece.setPosition(newPosition);
		board.updateBoardState(oldPosition, newPosition);
	}	
	
	//update to add roll offset for knight
	private boolean capturePiece() {
		int[] neededRolls = selectedPiece.getRolls(selectedEnemy);
		lastRoll = Math.abs((dice.nextInt() % 6) + 1);
		
		//if its a knight - and the enemy position is not adjacent - subtract 1 from dice roll
		if(selectedPiece.getid() == 'n' || selectedPiece.getid() == 'N') {
			if(!selectedPiece.getActions().contains(selectedEnemy.getPosition())){
				System.out.println("Subtracting 1 from Knight Attack");
				diceOffset = true;
				if(lastRoll != 1) //cant get less than 1
					lastRoll -= 1;
			}
		}
		
		if(devMode)
			lastRoll = 6;
		
		for(int x = 0; x < neededRolls.length; x++) {
			if(neededRolls[x] == lastRoll) {
				captureResult = "Won";
				currentEnemyCorp.removeMember(selectedEnemy);
				players[turn].getCaptures().add(selectedEnemy);
				if(selectedEnemy.getid() == 'k' || selectedEnemy.getid() == 'K') {
					gameOver = true;
				}
				return true;
			}				
		}
		captureResult = "Lost";
		return false;
	}
	
	public void quitGame() {
		gameOver = true;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void endTurn() {
		turn = ++turn % 2;
		subturn = 0;		
		board.updateBoardColors(getCurrentCorp().getMemberPositions(), null, null);
		System.out.println("--End Turn");
	}

	public void endSubturn() {
		System.out.println("End Subturn");
		if(++subturn == 3)
			endTurn();
		else 
			board.updateBoardColors(getCurrentCorp().getMemberPositions(), null, null);
	}
	
	public ChessPiece getSelectedPiece() {
		return selectedPiece;
	}
	
	public ChessPiece getSelectedEnemyPiece() {
		return selectedEnemy;
	}

	public GameBoard getBoard() {
		return board;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}

	public int getTurn() {
		return turn;
	}

	public int getSubTurn() {
		return subturn;
	}
	
	public boolean isDiceOffset() {
		return diceOffset;
	}
	
	public void toggleDevMode() {
		devMode = !devMode;
	}
	
	public boolean isDevMode() {
		return devMode;
	}
	
	public String getCaptureResult() {
		return captureResult;
	}

	public TeamController[] getPlayers(){return players;}
	public TeamController getCurrentPlayer(){
		return players[getTurn()];
	}
	public TeamController peekNextPlayer(){
		return players[(turn + 1) % 2];
	}
}
