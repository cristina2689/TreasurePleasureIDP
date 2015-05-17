package org.project.databaseutil.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Treasure {

	private double latitude;
	private double longitude;
	private String treasureUrl;
	private int gameId;
	private String hint;

	public Treasure(JSONObject treasure) {
		try {
			this.latitude = treasure.getDouble("latitude");
			this.longitude = treasure.getDouble("longitude");
			this.treasureUrl = treasure.getString("treasure_url");
			this.gameId = treasure.getInt("game_id");
			this.hint = treasure.getString("hint");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getDirectory() {
		String[] urlComponents = treasureUrl.split("/");
		return urlComponents[4];
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getTreasureUrl() {
		return treasureUrl;
	}

	public void setTreasureUrl(String treasureUrl) {
		this.treasureUrl = treasureUrl;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

}
