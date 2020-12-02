package models;

import java.util.ArrayList;

public class ChessPiece {
	private BoardPosition position;
	private char id;
	private int direction;
	/*							king	queen	knight	bishop	rook	pawn */
	private int[][][] rolls = {{{4,5,6},{4,5,6},{4,5,6},{4,5,6},{5,6},{1,2,3,4,5,6}}, 
							   {{4,5,6},{4,5,6},{4,5,6},{4,5,6},{5,6},{2,3,4,5,6}},
							   {{6},{6},{4,5,6},{4,5,6},{5,6},{2,3,4,5,6}},
							   {{5,6},{5,6},{5,6},{4,5,6},{5,6},{3,4,5,6}},
							   {{4,5,6},{4,5,6},{5,6},{5,6},{6},{5,6}},
							   {{6},{6},{6},{5,6},{6},{4,5,6}}};
	public final static int DOWN = 1;
	public final static int UP = -1;
	
	
	public ChessPiece(BoardPosition pos, char id, int direction) {
		this.position = pos;
		this.id = id;
		this.direction = direction;
	}
	public double getImportance(){
		switch(id){
			case 'r':
			case 'B':
			case 'R':
			case 'b':
				return 1.5;
			case 'n':
			case 'N':
				return 1.3;
			case 'q':
			case 'Q':
				return 2;
			case 'k':
			case 'K':
				return 3;
			default:
				return 1;
		}
	}
	
	private String getName() {
		switch(id) {
		case 'p':
			return "Black Pawn";
		case 'P':
			return "White Pawn";
		case 'r':
			return "Black Rook";
		case 'R':
			return "White Rook";
		case 'n':
			return "Black Knight";
		case 'N':
			return "White Knight";
		case 'b':
			return "Black Bishop";
		case 'B':
			return "White Bishop";
		case 'q':
			return "Black Queen";
		case 'Q':
			return "White Queen";
		case 'k':
			return "Black King";
		case 'K':
			return "White King";
		default:
			return null;
		}
	}
	
	private int convertIDtoArrayPosition() {
		switch(id) {
		case 'k':
		case 'K':
			return 0;
		case 'q':
		case 'Q':
			return 1;
		case 'n':
		case 'N':
			return 2;
		case 'b':
		case 'B':
			return 3;
		case 'r':
		case 'R':
			return 4;
		case 'p':
		case 'P':
			return 5;
		default:
			return 0;	
		}
			
	}
	
	public char getid() {
		return id;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public BoardPosition getPosition() {
		return position;
	}
	
	public void setPosition(BoardPosition pos) {
		position = pos;
	}

	public int[] getRolls(ChessPiece other) {
		return rolls[convertIDtoArrayPosition()][other.convertIDtoArrayPosition()];
	}
	
	
	//returns possible locations a piece may step to
	//disregards if there is a piece in that location
	//or if its out of bounds - just the possibilities
	//the rest is checked later
	public ArrayList<BoardPosition> getActions() {
		int startX = position.getX();
		int startY = position.getY();
		ArrayList<BoardPosition> possibleActions = new ArrayList<BoardPosition>();

		//pawn movements - only able to move forward and forward diagonal
		possibleActions.add(new BoardPosition(startX, startY + direction));
		possibleActions.add(new BoardPosition(startX + 1, startY + direction));
		possibleActions.add(new BoardPosition(startX - 1, startY + direction));
		
		//if its a pawn - end here
		if(id == 'p' || id == 'P') return possibleActions;

		//rest of piece movements
		//backward and back diagonal
		possibleActions.add(new BoardPosition(startX, startY - direction));
		possibleActions.add(new BoardPosition(startX + 1, startY - direction));
		possibleActions.add(new BoardPosition(startX - 1, startY - direction));
		
		//left and right
		possibleActions.add(new BoardPosition(startX + 1, startY));
		possibleActions.add(new BoardPosition(startX - 1, startY));
		
		return possibleActions;
	}
	
	//returns number of steps a piece can move
	public int getMoveCount() {
		switch(id) {
			case 'p':				
			case 'P':
			case 'r':
			case 'R':
			case 'b':
			case 'B':
				return 1;
			case 'q':
			case 'Q':
			case 'k':
			case 'K':
				return 3;
			case 'n':
			case 'N':
				return 5;
			default:
				return 0;
		}
	}
	
	public ChessPiece copy() {
		return new ChessPiece(new BoardPosition(position.getX(), position.getY()), id, direction);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ChessPiece))
			return false;
		ChessPiece other = (ChessPiece)o;
		return (this.direction == other.direction && this.id == other.id && this.position.equals(other.getPosition()));
	}
	
	@Override
	public int hashCode() {
		int result = position.hashCode();
		result = 31 * result + Character.hashCode(id);
		result = 31 * result + Integer.hashCode(direction);
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
