package de.lmu.ifi.bouncingbash.app.android;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import de.lmu.ifi.bouncingbash.app.Data;
import de.lmu.ifi.bouncingbash.app.game.Game;
import de.lmu.ifi.bouncingbash.app.IActivity;
import de.lmu.ifi.bouncingbash.app.MapActivity;
import de.lmu.ifi.bouncingbash.app.connectivity.BluetoothService;
import de.lmu.ifi.bouncingbash.app.game.GameData;

import android.util.Log;
public class AndroidLauncher extends AndroidApplication implements IActivity{

    private final String TAG = "AndroidLauncher";

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useCompass = true;

		initialize(new Game(this, BluetoothService.getBluetoothService()), cfg);

	}

    public void exitGame(boolean won) {
        Log.d(TAG, "exitGame");
        Data.currentSession = null;
        GameData.isHost = false;

        Intent i = new Intent(this, MapActivity.class);
        Bundle b = new Bundle();
        b.putString("state", "postgame");
        b.putBoolean("won", won);
        startActivity(i);
    }
}
