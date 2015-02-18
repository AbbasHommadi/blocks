package pt314.blocks.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	HorizontalBlockTest.class,
	VerticalBlockTest.class,
	TargetBlockTest.class,
	GameBoardTest.class
})

public class AllTests {}
