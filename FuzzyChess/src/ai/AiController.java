package ai;

import engine.FuzzyChessEngine;
import gui.FuzzyChessDisplay;
import models.*;

import java.util.ArrayList;
import java.util.List;

public class AiController extends TeamController{
	private FuzzyChess game;
	private GameBoard board;


	@Override
	public void makeMove(FuzzyChessEngine engine, BoardPosition placeholder) {
		game = engine.getGame();
		board = game.getBoard();
		FuzzyChessDisplay display = engine.getDisplay();
		Corp current_corp = game.getCurrentCorp();
		ArrayList<Node> possible_moves = new ArrayList<>();

		for(ChessPiece member : current_corp.getMembers()){
			Node current = new Node(member,board);
			possible_moves.add(current);

			for(BoardPosition movement : board.getMovementPositions(member)){
				GameBoard copy = board.copy();
				copy.updateBoardState(member.getPosition(),movement);
				possible_moves.add(new Node(member,copy,movement,current));
			}
			for(BoardPosition attacks : board.getCapturePositions(member,game.peekNextPlayer().getCorps())){
				GameBoard copy = board.copy();
				copy.updateBoardState(member.getPosition(),attacks);

				for(Corp c: game.peekNextPlayer().getCorps()){
					if(c.getMemberAt(attacks) != null){
						possible_moves.add(new Node(member,copy,attacks,current,getAttackScore(member,c.getMemberAt(attacks))));
					}
				}
			}
		}

		Node best_node = null;
		for(Node move : possible_moves){
			move.score += getDefendingScore(move);
			move.score += getAttackedScore(move);
			move.score += getMoveScore(move);


			if(best_node == null || move.score > best_node.score){
				best_node = move;
			}else if(move.score == best_node.score){
				best_node = Math.random() >= .5 ? best_node : move;
			}
		}


		System.out.println("BEST NODE: " + best_node.score);
		if(best_node.parent != null){
			game.selectPiece(best_node.parent.position);
			game.makeMove(best_node.position);


			if(game.getSelectedEnemyPiece() != null) {
				display.getAttackPanel().update(game.getSelectedPiece().getid(), game.getSelectedEnemyPiece().getid(), game.getCaptureResult());
				display.getAttackPanel().rollDice(game.getLastRoll());
				engine.setInAnimation(true);
				return;
			}
			game.endSubturn();
		}else{
			System.out.println("NOT MAKING MOVE");
			game.endSubturn();
		}

		engine.updateDisplay();
		game.resetSelectedPieces();

		if(game.isGameOver()) {
			System.out.println("Game Over");
			display.displayWinScreen();
		}
	}


	//Positive score that tries and get the pieces to move forward
	private double getMoveScore(Node move){
		if(Math.random() < .6 || move.parent == null) return 0;
		return Math.random() * move.position.distance(move.parent.position) * (1/move.current_piece.getImportance());
	}

	private double getAttackedScore(Node move){
		List<BoardPosition> enemy_capture_positions = new ArrayList<BoardPosition>();
		for(Corp corps : game.peekNextPlayer().getCorps()){
			for(ChessPiece enemy : corps.getMembers()){
				if(move.game_state.getCapturePositions(enemy,this.getCorps()).contains(move.position)){
					return Math.random() * (-2 * move.current_piece.getImportance());
				}
			}
		}
		return 0;
	}

	private double getDefendingScore(Node move){
		//Check all enemies that can attack allies
		//Check if I can attack current position
		List<Node> friendly_deaths = new ArrayList<>();

		for(Corp corp : game.peekNextPlayer().getCorps()){
			for(ChessPiece enemy : corp.getMembers()){
				for(BoardPosition friendly_death : move.game_state.getCapturePositions(enemy,this.getCorps())){
					GameBoard copy = board.copy();
					copy.updateBoardState(enemy.getPosition(),friendly_death);
					friendly_deaths.add(new Node(move.current_piece,copy,move.position,move));
				}
			}
		}

		return (Math.random() * friendly_deaths.size());
	}
	private double getAttackScore(ChessPiece item,ChessPiece enemy){
		if(Math.random() < .5) return 0;
		return Math.random() * (item.getRolls(enemy).length / 6);
	}






	private class Node{
		private Node parent;
		private GameBoard game_state;
		private ChessPiece current_piece;
		private BoardPosition position;
		private double score = 0;

		public Node(ChessPiece current_piece,GameBoard game_state){
			this.current_piece = current_piece;
			this.parent = null;
			this.position = current_piece.getPosition();
			this.game_state = game_state;
		}
		public Node(ChessPiece current_piece,GameBoard game_state,BoardPosition position,Node parent){
			this.current_piece = current_piece;
			this.parent = parent;
			this.position = position;
			this.game_state = game_state;
		}
		public Node(ChessPiece current_piece,GameBoard game_state,BoardPosition position,Node parent,double score){
			this.current_piece = current_piece;
			this.parent = parent;
			this.position = position;
			this.game_state = game_state;
			this.score = score;
		}

		@Override
		public String toString(){
			return position.toString();
		}
	}
}
