package com.kaos.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.Random;

import javax.swing.Renderer;

import sun.rmi.runtime.Log;

//import com.sun.xml.internal.ws.api.pipe.Tube;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	TextureRegion[] birdsAnim;
	//TextureRegion[] birdAnimUp;
	//TextureRegion[] birdAnimDown;
	Circle birdCircle;
	ShapeRenderer shapeRenderer;
	Animation<TextureRegion> flyAnimation;
	TextureRegion currentFrame;
	Texture topTube;
	Texture botTube;
	Texture gameOver;
	BitmapFont font;
	Rectangle[] topTubeRectangle;
	Rectangle[] botTubeRectangle;
	//int flapState =0;
	float stateTime =0.0f;
	float birdY =0f;
	float velocity = 0;
	float gravity=1.5f;
	int gameState=0;
	float gap=220;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity =5;
	int numberOfTube =4;
	float[] tubeX = new float [numberOfTube];
	float[] tubeOffset = new float [numberOfTube];
	float distanceBetweenTube;
	int score=0;
	int scoreTube=0;
	float birdRotation =0f;
	public enum State
	{
		PAUSE,
		RUN,
		RESUME,
		STOPPED
	}
	State state = State.RUN;
	float pixelDensity;
	float phoneWidth;
	float phoneHeight;
	//Color color;
	
	@Override
	public void create () {
		pixelDensity=Gdx.graphics.getDensity();
		phoneWidth=Gdx.graphics.getWidth();
		phoneHeight=Gdx.graphics.getHeight();
		birdCircle= new Circle();
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		//color = new Color();
		background = new Texture("bg.png");
		birds = new Texture[4];
		birdsAnim=new TextureRegion[4];
		birds[0]=new Texture("frame-1.png");
		birds[1]=new Texture("frame-2.png");
		birds[2]=new Texture("frame-3.png");
		birds[3]=new Texture("frame-4.png");
		topTube = new Texture("toptube.png");
		botTube=new Texture("bottomtube.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		gameOver= new Texture("gameOverDone.png");
		//set texture region
		for(int i=0; i<4;i++)
		{
			birdsAnim[i]=new TextureRegion(birds[i], birds[i].getWidth(), birds[i].getHeight());
		}
		//set animation
		flyAnimation=new Animation<TextureRegion>(0.025f, birdsAnim);
		birdY=phoneHeight/2-birds[0].getHeight()/6;
		maxTubeOffset = phoneHeight/2-gap/2-100;
		randomGenerator = new Random();
		distanceBetweenTube=phoneWidth*3/4;
		topTubeRectangle = new Rectangle[numberOfTube];
		botTubeRectangle = new Rectangle[numberOfTube];
		startGame();
	}
	public void startGame()
	{
		birdY=phoneHeight/2-birds[0].getHeight()/6;
		for(int i =0; i<numberOfTube;i++)
		{
			tubeOffset[i] = (randomGenerator.nextFloat()-0.5f)*(phoneHeight-gap-800);
			tubeX[i]=phoneWidth/2-topTube.getWidth()/2+phoneWidth+i*distanceBetweenTube;
			topTubeRectangle[i]=new Rectangle();
			botTubeRectangle[i]=new Rectangle();
		}
	}
	@Override
	public void render () {
		/*if(flapState<3)
			flapState++;
		else flapState=0;*/
		switch (state) {
			case RUN:
			{
				//Gdx.app.log("GameState",String.valueOf(gameState));
				//batch.enableBlending();
				batch.begin();
				//color=batch.getColor();
				//batch.setColor(color.r, color.g, color.b, 1f);
				//draw background first
				batch.draw(background, 0, 0, phoneWidth, phoneHeight);
				if (gameState == 1) {
					if (tubeX[scoreTube] < phoneWidth / 2) {
						score++;
						//Gdx.app.log("Score:", String.valueOf(score));
						if (scoreTube < numberOfTube - 1)
							scoreTube++;
						else scoreTube = 0;
					}
					if (Gdx.input.justTouched()) {
						velocity = -gravity * 15;
						//Gdx.app.log("Velocity", String.valueOf(velocity));
					}
					for (int i = 0; i < numberOfTube; i++) {
						if (tubeX[i] < -topTube.getWidth()) {
							tubeX[i] += numberOfTube * distanceBetweenTube;
							tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (phoneHeight - gap - 800);
						} else {
							tubeX[i] -= tubeVelocity;
						}
						batch.draw(topTube, tubeX[i], phoneHeight / 2 + gap + tubeOffset[i]);
						batch.draw(botTube, tubeX[i], phoneHeight / 2 - gap - botTube.getHeight() + tubeOffset[i]);
						topTubeRectangle[i] = new Rectangle(tubeX[i], phoneHeight/ 2 + gap + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
						botTubeRectangle[i] = new Rectangle(tubeX[i], phoneHeight/ 2 - gap - botTube.getHeight() + tubeOffset[i], botTube.getWidth(), botTube.getHeight());
					}
					if (birdY > 0/*||velocity<0*/) {
						velocity += gravity;
						//Gdx.app.log("Velocity After", String.valueOf(velocity));
						birdY -= velocity;
					} else {
						gameState = 2;
					}
					if (velocity < 21)
						birdRotation = 45f;
					else
						birdRotation = -45f;
				} else if (gameState == 0) {
					if (Gdx.input.justTouched()) {
						//Gdx.app.log("Touched", "True");
						gameState = 1;
					}
				} else if (gameState == 2) {
					//batch.setColor(color.r, color.g, color.b, .3f);
					if (Gdx.input.isTouched()) {
						gameState = 0;
						startGame();
						score = 0;
						scoreTube = 0;
						velocity = 0;
					}
				}
				//Gdx.app.log("GameStateAfter",String.valueOf(gameState));
				//set animation speed
				stateTime += Gdx.graphics.getDeltaTime() * 0.5f;
				currentFrame = flyAnimation.getKeyFrame(stateTime, true);
				//batch.draw(birds[flapState], Gdx.graphics.getWidth()/2-birds[flapState].getWidth()/6, Gdx.graphics.getHeight()/2-birds[flapState].getHeight()/6, birds[flapState].getWidth()/4, birds[flapState].getHeight()/4);
				//draw animation
				//batch.draw(currentFrame, Gdx.graphics.getWidth() / 2 - currentFrame.getRegionWidth() / 6, birdY, currentFrame.getRegionWidth() / 3, currentFrame.getRegionHeight() / 3);
				batch.draw(currentFrame, phoneWidth / 2 - currentFrame.getRegionWidth() / 6, birdY, currentFrame.getRegionWidth() / 6, currentFrame.getRegionHeight() / 6, currentFrame.getRegionWidth() / 3, currentFrame.getRegionHeight() / 3, 1f, 1f, birdRotation);
				font.draw(batch, String.valueOf(score), 100, 200);
				batch.end();
				birdCircle.set(phoneWidth/ 2, birdY + currentFrame.getRegionHeight() / 6, currentFrame.getRegionHeight() / 6.5f);
				//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				//shapeRenderer.setColor(Color.VIOLET);
				//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
				for (int i = 0; i < numberOfTube; i++) {
					//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2+gap+tubeOffset[i], topTube.getWidth(), topTube.getHeight());
					//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2-gap-botTube.getHeight()+tubeOffset[i], botTube.getWidth(), botTube.getHeight());
					if ((Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, botTubeRectangle[i]))) {
						//Gdx.app.log("Colision", "Checked");
						gameState = 2;
					}
				}
				if (gameState == 2)
					state = State.PAUSE;
				break;
		}
			case PAUSE: {
				if (Gdx.input.isTouched())
					state = State.RUN;
				batch.begin();
				batch.draw(gameOver, phoneWidth / 2 - gameOver.getWidth() / 2, phoneHeight / 2 - gameOver.getHeight() / 2);
				batch.end();
				//Gdx.app.log("State:", String.valueOf(state));
				break;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
