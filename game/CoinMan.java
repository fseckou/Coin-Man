package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.Text;
import jdk.nashorn.internal.ir.ForNode;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	// texture is a way of representing images
	Texture background;
	Texture[] man;
	Texture dizzy;
	int manState = 0;
	int pause = 0;
	// physics
	float gravity = 0.2f;
	float velocity = .2f;
	int manY = 0;
	int manX = 0;
	Rectangle manRectangle;
	BitmapFont font;

	//coins and bombs
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	Texture coin;
	int coinCount;
	Random random;

	int score = 0;
	int gameState = 0;
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight()/2;
		manX = Gdx.graphics.getWidth()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		manRectangle = new Rectangle();

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy = new Texture("dizzy-1.png");
	}
	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int) height);
		coinXs.add(Gdx.graphics.getWidth());

	}
	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int) height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		// this will start the game
		batch.begin();
		//everything that's gonna happen between the beginning and end of the game; the order of images matter
		// this will display the background image on the screen
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight() );

	if(gameState == 1){
		// make and display bombs every 250 times
		if(bombCount< 250){
			bombCount++;
		}else{
			bombCount = 0;
			makeBomb();
		}
		bombRectangle.clear();
		for(int i = 0; i<bombXs.size(); i++){
			batch.draw(bomb, bombXs.get(i), bombYs.get(i));
			bombXs.set(i, bombXs.get(i) - 8);
			bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
		}

			// game is being played
			// make and display coins every 100 times
			if(coinCount< 100){
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i = 0; i < coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			// when screen is touched, velocity will be -10 which will add 9.8 to the manY because
			// gravity will be added to velocity; which will cause a subtraction
			// manY - velocity will be added to the velocity

			if(Gdx.input.justTouched()){
				velocity =-10;
			}

			// manState allows me to determine which state the man is in, so I can determine the next step he should be in.
			// In this case, I'm changing  the images, which gives it a running impression
			// pause slows the man down
			if( pause < 8 ){
				pause++;
			}else{
				pause = 0;
				if(manState < 3){
					manState++;
				}else{
					manState = 0;
				}
			}
			// manY - velocity will slowly drop the man to manY = o which is the ground level

			velocity += gravity;
			manY-=velocity;

			if(manY <= 0){
				manY = 0;
			}
	}else if(gameState == 0){
			// waiting for a touch to start the game
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
	}else if(gameState == 2){
			// game over
			if(Gdx.input.justTouched()){
				gameState = 1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinYs.clear();
				coinXs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombRectangle.clear();
				bombXs.clear();
				bombYs.clear();
				bombCount = 0;
			}
		}

		// because we want our man to be in the center, we divide the height and width by 2
		// because our man is too large, to center him properly, we divide his width by 2
		if(gameState == 2){
			batch.draw(dizzy, manX - man[manState].getWidth()/2, manY);
		}else{
			batch.draw(man[manState], manX - man[manState].getWidth()/2, manY);
		}

		manRectangle = new Rectangle(manX - man[manState].getWidth()/2, manY, man[manState].getWidth(), man[manState].getHeight());
// detect if there has been a collision between the man and bomb or man and coin
		for(int i = 0; i < coinRectangle.size(); i++){
			if(Intersector.overlaps(manRectangle, coinRectangle.get(i))){
				score++;

				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for(int i = 0; i<bombRectangle.size(); i++){
			if(Intersector.overlaps(manRectangle, bombRectangle.get(i))){
				gameState = 2;
			}
		}
		font.draw(batch, String.valueOf(score),100, 200 );
		// ending the game
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
