package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AttackPanel extends JPanel implements Runnable{
	private static final long serialVersionUID = 1030010850185263656L;
	private GameResources resources;
	private ImagePanel attackerPanel;
	private ImagePanel dicePanel;
	private ImagePanel defenderPanel;
	private JLabel attackerLabel;
	private JLabel defenderLabel;
	public Thread diceRoller;
	private int lastRoll;
	
	public AttackPanel() {
		setLayout(new FlowLayout());
		attackerLabel = new JLabel("Attacker");
		defenderLabel = new JLabel("Defender");
		attackerPanel = new ImagePanel();
		dicePanel = new ImagePanel();
		defenderPanel = new ImagePanel();
		
		add(attackerLabel);
		add(attackerPanel);
		add(dicePanel);
		add(defenderPanel);
		add(defenderLabel);
	}
	
	private class ImagePanel extends JPanel{
		private int WIDTH = 100;
		private int HEIGHT = 100;
		private int OFFSET = 10;
		private BufferedImage img;
		
		public ImagePanel() {
			setPreferredSize(new Dimension(WIDTH,HEIGHT));
			setLayout(new BorderLayout());
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(resources.getBoardColor());
			g.setColor(resources.getBoardBorderColor());
			g.drawRect(0,0,WIDTH-1,HEIGHT-1);
			
			if(img != null) {
				g.drawImage(img, OFFSET, OFFSET, WIDTH-OFFSET*2, HEIGHT-OFFSET*2, resources.getBoardColor(), this);
			}
			g.dispose();
		}
		
		public void setImage(BufferedImage i) {
			img = i;
		}
		
	}
	
	//start animation - freeze rest of gui
	public void rollDice(int lastroll) {
		lastRoll = lastroll;
		if(diceRoller == null) {
			diceRoller = new Thread(this);
			diceRoller.start();
		}
	}
	
	public void update(char attackerID, char defenderID, String attackResult) {
		attackerPanel.setImage(resources.getChessSprite(attackerID));
		defenderPanel.setImage(resources.getChessSprite(defenderID));
		attackerPanel.repaint();
		defenderPanel.repaint();
	}
	
	public void setTheme(GameResources t) {
		resources = t;
		setBackground(resources.getBackgroundColor());
		attackerLabel.setForeground(resources.getForegroundColor());
		defenderLabel.setForeground(resources.getForegroundColor());
		setBorder(BorderFactory.createLineBorder(resources.getBoardBorderColor()));
		attackerLabel.setFont(resources.getFontStyle());
		defenderLabel.setFont(resources.getFontStyle());
		dicePanel.setImage(resources.getDiceSprite(1));
		attackerPanel.repaint();
		defenderPanel.repaint();
		dicePanel.repaint();
	}

	@Override
	public void run() {
		for(int i = 1; i <= 10; i++) {
			int nextImageID = i < 10 ? (int)((Math.random() * 100) % 6) + 1 : lastRoll;
			dicePanel.setImage(resources.getDiceSprite(nextImageID));
			dicePanel.repaint();
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		diceRoller = null;
	}
}
