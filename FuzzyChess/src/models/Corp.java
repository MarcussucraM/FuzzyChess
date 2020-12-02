package models;

import java.util.ArrayList;

/* Structure that contains members and leader of corp
 * Leaders: Left Bishop, Right Bishop, King
 * Left Bishop controls left 3 pawns and knight
 * Right Bishop controls right 3 pawns and knight
 * King controls Rooks, Queen, and middle 2 pawns
 * King can delegate control of its pieces to bishops
 * so essentially bishops can control all pieces excluding king
 * When Bishops die the king gains control of all its remaining pieces
 * However a subturn per turn is lost
 * When King dies its game over.
 */

//update getMemberAt

public class Corp {
	private ArrayList<ChessPiece> members;
	private ChessPiece leader;
	private Corp kingsCorp;
	private boolean isActive;
	
	public Corp(Corp kingsCorp) {
		members = new ArrayList<ChessPiece>();
		this.kingsCorp = kingsCorp;
		isActive = true;
	}
	
	public void addMember(ChessPiece member) {
		if(member.getid() == 'k' || member.getid() == 'K' || member.getid() == 'b' || member.getid() == 'B') {
			leader = member;
		}
		members.add(member);
	}
	
	//update to remove king corp member if captured
	public void removeMember(ChessPiece member) {
		//if member is a delegated member from king - remove it from kings members
		if(kingsCorp != null && kingsCorp.getMembers().contains(member)) {
			kingsCorp.removeMember(member);
		}
		else {
			members.remove(member);
			//if the member is a bishop - transfer the members it commands to the king
			if((member.getid() != 'K' && member.getid() != 'k') && member.getid() == leader.getid()) {
				transferMembers();
			}		
		}
	}
	
	public void removeAll() {
		members.removeAll(members);
		leader = null;
	}
	
	//maybe redo - idk if we'll need a reference to the leader later
	public ChessPiece getLeader() {
		return leader;
	}
	
	//redo - currently returns reference to all members
	public ArrayList<ChessPiece> getMembers() {
		ArrayList<ChessPiece> totalMembers = new ArrayList<ChessPiece>();
		totalMembers.addAll(members);
		//if null then this is the kings corp
		//otherwise king delegates all members besides himself to bishops
		if(kingsCorp != null) {
			totalMembers.addAll(kingsCorp.getMembers());
			totalMembers.remove(kingsCorp.getLeader());
		}
		return totalMembers;			
	}
	
	public ArrayList<BoardPosition> getMemberPositions(){
		ArrayList<BoardPosition> memberPositions = new ArrayList<BoardPosition>();
		for(ChessPiece member : getMembers()) {
			memberPositions.add(member.getPosition());
		}
		return memberPositions;
	}

	public ChessPiece getMemberAt(BoardPosition p) {
		for(ChessPiece member : getMembers()) {
			if(member.getPosition().equals(p)) {
				return member;
			}
		}
		return null;
	}
	
	//in case leader dies
	private void transferMembers() {
		for(int i = 0; i < members.size(); i++) {
			kingsCorp.addMember(members.get(i));
			members.remove(members.get(i));
		}
		isActive = false;
	}
	
	public boolean isActive() {
		return isActive;
	}
}
