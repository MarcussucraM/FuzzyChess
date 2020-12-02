package models;

import java.util.ArrayList;
import java.util.List;

public interface CorpControls {
    void removeMember(ChessPiece member);
    List<ChessPiece> getMembers();
    List<BoardPosition> getMemberPositions();
    ChessPiece getMemberAt(BoardPosition p);
}
