package uk.danishcake.shokorocket.animation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import uk.danishcake.shokorocket.simulation.Direction;
import uk.danishcake.shokorocket.simulation.SquareType;
import uk.danishcake.shokorocket.simulation.Vector2i;
import uk.danishcake.shokorocket.simulation.Walker;
import uk.danishcake.shokorocket.simulation.World;

/**
 * Encapsulates methods to draw a world
 * @author Edward
 *
 */
public class GameDrawer {
	private Bitmap mWorldBitmap = null;
	private boolean mAnimationsLoaded = false;
	private EnumMap<Direction, Animation> mMouseAnimations = new EnumMap<Direction, Animation>(Direction.class);
	private EnumMap<Direction, Animation> mCatAnimations = new EnumMap<Direction, Animation>(Direction.class);
	private Animation mRocketAnimation = null;
	private Animation mHoleAnimation = null;
	private Animation mRingAnimation = null;
	private EnumMap<Direction, Animation> mFullArrowAnimations = new EnumMap<Direction, Animation>(Direction.class);
	private EnumMap<Direction, Animation> mHalfArrowAnimations = new EnumMap<Direction, Animation>(Direction.class);
	private Bitmap mTileA = null;
	private Bitmap mTileB = null;
	private Animation mNorthWall = null;
	private Animation mWestWall = null;
	
	
	private int mGridSize = 32;
	
	private int mDrawOffsetX = 0;
	private int mDrawOffsetY = 0;
	
	/**
	 * Changes the position that the game is drawn at. Units are pixels
	 * @param x
	 * @param y
	 */
	public void setDrawOffset(int x, int y) {
		mDrawOffsetX = x;
		mDrawOffsetY = y;
	}
	
	/**
	 * Creates the background texture
	 * @param world The world to create the background texture from
	 */
	public void CreateBackground(World world)
	{
		//Create background image
		if(world != null)
		{
			mWorldBitmap = Bitmap.createBitmap(world.getWidth() * mGridSize, world.getHeight() * mGridSize, Bitmap.Config.ARGB_8888);
			Canvas world_canvas = new Canvas(mWorldBitmap);
			
			for(int y = 0; y < world.getHeight(); y++)
			{
				boolean use_tile_a = (y % 2 == 0);
				for(int x = 0; x < world.getWidth(); x++)	
				{
					if(use_tile_a)
						world_canvas.drawBitmap(mTileA, x * mGridSize, y * mGridSize, null);
					else
						world_canvas.drawBitmap(mTileB, x * mGridSize, y * mGridSize, null);
					use_tile_a = !use_tile_a;
				}
			}
			for(int y = 0; y < world.getHeight(); y++)
			{
				for(int x = 0; x < world.getWidth(); x++)
				{
					if(world.getWest(x, y))
					{
						mWestWall.DrawCurrentFrame(world_canvas, x * mGridSize, y * mGridSize);
						if(x == 0)
							mWestWall.DrawCurrentFrame(world_canvas, world_canvas.getWidth() - mWestWall.getCurrentFrame().getWidth(), y * mGridSize);
					}
					if(world.getNorth(x, y))
					{
						mNorthWall.DrawCurrentFrame(world_canvas, x * mGridSize, y * mGridSize);
						if(y == 0)
							mNorthWall.DrawCurrentFrame(world_canvas, x * mGridSize, world_canvas.getHeight() - mNorthWall.getCurrentFrame().getHeight());
					}
				}
			}
			
		} else //Destroy background if passed null
		{
			mWorldBitmap = null;
		}		
	}
	
	/**
	 * Loads any unloaded textures
	 * @param context The context to obtain animations from
	 */
	public void Setup(Context context, int gridSize)
	{
		mGridSize = gridSize;
		float scale = ((float)mGridSize) / 32.0f; 

		//Load unloaded animations
		if(context != null && !mAnimationsLoaded)
		{
			try
			{
				Map<String, Animation> mouse_animations = Animation.GetAnimations(context, "Animations/Game/Mouse_ShokoWhite.animation", scale);			
				mMouseAnimations.put(Direction.North, mouse_animations.get("North"));
				mMouseAnimations.put(Direction.South, mouse_animations.get("South"));
				mMouseAnimations.put(Direction.East, mouse_animations.get("East"));
				mMouseAnimations.put(Direction.West, mouse_animations.get("West"));
				mMouseAnimations.put(Direction.Invalid, mouse_animations.get("Stopped"));
				//TODO rescue, and death of mouse
				
				Map<String, Animation> cat_animations = Animation.GetAnimations(context, "Animations/Game/KapuKapu.animation", scale);
				mCatAnimations.put(Direction.North, cat_animations.get("North"));
				mCatAnimations.put(Direction.South, cat_animations.get("South"));
				mCatAnimations.put(Direction.East, cat_animations.get("East"));
				mCatAnimations.put(Direction.West, cat_animations.get("West"));
				mCatAnimations.put(Direction.Invalid, cat_animations.get("Stopped"));
				//TODO death of cat
				
				Map<String, Animation> arrow_animations = Animation.GetAnimations(context, "Animations/Game/Arrows.animation", scale);
				mFullArrowAnimations.put(Direction.North, arrow_animations.get("North"));
				mFullArrowAnimations.put(Direction.South, arrow_animations.get("South"));
				mFullArrowAnimations.put(Direction.East, arrow_animations.get("East"));
				mFullArrowAnimations.put(Direction.West, arrow_animations.get("West"));
				mFullArrowAnimations.put(Direction.Invalid, arrow_animations.get("Stopped"));
				
				Map<String, Animation> half_arrow_animations = Animation.GetAnimations(context, "Animations/Game/HalfArrows.animation", scale);
				mHalfArrowAnimations.put(Direction.North, half_arrow_animations.get("North"));
				mHalfArrowAnimations.put(Direction.South, half_arrow_animations.get("South"));
				mHalfArrowAnimations.put(Direction.East, half_arrow_animations.get("East"));
				mHalfArrowAnimations.put(Direction.West, half_arrow_animations.get("West"));
				mHalfArrowAnimations.put(Direction.Invalid, half_arrow_animations.get("Stopped"));
				
				Map<String, Animation> rocket_animations = Animation.GetAnimations(context, "Animations/Game/Rocket.animation", scale);
				mRocketAnimation = rocket_animations.get("Normal");
				
				Map<String, Animation> hole_animations = Animation.GetAnimations(context, "Animations/Game/Hole.animation", scale);
				mHoleAnimation = hole_animations.get("All");				
				
				Map<String, Animation> ring_animations = Animation.GetAnimations(context, "Animations/Game/Ring.animation", scale);
				mRingAnimation = ring_animations.get("All");
				
				Map<String, Animation> wall_animations = Animation.GetAnimations(context, "Animations/Game/Walls.animation", scale);
				mNorthWall = wall_animations.get("Horizontal");
				mWestWall = wall_animations.get("Vertical");
				
				Bitmap rawTileA = BitmapFactory.decodeStream(context.getAssets().open("Bitmaps/Game/TileA.png"));
				mTileA = Bitmap.createScaledBitmap(rawTileA, mGridSize, mGridSize, true);
				Bitmap rawTileB = BitmapFactory.decodeStream(context.getAssets().open("Bitmaps/Game/TileB.png"));
				mTileB = Bitmap.createScaledBitmap(rawTileB, mGridSize, mGridSize, true);
				
				mAnimationsLoaded = true;
			} catch(IOException ex)
			{
				//TODO log or something
			}
		}
	}
	/**
	 * Draws the world to the canvas
	 * @param canvas
	 * @param world
	 */
	public void Draw(Canvas canvas, World world)
	{
		if(mWorldBitmap != null)
			canvas.drawBitmap(mWorldBitmap, mDrawOffsetX, mDrawOffsetY, null);
		ArrayList<Walker> mice = world.getLiveMice();
		ArrayList<Walker> cats = world.getLiveCats();
		for (Walker walker : mice) {
			Vector2i position = walker.getPosition();
			int x = position.x * mGridSize + mDrawOffsetX + (mGridSize * walker.getFraction() / walker.FractionReset);
			int y = position.y * mGridSize + mDrawOffsetY + (mGridSize * walker.getFraction() / walker.FractionReset);
			Animation animation = mMouseAnimations.get(walker.getDirection());
			if(animation != null) 
				animation.DrawCurrentFrame(canvas, x, y);
		}
		for (Walker walker : cats) {
			Vector2i position = walker.getPosition();
			int x = position.x * mGridSize + mDrawOffsetX + (mGridSize * walker.getFraction() / walker.FractionReset);
			int y = position.y * mGridSize + mDrawOffsetY + (mGridSize * walker.getFraction() / walker.FractionReset);
			Animation animation = mCatAnimations.get(walker.getDirection());
			if(animation != null) 
				animation.DrawCurrentFrame(canvas, x, y);		
		}
	}
	
	/**
	 * Update animation
	 * @param timespan The timespan in milliseconds
	 */
	public void Tick(int timespan)
	{
		for (Animation animation : mMouseAnimations.values()) {
			animation.Tick(timespan);
		}
		for (Animation animation : mCatAnimations.values()) {
			animation.Tick(timespan);
		}
	}
}
