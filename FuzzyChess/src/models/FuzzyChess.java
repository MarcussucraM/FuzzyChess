package models;


//NEED TO LOSE REFERENCE TO SELECTED PEICE AND ENEMY PEICE - GUI BREAKS

import ai.TeamController;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;


/* FuzzyChess
 * Author: Marcus Kruzel
 * Version: 1.0
 * Last Updated Date: 10/17/20
 * Contains main logic for game
 */
public class FuzzyChess {
	private TeamController[] players;
	private GameBoard board;

	private ChessPiece selectedPiece;
	private ChessPiece selectedEnemy;
	private List<BoardPosition> possibleMoves;
	private List<BoardPosition> possibleCaptures;


	private int turn;
	private int subturn;
	private boolean gameOver;

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
		resetSelectedPiece();
		updateBoard();
		turn = 0;
		subturn = 0;
		dice = new Random();
		dice.setSeed((long)Math.random() * 100000);
		devMode = false;
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

	public Corp getCurrentCorp() {
		return players[turn].getCorps().get(subturn);
	}
	public void updateBoard(){
		possibleCaptures = getCapturePositions(selectedPiece,peekNextPlayer());
		possibleMoves = getMovementPositions(selectedPiece);
		board.updateBoardColors(getCurrentCorp().getMemberPositions(), possibleMoves, possibleCaptures);
	}

	private boolean selectPiece(BoardPosition position) {
		selectedPiece = getCurrentCorp().getMemberAt(position);
		updateBoard();
		return selectedPiece != null;
	}
	public void resetSelectedPiece(){
		captureResult = "";
		selectedPiece = null;
		selectedEnemy = null;
		possibleMoves = new ArrayList<>();
		possibleCaptures = new ArrayList<>();
	}

	public boolean makeMove(BoardPosition position){
		if(selectedPiece == null)
			return selectPiece(position);

		if(possibleMoves.contains(position))
			return movePiece(selectedPiece.getPosition(),position);

		if(possibleCaptures.contains(position)){
			selectedEnemy = peekNextPlayer().getMemberAt(position);
			if(capturePiece(selectedEnemy))
				return movePiece(selectedPiece.getPosition(),position);
			return false;
		}
		resetSelectedPiece();
		updateBoard();
		return false;
	}

	private boolean movePiece(BoardPosition oldPosition, BoardPosition newPosition) {
		selectedPiece.setPosition(newPosition);
		board.updateBoardState(oldPosition, newPosition);
		return true;
	}


	private boolean capturePiece(ChessPiece enemy) {
		int[] neededRolls = selectedPiece.getRolls(enemy);
		lastRoll = Math.abs((dice.nextInt() % 6) + 1);

		if(selectedPiece.getid() == 'n' || selectedPiece.getid() == 'N') {
			if(!selectedPiece.getActions(selectedPiece.getPosition(),1).contains(enemy.getPosition())){
				System.out.println("Subtracting 1 from Knight Attack");
				lastRoll = Math.min(1,lastRoll - 1);
				diceOffset = true;
			}
		}

		if(devMode)
			lastRoll = 6;

		for(int x = 0; x < neededRolls.length; x++) {
			if(neededRolls[x] == lastRoll) {
				captureResult = "Won";
				peekNextPlayer().removeMember(enemy);
				players[turn].getCaptures().add(enemy);
				if(enemy.getid() == 'k' || enemy.getid() == 'K') {
					gameOver = true;
				}
				return true;
			}
		}
		captureResult = "Lost";
		return false;
	}


	private class Node{
		private int depth;
		private BoardPosition current;

		public Node(BoardPosition current,int depth){
			this.current = current;
			this.depth = depth;
		}

		public boolean equals(Object other){
			Node o = (Node) other;
			return o.current.equals(this.current);
		}
	}

	public List<BoardPosition> getMovementPositions(ChessPiece selectedPiece){
		if(selectedPiece == null) return new ArrayList<>();
		Queue<Node> frontier = new LinkedList<>(Arrays.asList(new Node(selectedPiece.getPosition(),0)));
		List<BoardPosition> explored = new ArrayList<>();

		while(frontier.size() > 0){
			Node current = frontier.remove();
			if(current.depth >= selectedPiece.getMoveCount()) break;
			selectedPiece.getActions(current.current,1)
					.stream()
					.filter(pos -> !explored.contains(pos))
					.filter(pos -> board.isInBounds(pos) && !board.isOccupied(pos))
					.forEach(pos -> {
						frontier.add(new Node(pos,current.depth + 1));
						explored.add(pos);
					});
		}
		explored.remove(selectedPiece.getPosition());
		return explored;
	}

	public List<BoardPosition> getCapturePositions(ChessPiece selectedPiece,TeamController enemy){
		if(selectedPiece == null) return new ArrayList<>();

		return selectedPiece.getActions(selectedPiece.getPosition(),selectedPiece.getCaptureDistance())
				.stream()
				.filter(pos -> enemy.getMemberPositions().contains(pos))
				.collect(Collectors.toList());
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
		board.updateBoardColors(getCurrentCorp().getMemberPositions(), new ArrayList<>(), new ArrayList<>());
		System.out.println("--End Turn");
	}

	public void endSubturn() {
		System.out.println("End Subturn");
		if(++subturn == 3)
			endTurn();
		else
			board.updateBoardColors(getCurrentCorp().getMemberPositions(), new ArrayList<>(), new ArrayList<>());
	}

	public ChessPiece getSelectedPiece() {
		return selectedPiece;
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

	public ChessPiece getSelectedEnemy(){
		return selectedEnemy;
	}
}
