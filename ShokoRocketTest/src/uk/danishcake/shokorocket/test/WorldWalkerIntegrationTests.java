package uk.danishcake.shokorocket.test;

import java.security.InvalidParameterException;

import junit.framework.TestCase;
import uk.danishcake.shokorocket.simulation.Direction;
import uk.danishcake.shokorocket.simulation.SquareType;
import uk.danishcake.shokorocket.simulation.Vector2i;
import uk.danishcake.shokorocket.simulation.Walker;
import uk.danishcake.shokorocket.simulation.SPWorld;
import uk.danishcake.shokorocket.simulation.SPWorld.WorldState;

public class WorldWalkerIntegrationTests extends TestCase {
	
	public void testWalkerWorldBasics()
	{
		{//Check getter and setter of world work
			SPWorld world = new SPWorld();
			Walker walker = new Walker();
			walker.setWorld(world);
			
			assertEquals(world, walker.getWorld());
		}
		
		{//Check it can't be added to a world outside of bounds - should throw in this case
			SPWorld world = new SPWorld();
			Walker walker = new Walker();
			walker.setPosition(new Vector2i(15, 0));
			
			boolean throws_exception = false;
			try
			{
			walker.setWorld(world);
			} catch(InvalidParameterException exception)
			{
				throws_exception = true;
			}
			
			assertTrue(throws_exception);
		}
	}
	
	public void testWalkerTurns() 
	{
		Walker walker = new Walker();
		SPWorld world = new SPWorld();
		walker.setWorld(world);
		
		/* Test corner turn
		 * ========
		 * |    <--
		 * ||
		 * |v
		 */
		walker.setPosition(new Vector2i(2, 0));
		walker.setDirection(Direction.West);
		
		walker.Advance(2000);
		assertEquals(Direction.South, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test corner turn
		 * ========
		 * |    -->
		 * |^
		 * ||
		 */
		walker.setPosition(new Vector2i(0, 2));
		walker.setDirection(Direction.North);
		
		walker.Advance(2000);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test corner turn
		 * ||
		 * |v
		 * |   -->
		 * =======
		 */
		walker.setPosition(new Vector2i(0, 6));
		walker.setDirection(Direction.South);
		
		walker.Advance(2000);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(8, walker.getPosition().y);
		
		/* Test corner turn
		 * |^
		 * ||
		 * |   <--
		 * =======
		 */
		walker.setPosition(new Vector2i(2, 8));
		walker.setDirection(Direction.West);
		
		walker.Advance(2000);
		assertEquals(Direction.North, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(8, walker.getPosition().y);
		
		/* Test corner turn
		 * ========
		 * -->    |
		 *       ||
		 *       v|
		 */
		walker.setPosition(new Vector2i(9, 0));
		walker.setDirection(Direction.East);
		
		walker.Advance(2000);
		assertEquals(Direction.South, walker.getDirection());
		assertEquals(11, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test corner turn
		 * ========
		 * <--    |
		 *       ^|
		 *       ||
		 */
		walker.setPosition(new Vector2i(11, 2));
		walker.setDirection(Direction.North);
		
		walker.Advance(2000);
		assertEquals(Direction.West, walker.getDirection());
		assertEquals(11, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test corner turn
		 *      ||
		 *      v|
		 * <--   |
		 * =======
		 */
		walker.setPosition(new Vector2i(11, 6));
		walker.setDirection(Direction.South);
		
		walker.Advance(2000);
		assertEquals(Direction.West, walker.getDirection());
		assertEquals(11, walker.getPosition().x);
		assertEquals(8, walker.getPosition().y);
		
		/* Test corner turn
		 *      ^|
		 *      ||
		 * -->   |
		 * =======
		 */
		walker.setPosition(new Vector2i(9, 8));
		walker.setDirection(Direction.East);
		
		walker.Advance(2000);
		assertEquals(Direction.North, walker.getDirection());
		assertEquals(11, walker.getPosition().x);
		assertEquals(8, walker.getPosition().y);
		
		/* Test dead end turn
		 * || ^|
		 * |v ||
		 * |---|
		 * =====
		 */
		
		world.setEast(0, 8, true);
		walker.setPosition(new Vector2i(0, 6));
		walker.setDirection(Direction.South);
		
		walker.Advance(2000);
		assertEquals(Direction.North, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(8, walker.getPosition().y);		
		
		/* Test dead end turn
		 * =====
		 * |---|
		 * |^ ||
		 * || v|
		 */

		world.setEast(0, 0, true);
		walker.setPosition(new Vector2i(0, 2));
		walker.setDirection(Direction.North);
		
		walker.Advance(2000);
		assertEquals(Direction.South, walker.getDirection());
		assertEquals(0, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test dead end turn
		 * =====
		 * | <--
		 * | -->
		 * =====
		 */
		
		world.setSouth(1, 0, true);
		walker.setPosition(new Vector2i(3, 0));
		walker.setDirection(Direction.West);
		
		walker.Advance(2000);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(1, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
		
		/* Test dead end turn
		 * =====
		 * <-- |
		 * --> |
		 * =====
		 */
		world.setSouth(11, 0, true);
		walker.setPosition(new Vector2i(9, 0));
		walker.setDirection(Direction.East);
		
		walker.Advance(2000);
		assertEquals(Direction.West, walker.getDirection());
		assertEquals(11, walker.getPosition().x);
		assertEquals(0, walker.getPosition().y);
	}
	
	void testWalkersTurnWhenAdded()
	{
		/* Tests that a walker that is totally surrounded will stop */
		SPWorld world = new SPWorld();
		world.setEast(0, 0, true);
		world.setNorth(0, 0, true);
		world.setWest(0, 0, true);
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(0,0));
		walker.setDirection(Direction.East);
		walker.setWorld(world);
		
		assertEquals(Direction.South, walker.getDirection());
	}
	
	public void testTrappedWalker()
	{
		/* Tests that a walker that is totally surrounded will stop */
		SPWorld world = new SPWorld();
		world.setEast(0, 0, true);
		world.setSouth(0, 0, true);
		world.setNorth(0, 0, true);
		world.setWest(0, 0, true);
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(0,0));
		walker.setWorld(world);
		
		
		walker.Advance(500);
		
		assertEquals(Direction.Invalid, walker.getDirection());
	}
	
	public void testAddMiceToWorld()
	{
		SPWorld world = new SPWorld();
		assertEquals(0, world.getLiveMice().size());
		
		Walker walker = new Walker();
		world.addMouse(walker);
		assertEquals(world, walker.getWorld());
		assertEquals(1, world.getLiveMice().size());
		assertTrue(world.getLiveMice().contains(walker));	
	}

	public void testAddCatsToWorld()
	{
		SPWorld world = new SPWorld();
		assertEquals(0, world.getLiveCats().size());
		
		Walker walker = new Walker();
		world.addCat(walker);
		assertEquals(world, walker.getWorld());
		assertEquals(1, world.getLiveCats().size());
		assertTrue(world.getLiveCats().contains(walker));	
	}
	
	public void testRelativeSpeed()
	{
		//Cats move at 2/3rds speed of a mouse
		SPWorld world = new SPWorld();
		Walker mouse = new Walker();
		Walker cat = new Walker();
		
		mouse.setPosition(new Vector2i(0, 0));
		mouse.setDirection(Direction.East);
		
		cat.setPosition(new Vector2i(0, 1));
		cat.setDirection(Direction.East);
		
		world.addCat(cat);
		world.addMouse(mouse);
		
		world.Tick(3100);
		
		assertEquals(3, mouse.getPosition().x);
		assertEquals(2, cat.getPosition().x);
	}
	
	public void testMiceAffectedByArrows() 
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(0, 2));
		walker.setDirection(Direction.East);
		world.addMouse(walker);
		
		world.setArrow(2, 2, Direction.South);
		world.setArrow(2, 6, Direction.East);
		world.setArrow(6, 6, Direction.North);
		world.setArrow(6, 2, Direction.West);
		
		world.Tick(3000);
		assertEquals(2, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.South, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(3, walker.getPosition().x);
		assertEquals(6, walker.getPosition().y);
		assertEquals(Direction.East, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(6, walker.getPosition().x);
		assertEquals(5, walker.getPosition().y);
		assertEquals(Direction.North, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(5, walker.getPosition().x);
		assertEquals(2, walker.getPosition().y);
		assertEquals(Direction.West, walker.getDirection());
	}
	
	public void testCatsAffectedByArrows()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(0, 2));
		walker.setDirection(Direction.East);
		world.addCat(walker);
		walker.setSpeed(3000); //Force speed to mouse speed to make unit test simpler
		
		world.setArrow(2, 2, Direction.South);
		world.setArrow(2, 6, Direction.East);
		world.setArrow(6, 6, Direction.North);
		world.setArrow(6, 2, Direction.West);
		
		world.Tick(3000);
		assertEquals(2, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.South, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(3, walker.getPosition().x);
		assertEquals(6, walker.getPosition().y);
		assertEquals(Direction.East, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(6, walker.getPosition().x);
		assertEquals(5, walker.getPosition().y);
		assertEquals(Direction.North, walker.getDirection());
		
		world.Tick(4000);
		assertEquals(5, walker.getPosition().x);
		assertEquals(2, walker.getPosition().y);
		assertEquals(Direction.West, walker.getDirection());		
	}
	
	public void testCatsDiminishArrows()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(0, 2));
		walker.setDirection(Direction.East);
		world.addCat(walker);
		walker.setSpeed(3000); //Force to mouse speed to simplify
		
		world.setArrow(2, 2, Direction.South);
		world.setArrow(2, 3, Direction.East);
		world.setArrow(6, 3, Direction.West);
		
		world.Tick(3000);
		
		assertEquals(2, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(SquareType.EastArrow, world.getSpecialSquare(2, 3));
		assertEquals(SquareType.WestArrow, world.getSpecialSquare(6, 3));
		
		world.Tick(4000);
		assertEquals(6, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.West, walker.getDirection());
		assertEquals(SquareType.EastArrow, world.getSpecialSquare(2, 3));
		assertEquals(SquareType.WestHalfArrow, world.getSpecialSquare(6, 3));
		
		world.Tick(4000);
		assertEquals(2, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(SquareType.EastHalfArrow, world.getSpecialSquare(2, 3));
		assertEquals(SquareType.WestHalfArrow, world.getSpecialSquare(6, 3));
		
		world.Tick(4000);
		assertEquals(6, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.West, walker.getDirection());
		assertEquals(SquareType.EastHalfArrow, world.getSpecialSquare(2, 3));
		assertEquals(SquareType.WestDestroyedArrow, world.getSpecialSquare(6, 3));
		
		world.Tick(4000);
		assertEquals(2, walker.getPosition().x);
		assertEquals(3, walker.getPosition().y);
		assertEquals(Direction.East, walker.getDirection());
		assertEquals(SquareType.EastDestroyedArrow, world.getSpecialSquare(2, 3));
		assertEquals(SquareType.WestDestroyedArrow, world.getSpecialSquare(6, 3));
		
		
	}

	//This also tests that timesteps are broken up
	public void testCatsKillMice()
	{
		SPWorld world = new SPWorld();
		Walker mouse = new Walker();
		Walker cat = new Walker();
		
		mouse.setPosition(new Vector2i(0, 0));
		mouse.setDirection(Direction.East);
		cat.setPosition(new Vector2i(10, 0));
		cat.setDirection(Direction.West);
		
		world.addCat(cat);
		world.addMouse(mouse);
		
		world.Tick(10000);
		assertEquals(WorldState.Failed, world.getWorldState());
	}
	
	public void testHolesKillMice()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(2, 2));
		walker.setDirection(Direction.East);
		world.addMouse(walker);
		
		world.setHole(4, 2, true);
		
		world.Tick(3000);
		
		assertEquals(0, world.getLiveMice().size());
		assertEquals(1, world.getDeadMice().size());
	}
	
	public void testHolesKillCats()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(2, 2));
		walker.setDirection(Direction.East);
		world.addCat(walker);
		
		world.setHole(4, 2, true);
		
		world.Tick(3000);
		
		assertEquals(0, world.getLiveCats().size());
		assertEquals(1, world.getDeadCats().size());		
	}
	
	public void testRocketsRescueMice()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(2, 2));
		walker.setDirection(Direction.East);
		world.addMouse(walker);
		
		world.setRocket(4, 2, true);
		
		world.Tick(3000);
		
		assertEquals(0, world.getLiveMice().size());
		assertEquals(0, world.getDeadMice().size());
		assertEquals(1, world.getRescuedMice().size());
	}
	
	public void testCatsKillRockets()
	{
		SPWorld world = new SPWorld();
		Walker walker = new Walker();
		walker.setPosition(new Vector2i(2, 2));
		walker.setDirection(Direction.East);
		world.addCat(walker);
		
		world.setRocket(4, 2, true);
		
		
		assertEquals(SPWorld.WorldState.OK, world.getWorldState());
		
		world.Tick(3000);
		
		assertEquals(0, world.getLiveCats().size());
		assertEquals(1, world.getDeadCats().size());
		
		//Now for world state
		assertEquals(SPWorld.WorldState.Failed, world.getWorldState());
	}
	
	public void testWrappingCollisions()
	{
		SPWorld world = new SPWorld();
		Walker mouse = new Walker();
		Walker cat = new Walker();
		
		world.setEast(11, 0, false);
		
		mouse.setPosition(new Vector2i(0,0));
		mouse.setDirection(Direction.West);
		
		cat.setPosition(new Vector2i(11, 0));
		cat.setDirection(Direction.East);
		
		world.addCat(cat);
		world.addMouse(mouse);
		
		
		world.Tick(1000);
		
		assertEquals(WorldState.Failed, world.getWorldState());
	}
	
	public void testWorldReset()
	{
		SPWorld world = new SPWorld();
		Walker mouse_to_die = new Walker();
		mouse_to_die.setPosition(new Vector2i(8,0));
		mouse_to_die.setDirection(Direction.East);
		
		Walker mouse_to_rescue = new Walker();
		mouse_to_rescue.setPosition(new Vector2i(8, 3));
		mouse_to_rescue.setDirection(Direction.East);
		
		Walker cat_to_die = new Walker();
		cat_to_die.setPosition(new Vector2i(10, 6));
		cat_to_die.setDirection(Direction.East);
		
		world.addMouse(mouse_to_die);
		world.addMouse(mouse_to_rescue);
		world.addCat(cat_to_die);
		
		
		world.setHole(11, 1, true);
		world.setRocket(11, 4, true);
		world.setHole(11, 7, true);
		
		world.Tick(6000);
		
		assertEquals(1, world.getRescuedMice().size());
		assertEquals(1, world.getDeadMice().size());
		assertEquals(1, world.getDeadCats().size());
		
		assertEquals(Direction.South, mouse_to_rescue.getDirection());
		assertEquals(Direction.South, mouse_to_die.getDirection());
		assertEquals(Direction.South, cat_to_die.getDirection());
		
		assertEquals(WorldState.Failed, world.getWorldState());
		world.Reset();

		assertEquals(0, world.getRescuedMice().size());
		assertEquals(0, world.getDeadMice().size());
		assertEquals(0, world.getDeadCats().size());
		
		assertEquals(Direction.East, mouse_to_rescue.getDirection());
		assertEquals(Direction.East, mouse_to_die.getDirection());
		assertEquals(Direction.East, cat_to_die.getDirection());
		assertEquals(WorldState.OK, world.getWorldState());
	}
	
	public void testWalkerCollisionWhenAligned()
	{
		//BUG: When a mouse is running away from a cat
		// C>M> 
		//It should be safe, but it collides!
		
		SPWorld world = new SPWorld();
		Walker mouse = new Walker();
		mouse.setPosition(new Vector2i(5, 5));
		mouse.setDirection(Direction.East);
		world.addMouse(mouse);
		
		Walker cat = new Walker();
		cat.setDirection(Direction.East);
		cat.setPosition(new Vector2i(4, 5));
		
		world.Tick(2000);
		assertEquals(WorldState.OK, world.getWorldState());
		
	}
}
