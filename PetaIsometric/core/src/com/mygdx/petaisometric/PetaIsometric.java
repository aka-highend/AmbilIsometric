package com.mygdx.petaisometric;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class PetaIsometric implements ApplicationListener, InputProcessor {
	private Matrix4 			isoTransform = null;
	private Matrix4				invIsotransform = null;
	private Matrix4				id = null;
	private SpriteBatch			spriteBatch = null;
	private int[][]				map = null;
	private Texture				textureTileset = null;
	private TextureRegion[]		tileSet = null;
	private OrthographicCamera	cam = null;
	private float				tileWidth = 1.0f;
	private float				tileHeight = .5f;
	private Vector3				touch = null;

	private int 				pickedTileX = -1, pickedTileY = -1;

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touch.set(screenX, screenY, 0);
		cam.unproject(touch);
		touch.mul(invIsotransform);

		pickedTileX = (int)touch.x;
		pickedTileY = (int)touch.y;

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void create() {

		Gdx.input.setInputProcessor(this);	//register this class as input processor



		GL20 gl = Gdx.graphics.getGL20();
		gl.glEnable(GL20.GL_BLEND);
		gl.glEnable(GL20.GL_TEXTURE_2D);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch = new SpriteBatch();

		//load the tileset
		textureTileset = new Texture("data/tileset.png");
		textureTileset.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		tileSet = new TextureRegion[4];
		for(int x=0;x<4;x++){
			tileSet[x] = new TextureRegion(textureTileset, x*64, 0, 64, 32);
		}

		//creat a 10x10 isometric map
		map = new int[][]{
				{0, 0, 0 ,0, 0, 0, 0, 0, 0, 0},
				{0, 1, 1 ,1, 1, 1, 1, 1, 1, 0},
				{0, 1, 2 ,2, 0, 0, 0, 0, 1, 0},
				{0, 1, 2 ,2, 0, 0, 0, 0, 1, 0},
				{0, 1, 0 ,0, 0, 0, 0, 0, 1, 0},
				{0, 1, 0 ,0, 0, 0, 0, 0, 1, 0},
				{0, 1, 0 ,0, 0, 0, 0, 0, 1, 0},
				{0, 1, 0 ,0, 0, 0, 0, 0, 1, 0},
				{0, 1, 1 ,1, 1, 1, 1, 1, 1, 0},
				{0, 0, 0 ,0, 0, 0, 0, 0, 0, 0}
		};

		id = new Matrix4();
		id.idt();

		//create the isometric transform
		isoTransform = new Matrix4();
		isoTransform.idt();
		isoTransform.translate(0.0f, 0.25f, 0.0f);
		isoTransform.scale((float)(Math.sqrt(2.0) / 2.0), (float)(Math.sqrt(2.0) / 4.0), 1.0f);
		isoTransform.rotate(0.0f, 0.0f, 1.0f, -45.0f);




		//... and the inverse matrix
		invIsotransform = new Matrix4(isoTransform);
		invIsotransform.inv();

		//touch vector
		touch = new Vector3();

	}

	@Override
	public void resize(int width, int height) {

		//the cam will show 10 tiles
		float camWidth = tileWidth * 10.0f;

		//for the height, we just maintain the aspect ratio
		float camHeight = camWidth * ((float)height / (float)width);

		cam = new OrthographicCamera(camWidth, camHeight);
		cam.position.set(camWidth / 2.0f, 0, 0);
		cam.update();

	}

	@Override
	public void render() {

		GL20 gl = Gdx.graphics.getGL20();
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.setTransformMatrix(id);

		spriteBatch.begin();
		renderMap();
		spriteBatch.end();


		spriteBatch.setTransformMatrix(isoTransform);
		spriteBatch.begin();
		spriteBatch.draw(tileSet[3], 0.0f, 0.0f, 1.0f, 1.0f);
		spriteBatch.end();


	}

	private void renderMap(){
		for (int x = 0; x < 10; x++){
			for(int y = 10-1; y >= 0; y--){

				float x_pos = (x * tileWidth /2.0f ) + (y * tileWidth / 2.0f);
				float y_pos = - (x * tileHeight / 2.0f) + (y * tileHeight /2.0f);

				if(x==pickedTileX && y==pickedTileY)
					spriteBatch.setColor(1.0f, 0.0f, 0.0f, 1.0f);
				else
					spriteBatch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
				spriteBatch.draw(tileSet[map[x][y]], x_pos, y_pos, tileWidth, tileHeight);

			}
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		GL20 gl = Gdx.graphics.getGL20();
		gl.glDisable(GL20.GL_BLEND);
		gl.glDisable(GL20.GL_TEXTURE_2D);

	}
}
