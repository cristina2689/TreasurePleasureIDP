package org.project.databaseutil.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Game {
	private int gameId;
	private String startDate;
	private String endDate;
	private String title;
	private String description;
	private int userId;

	public Game(JSONObject game) {
		try {
			this.gameId = game.getInt("game_id");
			this.startDate = game.getString("start_date");
			this.endDate = game.getString("end_date");
			this.title = game.getString("title");
			this.description = game.getString("description");
			this.userId = game.getInt("user_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getGameId() {
		return gameId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
