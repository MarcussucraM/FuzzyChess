package models;

import java.util.*;

public class GameBoard{

	private char[][] boardState;
	private char[][] boardColors;

	public GameBoard(){
		resetBoardState();
		resetBoardColors();
	}

	public void updateBoardState(BoardPosition oldPosition, BoardPosition newPosition) {
		boardState[newPosition.getY()][newPosition.getX()] = boardState[oldPosition.getY()][oldPosition.getX()];
		boardState[oldPosition.getY()][oldPosition.getX()] = '-';
	}

	public void updateBoardColors(List<BoardPosition> activeCorps, List<BoardPosition> movements, List<BoardPosition> captures) {
		resetBoardColors();
		for(BoardPosition p : activeCorps)
			boardColors[p.getY()][p.getX()] = 'a';

		for(BoardPosition p : movements)
			boardColors[p.getY()][p.getX()] = 'm';

		for(BoardPosition p : captures)
			boardColors[p.getY()][p.getX()] = 'c';
	}
	
	public void resetBoardState(){
		 boardState = new char[][]{ {'r','n','b','q','k','b','n','r'},
				 					{'p','p','p','p','p','p','p','p'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'P','P','P','P','P','P','P','P'},
								    {'R','N','B','Q','K','B','N','R'}};
	}
	
	
	public void resetBoardColors() {
		boardColors = new char[][]{ {'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'}};
	}

	public GameBoard copy() {
		GameBoard copy = new GameBoard();
		copy.setBoardState(boardState);
		return copy;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof GameBoard))
			return false;
		GameBoard other = (GameBoard)o;
		//just check board state - boardcolors don't matter for equality
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				if(this.boardState[i][j] != other.getBoardState()[i][j])
					return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				result = 31 * result + Character.hashCode(boardState[i][j]);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		for(int i = 0; i < boardState.length; i++) {
			sb.append(i + "\t");
			for(int j = 0; j < boardState.length; j++) {
				sb.append(boardState[i][j] + " ");
			}		
			sb.append("\t");			
			for(int k = 0; k < boardColors.length; k++) {
				sb.append(boardColors[i][k] + " ");
			}
			sb.append("\n");
		}
		
		sb.append("\n \t");
		for(int i = 0; i < boardState.length; i++) {
			sb.append(i + " ");
		}
		sb.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		return sb.toString();
	}
	public void setBoardState(char[][] boardState) {
		this.boardState = boardState;
	}
	public char[][] getBoardState() {
		return boardState;
	}
	public char[][] getBoardColors() {
		return boardColors;
	}
	public boolean isOccupied(BoardPosition p) {
		return boardState[p.getY()][p.getX()] != '-';
	}
	public boolean isInBounds(BoardPosition p) {
		return (p.getX() >= 0 && p.getX() < 8) && (p.getY() >= 0 && p.getY() < 8);
	}
}
