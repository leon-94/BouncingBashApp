package de.lmu.ifi.bouncingbash.app;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import de.lmu.ifi.bouncingbash.app.android.AndroidLauncher;
import de.lmu.ifi.bouncingbash.app.android.R;
import de.lmu.ifi.bouncingbash.app.connectivity.RestService;
import de.lmu.ifi.bouncingbash.app.game.GameData;


public class LoginActivity extends ActionBarActivity {

    private EditText edit_userid;
    private EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**set actionbar icon**/
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_bouncingbash);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_login);

        edit_userid = (EditText) findViewById(R.id.edit_login_userid);
        edit_password = (EditText) findViewById(R.id.edit_login_password);

        Utils.enableBluetooth(this);
        Utils.enableLocation(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // --- debug ---
        if(GameData.debug_sp && GameData.postgame) {
            GameData.postgame = false;
            String title = GameData.won ? "Congratulations!" : "Oh no";
            String message = GameData.won ? "You destroyed your opponent like a baws. Well done." : "Maybe next time ...";
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_menu_myplaces)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonLogin(View view) {

        String userId = edit_userid.getText().toString();
        String password = edit_password.getText().toString();

        RestService.getRestService().testAuthentication(userId, password, loginHandler);
    }

    public void onButtonSignup(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    private void showAlert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void startGame(View view) {

        GameData.debug_sp = true;
        Intent i = new Intent(this, AndroidLauncher.class);
        startActivity(i);
    }

    private final Handler loginHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == RestService.MESSAGE_ERROR) {
                Utils.showConnectionErrorDialog(LoginActivity.this);
                return;
            }
            String messageString = (String)msg.obj;
            JsonObject message = (JsonObject) Json.parse(messageString);

            if(message.getBoolean("success", false)) {

                if(!message.getString("type", "UNKNOWN").equals("testauth")) return;

                JsonObject credentials = (JsonObject) message.get("credentials");
                String userId = credentials.getString("userId", null);
                String password = credentials.getString("password", null);
                Data.setCredentials(userId, password);

                Intent i = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(i);
            }
            else {

                String cause = message.getString("cause", "UNKNOWN");
                switch(cause) {
                    case "INVALID_CREDENTIALS":
                        showAlert("Wrong name or password.");
                        break;
                    default:
                        showAlert("There was a problem during log in. Try again later.");
                        break;
                }
            }
        }
    };
}
