package pt314.blocks.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pt314.blocks.game.Block;
import pt314.blocks.game.Direction;
import pt314.blocks.game.GameBoard;
import pt314.blocks.game.HorizontalBlock;
import pt314.blocks.game.TargetBlock;
import pt314.blocks.game.VerticalBlock;

/**
 * Simple GUI test...
 */
public class SimpleGUI extends JFrame implements ActionListener {

	private static  int NUM_ROWS;
	private static  int NUM_COLS;

	private GameBoard board;
	
	// currently selected block
	private Block selectedBlock;
	private int selectedBlockRow;
	private int selectedBlockCol;

	private GridButton[][] buttonGrid;
	
	private JMenuBar menuBar;
	private JMenu gameMenu, helpMenu;
	private JMenuItem newGameMenuItem;
	private JMenuItem exitMenuItem;
	private JMenuItem aboutMenuItem;
	
	// image icons for blocks
	private ImageIcon targetImg;
	private ImageIcon verticalImg;
	private ImageIcon horizontalImg;
	private ImageIcon emptyImg;
	
	public SimpleGUI() {
		super("Blocks");
		
		initMenus();
		
		initImageIcons();
		
		initBoard();
		
		pack();
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void initImageIcons() {
		try{
		targetImg = new ImageIcon("res/images/Target.png");
		verticalImg = new ImageIcon("res/images/VerticalBlock.png");
		horizontalImg = new ImageIcon("res/images/HorizontalBlock.png");
		emptyImg = new ImageIcon("res/images/EmptyCell.png");
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	private void initMenus() {
		menuBar = new JMenuBar();
		
		gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);
		
		helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		newGameMenuItem = new JMenuItem("New game");
		newGameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initBoard();
			}
		});
		gameMenu.add(newGameMenuItem);
		
		gameMenu.addSeparator();
		
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		gameMenu.add(exitMenuItem);
		
		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(SimpleGUI.this, "Sliding blocks!");
			}
		});
		helpMenu.add(aboutMenuItem);
		
		setJMenuBar(menuBar);
	}
	
	private void initBoard() {
		getContentPane().removeAll();
		
		char [][] map = loadPuzzle();
		board = new GameBoard(NUM_COLS, NUM_ROWS);
		buttonGrid = new GridButton[NUM_ROWS][NUM_COLS];
		
		setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
		
		int targetRow = NUM_ROWS/2;
		int targetCol = NUM_COLS-1;
		int HorizontalBlockCol = 0;
		int targetCount = 0;
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				GridButton cell = new GridButton(row, col);
				cell.setPreferredSize(new Dimension(64, 64));
				cell.addActionListener(this);
				cell.setOpaque(true);
				buttonGrid[row][col] = cell;
				add(cell);
				
				switch (map[row][col]) {
				case 'H':
					if(row == targetRow && col>= targetCol)
						throw new IllegalStateException();
					board.placeBlockAt(new HorizontalBlock(), row, col);
					if(row == targetRow)
						HorizontalBlockCol = col;
					break;
				case 'V':
					board.placeBlockAt(new VerticalBlock(), row, col);
					break;	
				case 'T':
					targetCount++;
					if(row != targetRow || col < HorizontalBlockCol || targetCount > 1)
						throw new IllegalStateException();
					board.placeBlockAt(new TargetBlock(), row, col);
					targetCol = col;
					break;	
				default:
					break;
				}
			}
		}
			
		if(targetCount == 0)
			throw new IllegalStateException();
		updateUI();
	}
	/**
	 * Load a puzzle from a file and return two dimensional character array that represent the game map.
	 * @throws IllegalStateException if and only if number of columns or number of rows less than 1.
	 */
	
	private char [][] loadPuzzle(){
		char map[][] = null;
		try {
			Scanner inFile = new Scanner(new FileReader("res/puzzles/puzzle-000.txt"));
			String strLine[]=inFile.nextLine().split("\\s+");
			NUM_ROWS = Integer.parseInt(strLine[0]);
			NUM_COLS = Integer.parseInt(strLine[1]);
			if(NUM_ROWS< 1 || NUM_COLS < 1)
				throw new IllegalStateException("NUM_ROWS = "+NUM_ROWS +" NUM_COLS"+ NUM_COLS);
			map = new char[NUM_ROWS][NUM_COLS];
			int i=0;
			while(inFile.hasNextLine())
			{
			   map[i]= inFile.nextLine().toCharArray();
			   i++;
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		return map;
	}
	
	// Update display based on the state of the board...
	// TODO: make this more efficient
	private void updateUI() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLS; col++) {
				Block block = board.getBlockAt(row, col);
				JButton cell = buttonGrid[row][col];
				if (block == null)
					cell.setIcon(emptyImg);
				else if (block instanceof TargetBlock)
					cell.setIcon(targetImg);
				else if (block instanceof HorizontalBlock)
					cell.setIcon(horizontalImg);
				else if (block instanceof VerticalBlock)
					cell.setIcon(verticalImg);
			}
		}
	}

	/**
	 * Handle board clicks.
	 * 
	 * Movement is done by first selecting a block, and then
	 * selecting the destination.
	 * 
	 * Whenever a block is clicked, it is selected, even if
	 * another block was selected before.
	 * 
	 * When an empty cell is clicked after a block is selected,
	 * the block is moved if the move is valid.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Handle grid button clicks...
		GridButton cell = (GridButton) e.getSource();
		int row = cell.getRow();
		int col = cell.getCol();
		System.out.println("Clicked (" + row + ", " + col + ")");
		
		if (selectedBlock == null || board.getBlockAt(row, col) != null) {
			selectBlockAt(row, col);
		}
		else {
			moveSelectedBlockTo(row, col);
		}
	}

	/**
	 * Select block at a specific location.
	 * 
	 * If there is no block at the specified location,
	 * the previously selected block remains selected.
	 * 
	 * If there is a block at the specified location,
	 * the previous selection is replaced.
	 */
	private void selectBlockAt(int row, int col) {
		Block block = board.getBlockAt(row, col);
		if (block != null) {
			selectedBlock = block;
			selectedBlockRow = row;
			selectedBlockCol = col;
		}
	}
	
	/**
	 * Try to move the currently selected block to a specific location.
	 * 
	 * If the move is not possible, nothing happens.
	 */
	private void moveSelectedBlockTo(int row, int col) {
		
		int vertDist = row - selectedBlockRow;
		int horzDist = col - selectedBlockCol;
		
		if (vertDist != 0 && horzDist != 0) {
			System.err.println("Invalid move!");
			return;
		}
		
		Direction dir = getMoveDirection(selectedBlockRow, selectedBlockCol, row, col);
		int dist = Math.abs(vertDist + horzDist);
		
		if (!board.moveBlock(selectedBlockRow, selectedBlockCol, dir, dist)) {
			System.err.println("Invalid move!");
		}
		else {
			selectedBlock = null;
			updateUI();
			if(board.getBlockAt(NUM_ROWS/2 , NUM_COLS-1) instanceof TargetBlock)
			{
				int dialogButton=0;
				dialogButton = JOptionPane.showConfirmDialog (SimpleGUI.this, "Congratulation...\n You win \n Do you like to restart the game","Blocks",dialogButton);
                if(dialogButton == JOptionPane.YES_OPTION)
                	initBoard();
                else
                	System.exit(0);
			}
		}
	}

	/**
	 * Determines the direction of a move based on
	 * the starting location and the destination.
	 *  
	 * @return <code>null</code> if both the horizontal distance
	 * 	       and the vertical distance are not zero. 
	 */
	private Direction getMoveDirection(int startRow, int startCol, int destRow, int destCol) {
		int vertDist = destRow - startRow;
		int horzDist = destCol - startCol;
		if (vertDist < 0)
			return Direction.UP;
		if (vertDist > 0)
			return Direction.DOWN;
		if (horzDist < 0)
			return Direction.LEFT;
		if (horzDist > 0)
			return Direction.RIGHT;
		return null;
	}
}
