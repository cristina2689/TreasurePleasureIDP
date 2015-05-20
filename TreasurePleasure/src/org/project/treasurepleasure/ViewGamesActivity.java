package org.project.treasurepleasure;

import static org.project.treasurepleasure.Constants.JOIN_GAME;
import static org.project.treasurepleasure.Constants.SERVER_URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.project.databaseutil.classes.Game;
import org.project.databaseutil.classes.Treasure;
import org.project.databaseutil.classes.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewGamesActivity extends Activity {

	private List<Game> games = new ArrayList<Game>();

	private List<User> users = new ArrayList<User>();
	
	private LinearLayout linearLayout;
	
	public List<Treasure> treasures = new ArrayList<Treasure>(); 
	
	private int[] buttons;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_games);
		linearLayout = (LinearLayout) findViewById(R.id.linearLayoutViewGames);

		new GetGamesConnectDB().execute();
	}

	class GetGamesConnectDB extends AsyncTask<String, Void, String> {

		private ProgressDialog progressMessage;
		String gamesFromDB, usersFromDB;

		View.OnClickListener joinGameHandler = new View.OnClickListener() {
			public void onClick(View v) {
				// find clicked button
				int clickedButton = v.getId();
				
				Log.i("clicked button", "" + clickedButton);
				
				Intent intent = new Intent(ViewGamesActivity.this, JoinGameActivity.class);
				intent.putExtra(JOIN_GAME, buttons[clickedButton]);
				startActivity(intent);
			}
		};
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressMessage = new ProgressDialog(ViewGamesActivity.this);
			progressMessage.setMessage("Loading ...");
			progressMessage.setIndeterminate(false);
			progressMessage.setCancelable(false);
			progressMessage.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String url = SERVER_URL + "getAllGames.php";
			HttpClient httpClient = new DefaultHttpClient();

			try {
				HttpGet request = new HttpGet();
				request.setURI(new URI(url));

				HttpResponse response = httpClient.execute(request);
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuilder sb = new StringBuilder();
				String line = "";

				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}

				String[] infoFromDB = sb.toString().split("b43ba7021dc79664d543e0a2346ea986");
				// infoFromDB[0] -> games; infoFromDB[1] -> users + last 3 lines
				gamesFromDB = infoFromDB[0];

				String[] infoFromDB2 = infoFromDB[1].split("\n");
				usersFromDB = "";
				for (int i = 0; i < infoFromDB2.length - 3; i++) {
					usersFromDB += infoFromDB2[i] + "\n";
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (!gamesFromDB.equals("null")) {

							JSONArray ja = null;

							try {
								ja = new JSONArray(gamesFromDB);
								for (int i = 0; i < ja.length(); i++) {
									Game game = new Game(ja.getJSONObject(i));
									games.add(game);
								}

								ja = new JSONArray(usersFromDB);
								for (int i = 0; i < ja.length(); i++) {
									User user = new User(ja.getJSONObject(i));
									users.add(user);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

							buttons = new int[games.size() + 1];
							
							// populate view with info from DB
							for (int i = 0; i < games.size(); i++) {
								Game game = games.get(i);
								
								LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.view_game, linearLayout, false);

								LinearLayout firstLinearLayout = (LinearLayout) view.getChildAt(0);
								((TextView) firstLinearLayout.getChildAt(1)).setText(game.getTitle());
								
								LinearLayout secondLinearLayout = (LinearLayout) view.getChildAt(1);
								((TextView) secondLinearLayout.getChildAt(1)).setText(users.get(i).getUserName());
								
								LinearLayout thirdLinearLayout = (LinearLayout) view.getChildAt(2);
								((TextView) thirdLinearLayout.getChildAt(1)).setText(game.getDescription());

								LinearLayout fourthLinearLayout = (LinearLayout) view.getChildAt(3);
								((TextView) fourthLinearLayout.getChildAt(1)).setText(game.getStartDate());
								
								LinearLayout fifthLinearLayout = (LinearLayout) view.getChildAt(4);
								((TextView) fifthLinearLayout.getChildAt(1)).setText(game.getEndDate());
								
								Button button = (Button) view.getChildAt(5);
								button.setId(i + 1);
								button.setOnClickListener(joinGameHandler);
								
								buttons[button.getId()] = game.getGameId();								
								
								linearLayout.addView(view);
							}
						}
						
 						progressMessage.dismiss();						
					}
				});

			} catch (Exception e) {
				Log.e("ERROR:", e.getMessage());
			}

			return null;
		}

	}
}
