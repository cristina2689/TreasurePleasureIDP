package org.project.databaseutil;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private int userId;
	private String userName;

	public User(JSONObject user) {
		try {
			this.userId = user.getInt("user_id");
			this.userName = user.getString("user_name");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
