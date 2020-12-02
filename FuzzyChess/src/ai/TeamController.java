package ai;

import engine.FuzzyChessEngine;
import models.BoardPosition;
import models.ChessPiece;
import models.Corp;
import models.FuzzyChess;

import java.util.ArrayList;

public abstract class TeamController {
    protected ArrayList<Corp> corps;
    protected ArrayList<ChessPiece> captures;
    public TeamController(){
        this.corps = new ArrayList<>();
        this.captures = new ArrayList<>();
    }



    public ArrayList<Corp> getCorps(){
        return corps;
    }
    public ArrayList<ChessPiece> getCaptures(){
        return captures;
    }


    public abstract void makeMove(FuzzyChessEngine game, BoardPosition placeholder);
}
