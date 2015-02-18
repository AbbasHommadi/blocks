package pt314.blocks.gui;

import javax.swing.JButton;

public class GridButton extends JButton {

	private int row;
	private int col;

	public GridButton(int row, int col) {
		//super("(" + row + ", " + col + ")");
		super();
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
}
