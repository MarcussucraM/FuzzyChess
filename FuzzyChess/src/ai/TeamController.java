package ai;

import engine.FuzzyChessEngine;
import models.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TeamController implements CorpControls {
    protected List<Corp> corps;
    protected List<ChessPiece> captures;

    public TeamController(){
        this.corps = new ArrayList<>();
        this.captures = new ArrayList<>();
    }
    public List<Corp> getCorps(){
        return corps;
    }
    public List<ChessPiece> getCaptures(){
        return captures;
    }
    public abstract void makeMove(FuzzyChessEngine game, BoardPosition placeholder);

    @Override
    public void removeMember(ChessPiece member) {
        for(Corp corp : corps){
            if(corp.getMembers().contains(member)){
                corp.removeMember(member);
                return;
            }
        }
    }

    @Override
    public List<ChessPiece> getMembers() {
        List<ChessPiece> members = new ArrayList<>();
        for(Corp corp : corps){
           members.addAll(corp.getMembers());
        }
        return members;
    }

    @Override
    public List<BoardPosition> getMemberPositions() {
        List<BoardPosition> members = new ArrayList<>();
        for(Corp corp : corps){
            members.addAll(corp.getMemberPositions());
        }
        return members;
    }

    @Override
    public ChessPiece getMemberAt(BoardPosition p) {
        for(Corp corp : corps){
            if(corp.getMemberAt(p) != null){
                return corp.getMemberAt(p);
            }
        }
        return null;
    }
}
