package pt314.blocks.game;

import static org.junit.Assert.*;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

public class GameBoardTest {
	
	private GameBoard board;
	
	@Before
	public void setUp(){
		board = new GameBoard(5, 5);
		board.placeBlockAt(new HorizontalBlock(), 0, 0);
		board.placeBlockAt(new HorizontalBlock(), 4, 4);
		board.placeBlockAt(new VerticalBlock(), 1, 3);
		board.placeBlockAt(new VerticalBlock(), 3, 1);
		board.placeBlockAt(new TargetBlock(), 2, 2);
	}
	
	@Test
	public void testMoveBlockOutofBounds() {
		//checking horizontal block 
		assertTrue(board.getBlockAt(4, 4) instanceof HorizontalBlock);
		assertFalse(board.moveBlock(4, 4, Direction.RIGHT, 1));
		assertFalse(board.moveBlock(4, 4, Direction.LEFT, 5));
		
		//checking vertical block 
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		assertFalse(board.moveBlock(1, 3, Direction.UP, 2));
		assertFalse(board.moveBlock(1, 3, Direction.DOWN, 4));
		
		//checking target block 
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertFalse(board.moveBlock(2, 2, Direction.RIGHT, 4));
		assertFalse(board.moveBlock(2, 2, Direction.LEFT, 4));
	}
	
	@Test
	public void testMoveNullBlock() {
		assertNull(board.getBlockAt(3, 3));
		assertFalse(board.moveBlock(3, 3, Direction.RIGHT, 1));
		assertFalse(board.moveBlock(3, 3, Direction.LEFT, 1));
		assertFalse(board.moveBlock(3, 3, Direction.UP, 1));
		assertFalse(board.moveBlock(3, 3, Direction.DOWN, 1));
	}
	
	@Test
	public void testValidMoveForOneOrMoreThanOneBlocks() {
		assertTrue(board.getBlockAt(4, 4) instanceof HorizontalBlock);
		assertTrue(board.moveBlock(4, 4, Direction.LEFT, 1));
		assertNull(board.getBlockAt(4, 4));
		assertTrue(board.getBlockAt(4, 3) instanceof HorizontalBlock);
		assertTrue(board.moveBlock(4, 3, Direction.LEFT, 2));
		assertNull(board.getBlockAt(4, 3));
		assertTrue(board.getBlockAt(4, 1) instanceof HorizontalBlock);
		
		assertTrue(board.getBlockAt(0, 0) instanceof HorizontalBlock);
		assertTrue(board.moveBlock(0, 0, Direction.RIGHT, 1));
		assertNull(board.getBlockAt(0, 0));
		assertTrue(board.getBlockAt(0, 1) instanceof HorizontalBlock);
		assertTrue(board.moveBlock(0, 1, Direction.RIGHT, 2));
		assertNull(board.getBlockAt(0, 1));
		assertTrue(board.getBlockAt(0, 3) instanceof HorizontalBlock);
		
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 2, Direction.RIGHT, 1));
		assertNull(board.getBlockAt(2, 2));
		assertTrue(board.getBlockAt(2, 3) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 3, Direction.LEFT, 1));
		assertNull(board.getBlockAt(2, 3));
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 2, Direction.RIGHT, 2));
		assertNull(board.getBlockAt(2, 2));
		assertTrue(board.getBlockAt(2, 4) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 4, Direction.LEFT, 2));
		assertNull(board.getBlockAt(2, 4));
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		assertTrue(board.moveBlock(1, 3, Direction.DOWN, 1));
		assertNull(board.getBlockAt(1, 3));
		assertTrue(board.getBlockAt(2, 3) instanceof VerticalBlock);
		assertTrue(board.moveBlock(2, 3, Direction.DOWN, 2));
		assertNull(board.getBlockAt(2, 3));
		assertTrue(board.getBlockAt(4, 3) instanceof VerticalBlock);
		
		assertTrue(board.moveBlock(4, 3, Direction.UP, 1));
		assertNull(board.getBlockAt(4, 3));
		assertTrue(board.getBlockAt(3, 3) instanceof VerticalBlock);
		assertTrue(board.moveBlock(3, 3, Direction.UP, 2));
		assertNull(board.getBlockAt(3, 3));
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		
	}
	
	/**
	 * test invalid direction move for horizontal blocks and target block by trying to move them vertically.
	 */
	@Test
	public void testInvalidVerticleMoveForHorizontalAndTargetBlocks() {
		//checking invalid vertical move for horizontal blocks
		assertTrue(board.getBlockAt(4, 4) instanceof HorizontalBlock);
		assertFalse(board.moveBlock(4, 4, Direction.UP, 1));
		assertTrue(board.getBlockAt(0,0) instanceof HorizontalBlock);
		assertFalse(board.moveBlock(0, 0, Direction.DOWN, 1));
		
		//checking invalid vertical move for target block
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertFalse(board.moveBlock(2, 2, Direction.DOWN, 1));
		assertFalse(board.moveBlock(2, 2, Direction.UP, 1));
	}
	
	/**
	 * test invalid direction move for vertical blocks by trying to move them horizontally.
	 */
	@Test
	public void testInvalidHorizontalMoveForVerticalBlocks() {
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		assertFalse(board.moveBlock(1, 3, Direction.LEFT, 1));
		assertFalse(board.moveBlock(1, 3, Direction.RIGHT, 1));
	}
	
	/**
	 * test to move horizontal, or target blocks with a block in the way(invalid move).
	 */
	@Test
	public void testMoveH_TBlocksWithaBlockInTheWay() {
		// Move vertical block at(1,3) to (0,3) , to make it in horizontal path of another block(0,0) 
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		assertTrue(board.moveBlock(1, 3, Direction.UP, 1));
		assertTrue(board.getBlockAt(0, 3) instanceof VerticalBlock);
		
		assertTrue(board.getBlockAt(0, 0) instanceof HorizontalBlock);
		assertFalse(board.moveBlock(0, 0, Direction.RIGHT, 4));
		assertFalse(board.moveBlock(0, 0, Direction.RIGHT, 3));
	
		// Move vertical block at(0,3) to (2,3) , to make it in horizontal path of target block(2,2) 
		assertTrue(board.getBlockAt(0, 3) instanceof VerticalBlock);
		assertTrue(board.moveBlock(0, 3, Direction.DOWN, 2));
		assertTrue(board.getBlockAt(2, 3) instanceof VerticalBlock);
		
		// Move vertical block at(3,1) to (2,1) , to make it in horizontal path of target block(2,2) 
		assertTrue(board.getBlockAt(3, 1) instanceof VerticalBlock);
		assertTrue(board.moveBlock(3, 1, Direction.UP, 1));
		assertTrue(board.getBlockAt(2, 1) instanceof VerticalBlock);
		
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertFalse(board.moveBlock(2, 2, Direction.LEFT, 1));
		assertFalse(board.moveBlock(2, 2, Direction.LEFT, 2));
	}
	
	/**
	 * test to move vertical blocks with a block in the way(invalid move).
	 */
	@Test
	public void testMoveVBlocksWithaBlockInTheWay() {
		// Move target block at(2,2) to (2,1) , to make it in vertical path of another block(3,1) 
		assertTrue(board.getBlockAt(2, 2) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 2, Direction.LEFT, 1));
		assertTrue(board.getBlockAt(2, 1) instanceof TargetBlock);
		
		assertTrue(board.getBlockAt(3, 1) instanceof VerticalBlock);
		assertFalse(board.moveBlock(3, 1, Direction.UP, 1));
		assertFalse(board.moveBlock(3, 1, Direction.UP, 2));
	
		// Move target block at(2,1) to (2,3) , to make it in vertical path of another block(1,3) 
		assertTrue(board.getBlockAt(2, 1) instanceof TargetBlock);
		assertTrue(board.moveBlock(2, 1, Direction.RIGHT, 2));
		assertTrue(board.getBlockAt(2, 3) instanceof TargetBlock);
		
		assertTrue(board.getBlockAt(1, 3) instanceof VerticalBlock);
		assertFalse(board.moveBlock(1, 3, Direction.DOWN, 2));
		assertFalse(board.moveBlock(1, 3, Direction.DOWN, 1));
	}
}
