package uk.danishcake.shokorocket.moding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.danishcake.shokorocket.R;
import uk.danishcake.shokorocket.animation.Animation;
import uk.danishcake.shokorocket.simulation.SPWorld;
import uk.danishcake.shokorocket.simulation.Vector2i;
import uk.danishcake.shokorocket.sound.MusicManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Interpolator.Result;
import android.graphics.Paint.Align;
import android.text.TextPaint;

public class ModeIntro extends Mode {
	private class Rocket
	{
		public Vector2i position;
		public int age;
		public String text;
	}
	private class Smoke
	{
		public Vector2i position;
		public Vector2i velocity;
		public int age;
	}
	
	Animation mLogoLeft;
	Animation mLogoRight;
	Animation mCreditRocket;
	Animation mSmokeAnimation;
	Interpolator mLogoInterpolator = null;
	Matrix mMatrix = new Matrix();
	boolean mFirstRun = false;
	private List<Rocket> mRockets = new ArrayList<Rocket>();
	private List<Smoke> mPlumes = new ArrayList<Smoke>();
	private int mSpawnTimer = 0;
	private Random mRandom = new Random();
	private Progress mProgress = null;
	private ArrayList<String> mUserLevels = null;
	private int mUserLevelIndex = 0;
	private TextPaint mTextPaint;

	@Override
	public void Setup(Context context) {
		super.Setup(context);
		
		MusicManager.PlayMenuMusic();

		mTextPaint = new TextPaint();
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTextPaint.setTextSize(mFontSize);
		mTextPaint.setColor(Color.BLACK);

		mProgress = new Progress(context);
		ArrayList<String> cont_levels = mProgress.getUserLevels();
		mUserLevels = new ArrayList<String>();
		mUserLevels.add(context.getString(R.string.intro_credits));
		mUserLevels.add("by Edward Woolhouse");
		mUserLevels.add("");
		mUserLevels.add(context.getString(R.string.intro_contributors));
		mUserLevels.add("");
		
		for (String level: cont_levels) {
			try {
				SPWorld temp_world = mProgress.getWorld(level);
				if(!mUserLevels.contains(temp_world.getAuthor()))
					mUserLevels.add(temp_world.getAuthor());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mUserLevels.add(context.getString(R.string.intro_thanks_guys));

		mFirstRun = Progress.IsFirstRun(context);
		try
		{
			Map<String, Animation> animations = Animation.GetAnimations(context, "Animations/Intro/Splash.animation"); 
			mLogoLeft = animations.get("Left");
			mLogoRight = animations.get("Right");
			mCreditRocket = animations.get("CreditRocket");
			mSmokeAnimation = animations.get("Smoke");
		} catch(IOException io_ex)
		{
			//TODO log it!
		}
	}
	
	@Override
	public Mode Teardown() {
		return super.Teardown();
	}
	
	@Override
	public ModeAction Tick(int timespan) {
		int rocket_speed = 300 * 800 / mScreenHeight; //300px/s 
		mLogoLeft.Tick(timespan);
		mLogoRight.Tick(timespan);
		mCreditRocket.Tick(timespan);
		
		if(mAge > 4000)
		{
			mSpawnTimer += timespan;
			if(mSpawnTimer > 1000 && mUserLevelIndex < mUserLevels.size())
			{
				if(!mUserLevels.get(mUserLevelIndex).equals(""))
				{
					Rocket rocket = new Rocket();
					rocket.position = new Vector2i(mRandom.nextInt(mScreenWidth-128)+32, (mScreenHeight + 150 + mRandom.nextInt(50)) * 1000);
					rocket.age = 0;
					rocket.text = mUserLevels.get(mUserLevelIndex);
					mRockets.add(rocket);
				}
				mUserLevelIndex++;
				mSpawnTimer = 0;
			}
		}
		Iterator<Rocket> v_it = mRockets.iterator();
		while(v_it.hasNext()){
			Rocket rocket = v_it.next();
			int ltv_age= rocket.age;
			rocket.age += timespan;
			rocket.position.y -= rocket_speed * timespan;
			if(rocket.position.y < -1000000)
				v_it.remove();
			final int smoke_offset = 100000;
			if(rocket.age / 80 > ltv_age / 80)
			{
				{
					Smoke s = new Smoke();
					s.position = new Vector2i(rocket.position.x * 1000 + 10000, rocket.position.y + smoke_offset);
					s.velocity = new Vector2i(-7, -rocket_speed);
					mPlumes.add(s);
				}
				{
					Smoke s = new Smoke();
					s.position = new Vector2i(rocket.position.x * 1000, rocket.position.y + smoke_offset);
					s.velocity = new Vector2i(0, -rocket_speed);
					mPlumes.add(s);
				}
				{
					Smoke s = new Smoke();
					s.position = new Vector2i(rocket.position.x * 1000 - 10000, rocket.position.y + smoke_offset);
					s.velocity = new Vector2i(7, -rocket_speed);
					mPlumes.add(s);
				}
			}
		}
		
		Iterator<Smoke> s_it = mPlumes.iterator();
		while(s_it.hasNext()){
			Smoke smoke = s_it.next();
			smoke.position.y += timespan * smoke.velocity.y;
			smoke.position.x += timespan * smoke.velocity.x;
			
			smoke.age += timespan;
			if(smoke.age >= 1000)
				s_it.remove();
			if(smoke.age > 125 && smoke.velocity.y < 0)
				smoke.velocity.y = 0;
		}

		return super.Tick(timespan);
	}
	
	@Override
	public void Redraw(Canvas canvas) {
		if(mLogoInterpolator == null)
		{
			mLogoInterpolator = new Interpolator(3, 5);
			mLogoInterpolator.setKeyFrame(0, 0, new float[] {-250, 100, 0});
			mLogoInterpolator.setKeyFrame(1, 500, new float[]{-250, 100, 0});
			mLogoInterpolator.setKeyFrame(2, 1500, new float[]{canvas.getWidth() + 1, canvas.getHeight() + 1, 45});
			mLogoInterpolator.setKeyFrame(3, 2500, new float[]{canvas.getWidth() + 1, canvas.getHeight() + 1, 45}, new float[]{1.0f, 0.0f, 0.0f, 0.0f});
			mLogoInterpolator.setKeyFrame(4, 3500, new float[]{canvas.getWidth() / 2 - 100, canvas.getHeight() / 2 - 50, 0});
		}
		
		Paint fill_paint = new Paint();
		fill_paint.setARGB(255, 0, 255, 255);
		canvas.drawPaint(fill_paint);
		//canvas.drawARGB(255,0,255,255);
		float[] interpolated = new float[3];
		Result interp_result = mLogoInterpolator.timeToValues(mAge, interpolated);
		float x = interpolated[0];
		float y = interpolated[1];
		float angle = interpolated[2];
		
		mMatrix.reset();
		if(interp_result != Result.FREEZE_END)
			mMatrix.setRotate(angle, 100, 50);
		else
			mMatrix.setRotate(-(mAge - 3500) / 30, 100, 50);

		mMatrix.postTranslate(x, y);

		for (Rocket r : mRockets) {
			canvas.drawBitmap(mCreditRocket.getCurrentFrame(), r.position.x, r.position.y / 1000, null);
			canvas.drawText(r.text, r.position.x+32, r.position.y / 1000, mTextPaint);
		}
		
		for (Smoke s : mPlumes) {
			canvas.drawBitmap(mSmokeAnimation.getFrameByTime(s.age), s.position.x/1000, s.position.y/1000, null);
		}
		
		if(mAge < 2500)
		{
			canvas.drawBitmap(mLogoRight.getCurrentFrame(), mMatrix, null);
		} else
		{
			canvas.drawBitmap(mLogoLeft.getCurrentFrame(), mMatrix, null);
		}

		super.Redraw(canvas);
	}
	
	@Override
	public void handleTap(int x, int y) {
		if(mFirstRun)
			mPendMode = new ModeTutorial(new ModeMenu(mProgress));
		else
			mPendMode = new ModeMenu(mProgress);
	}
	
	@Override
	public boolean getBackgroundDrawn() {
		return false;
	}
}

