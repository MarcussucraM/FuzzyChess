package engine;

import java.util.Scanner;

import ai.AiController;
import ai.PlayerController;
import ai.TeamController;
import gui.FuzzyChessConsoleDisplay;
import models.BoardPosition;
import models.FuzzyChess;

/*Acts as an interface to user through the console to the game models*/
public class FuzzyChessConsoleEngine {
	private Scanner inputScanner;
	private FuzzyChess game;
	private TeamController ai = new AiController();
	private TeamController player = new PlayerController();
	
	public FuzzyChessConsoleEngine() {
		inputScanner = new Scanner(System.in);
		game = new FuzzyChess(player,ai);
	}
	
	public void run() {
		System.out.println("FUZZY CHESS");
		System.out.println("enter o at piece selection for options...");
		while(!game.isGameOver()) {
			FuzzyChessConsoleDisplay.displayTurn(game.getTurn(), game.getSubTurn(), game.getBoard().toString());
			
			if(game.getTurn() == 0) {
				getPlayer1Move();
			}
			
			else if(game.getTurn() == 1) {
				//ai turn - currently just ends turn
				getPlayer2Move();
			}
		}
		
		System.out.println("Game Over!");
	}
	
	public void getPlayer1Move() {
		while(true) {
			System.out.println("Select a piece");
			System.out.print(">>>");
			String input = inputScanner.nextLine();
			
			if(input.equals("e")) {
				game.endTurn();
				break;
			}
			else if(input.contentEquals("q")) {
				game.quitGame();
				break;
			}
			else if(input.contentEquals("c")) {
				FuzzyChessConsoleDisplay.displayCapturedPieces(game.getPlayers()[0].getCaptures().toString(), game.getPlayers()[1].getCaptures().toString());
			}
			else if(input.contentEquals("o")) {
				FuzzyChessConsoleDisplay.displayOptions();
			}
			else if(input.contentEquals("r")) {
				FuzzyChessConsoleDisplay.displayRules();
			}
			else{
				try{
					String[] inputs = input.replace(" ", "").split(",");
					int x = Integer.parseInt(inputs[0]);
					int y = Integer.parseInt(inputs[1]);
					BoardPosition selectedPosition = new BoardPosition(x, y);
					if(game.makeMove(selectedPosition)) {
						System.out.println(String.format("Selected %s", game.getSelectedPiece()));
						FuzzyChessConsoleDisplay.displayTurn(game.getTurn(), game.getSubTurn(), game.getBoard().toString());
						
						//Select move location or capture location
						System.out.println("Select place to move to/or a piece to capture.");
						System.out.print(">>>");
						input = inputScanner.nextLine();
						inputs = input.replace(" ",  "").split(",");
						x = Integer.parseInt(inputs[0]);
						y = Integer.parseInt(inputs[1]);
						selectedPosition = new BoardPosition(x, y);
						
						//make the move and update the state of the game
						if(game.makeMove(selectedPosition)) {
							//if theres a selected enemy piece then a capture happened
							//display all the stats
							if(game.getSelectedEnemy() != null) {
								FuzzyChessConsoleDisplay.displayAttack(game.getSelectedPiece().toString(), game.getSelectedEnemy().toString(),
										game.getLastRoll(), game.isDiceOffset(), game.getCaptureResult(), 
										game.getSelectedPiece().getRolls(game.getSelectedEnemy()));
							}
							game.endSubturn();
						}
						else {
							System.out.println("\nNo move made\n");
						}
						break;
					}
					else {
						System.out.println("\nNo piece selected\n");
					}
				} catch(NumberFormatException e) {
					System.out.println("\nInvalid Input\n");
				}
			}
			inputScanner.reset();
		}
	}
	
	public void getPlayer2Move() {
		//
		game.endTurn();
	}
}
