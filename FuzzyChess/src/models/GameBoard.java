package models;

import java.util.*;

public class GameBoard{
	/*positions of all pieces in game - lowercase is black, uppercase is white
	 * r - rook
	 * n - knight
	 * b - bishop
	 * q - queen
	 * k - king
	 * p - pawn */
	private char[][] boardState = {{'r','n','b','q','k','b','n','r'},
								   {'p','p','p','p','p','p','p','p'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'-','-','-','-','-','-','-','-'},
								   {'P','P','P','P','P','P','P','P'},
								   {'R','N','B','Q','K','B','N','R'}};
	
	/*colors of all board tiles
	 * w - white
	 * b - black
	 * m - possible movement space
	 * c - possible capture space  */
	private char[][] boardColors = {{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'},
									{'#','.','#','.','#','.','#','.'},
									{'.','#','.','#','.','#','.','#'}};
	
	//i = y, j = x
	public void updateBoardState(BoardPosition oldPosition, BoardPosition newPosition) {
		boardState[newPosition.getY()][newPosition.getX()] = boardState[oldPosition.getY()][oldPosition.getX()];
		boardState[oldPosition.getY()][oldPosition.getX()] = '-';
	}
	
	//list movements
	//list chesspieces
	//list captures
	//selected location
	//updates the boards colors for display
	//displays what peices are active
	//what piece is selected
	//what available moves and captures are
	//available to the piece
	public void updateBoardColors(ArrayList<BoardPosition> activeCorps, ArrayList<BoardPosition> movements, ArrayList<BoardPosition> captures) {
		resetBoardColors();
		if(activeCorps != null) {
			for(BoardPosition p : activeCorps) {
				boardColors[p.getY()][p.getX()] = 'a';
			}
		}
		
		if(movements != null) {
			for(BoardPosition p : movements) {
				boardColors[p.getY()][p.getX()] = 'm';
			}
		}
		
		if(captures != null) {
			for(BoardPosition p : captures) {
				boardColors[p.getY()][p.getX()] = 'c';
			}
		}
	}
	
	public void setBoardState(char[][] newBoardState) {
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState.length; j++) {
				boardState[i][j] = newBoardState[i][j];
			}
		}
	}
	
	public void setBoardColors(char[][] newBoardColors) {
		
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
	
	public void resetBoardState(){
		 char[][] initialState =   {{'r','n','b','q','k','b','n','r'},
								    {'p','p','p','p','p','p','p','p'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'-','-','-','-','-','-','-','-'},
								    {'P','P','P','P','P','P','P','P'},
								    {'R','N','B','Q','K','B','N','R'}};


		 for(int i = 0; i < boardState.length; i++) {
			 for(int j = 0; j < boardState.length; j++) {
				 boardState[i][j] = initialState[i][j];
			 }
		 }
	}
	
	
	public void resetBoardColors() {
		char[][] initialColors =	{{'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'},
									 {'#','.','#','.','#','.','#','.'},
									 {'.','#','.','#','.','#','.','#'}};
		
		for(int i = 0; i < boardColors.length; i++) {
			 for(int j = 0; j < boardColors.length; j++) {
				 boardColors[i][j] = initialColors[i][j];
			 }
		 }
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





	private class GameNode{
		public GameBoard state;
		public GameNode parent;
		public ChessPiece currentPiece;
		public int depth;

		public GameNode(GameBoard state, ChessPiece currentPiece, GameNode parent) {
			this.state = state;
			this.currentPiece = currentPiece;
			this.parent = parent;
			depth = parent == null ? 0 : parent.depth + 1;
		}

		@Override
		public int hashCode() {
			int result = state.hashCode();
			result = 31 * result + currentPiece.hashCode();
			result = 31 * result + Integer.hashCode(depth);
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof GameNode))
				return false;
			GameNode other = (GameNode)o;
			return (state.equals(other.state) && currentPiece.equals(other.currentPiece));
		}
	}

	//creates a new gamenode for pathfinding algorithm
	private GameNode getNode(GameNode parent, BoardPosition action,ChessPiece selectedPiece) {
		if(parent == null || action == null) {
			return new GameNode(this.copy(), selectedPiece.copy(), null);
		}

		GameBoard newState = parent.state.copy();
		ChessPiece piece = parent.currentPiece.copy();

		if(newState.isInBounds(action) && !newState.isOccupied(action)){
			newState.updateBoardState(piece.getPosition(), action);
			piece.setPosition(action);
			return new GameNode(newState, piece, parent);
		}

		return null;
	}

	//todo - add meaningful comments
	public ArrayList<BoardPosition> getMovementPositions(ChessPiece selectedPiece) {
		GameNode root = new GameNode(this.copy(), selectedPiece.copy(), null);
		Queue<GameNode> frontier = new LinkedList<GameNode>();
		frontier.add(root);
		HashSet<BoardPosition> explored = new HashSet<BoardPosition>();
		int maxDepth = root.currentPiece.getMoveCount();

		while(!frontier.isEmpty()) {
			GameNode curNode = frontier.remove();
			if(curNode.depth > maxDepth) break;
			if(!curNode.state.equals(root.state)) //dont want to count start position
				explored.add(curNode.currentPiece.getPosition());
			for(int i = 0; i < curNode.currentPiece.getActions().size(); i++) {
				BoardPosition curAction = curNode.currentPiece.getActions().get(i);
				GameNode child = getNode(curNode, curAction,curNode.currentPiece);
				if((child != null) && !(frontier.contains(child)) && !(explored.contains(curAction))) {
					frontier.add(child);
				}
			}
		}
		return new ArrayList<BoardPosition>(explored);
	}

	public ArrayList<BoardPosition> getCapturePositions(ChessPiece selectedPiece,List<Corp> enemyCorps){
		ArrayList<BoardPosition> capturePositions = new ArrayList<BoardPosition>();
		switch(selectedPiece.getid()) {
			case 'n':
			case 'N': //knight attack - melee/charge
				for(BoardPosition move : this.getMovementPositions(selectedPiece)) {
					ChessPiece current = new ChessPiece(move, selectedPiece.getid(), selectedPiece.getDirection());
					capturePositions.addAll(getSurroundingEnemyPositions(current, 1,enemyCorps));
				}
				break;
			case 'r':
			case 'R': //rook attack - ranged radius of 3
				capturePositions.addAll(getSurroundingEnemyPositions(selectedPiece, 3,enemyCorps));
				break;
			case 'p':
			case 'P':
			case 'b':
			case 'B': //pawn and bishop attack - melee front and diagonal
				for(BoardPosition p : selectedPiece.getActions()) {
					for(Corp enemyCorp : enemyCorps) {
						if(enemyCorp.getMemberAt(p) != null) {
							capturePositions.add(p);
						}
					}
				}
				break;
			default: //king/queen attack - melee adjacent enemies
				capturePositions.addAll(getSurroundingEnemyPositions(selectedPiece, 1,enemyCorps));
		}
		return capturePositions;
	}

	//check attack radius of current ChessPiece for possible enemies
	public ArrayList<BoardPosition> getSurroundingEnemyPositions(ChessPiece current, int radius, List<Corp> enemyCorps){
		ArrayList<BoardPosition> surroundingEnemyPositions = new ArrayList<BoardPosition>();
		int min_x = (current.getPosition().getX() - radius) < 0 ? 0 : current.getPosition().getX() - radius;
		int max_x = (current.getPosition().getX() + radius) > 7 ? 7 : current.getPosition().getX() + radius;
		int min_y = (current.getPosition().getY() - radius) < 0 ? 0 : current.getPosition().getY() - radius;
		int max_y = (current.getPosition().getY() + radius) > 7 ? 7 : current.getPosition().getY() + radius;

		for(int y = min_y; y <= max_y; y++) {
			for(int x = min_x; x <= max_x; x++) {
				BoardPosition p = new BoardPosition(x,y);
				if(!current.getPosition().equals(p)) {
					for(Corp enemyCorp : enemyCorps) {
						if(enemyCorp.getMemberAt(p) != null) {
							surroundingEnemyPositions.add(p);
						}
					}
				}
			}
		}
		return surroundingEnemyPositions;
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
}
