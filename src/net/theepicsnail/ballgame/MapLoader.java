package net.theepicsnail.ballgame;

import java.util.HashMap;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXTiledMapProperty;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.AssetManager;

public class MapLoader {

	private int boxes = 0;
	private int targets = 0;
	private int shots = 0;
	private int splitters = 0;
	private TMXTiledMap map;

	public MapLoader(int level, TextureManager texture, AssetManager assets,
			VertexBufferObjectManager vertex) throws TMXLoadException {
		boxes = 0;
		targets = 0;

		map = (new TMXLoader(assets, texture, vertex))
				.loadFromAsset("tmx/level" + level + ".tmx");

		TMXLayer layer = map.getTMXLayers().get(0);
		for (int row = 0; row < layer.getTileRows(); row++)
			for (int col = 0; col < layer.getTileColumns(); col++)
				switch (Piece.fromTileId(layer.getTMXTile(col, row)
						.getGlobalTileID())) {
				case BOX:
					boxes++;
					break;
				case TARGET:
					targets++;
				default:
					break;
				}

		HashMap<String, Integer> resourceMap = new HashMap<String, Integer>();
		resourceMap.put("shots", 1);
		resourceMap.put("splitters", 1);
		for (TMXTiledMapProperty property : map.getTMXTiledMapProperties()) {
			resourceMap.put(property.getName(),
					Integer.parseInt(property.getValue()));
		}

		shots = resourceMap.get("shots");
		splitters = resourceMap.get("splitters");
	}

	public TMXTiledMap getMap() {
		return map;
	}

	public int getBoxes() {
		return boxes;
	}

	public int getTargets() {
		return targets;
	}

	public int getShots() {
		return shots;
	}

	public int getSplitters() {
		return splitters;
	}
}