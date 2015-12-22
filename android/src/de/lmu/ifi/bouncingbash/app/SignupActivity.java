package de.lmu.ifi.bouncingbash.app;

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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import de.lmu.ifi.bouncingbash.app.android.R;
import de.lmu.ifi.bouncingbash.app.connectivity.RestService;


public class SignupActivity extends ActionBarActivity {

    AlertDialog.Builder alertDialogBuilder;

    private EditText edit_userid;
    private EditText edit_password;
    private EditText edit_password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

         alertDialogBuilder = new AlertDialog.Builder(this);

        edit_userid = (EditText) findViewById(R.id.edit_signup_userid);
        edit_password = (EditText) findViewById(R.id.edit_signup_password);
        edit_password2 = (EditText) findViewById(R.id.edit_signup_password2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    public void onButtonSignup(View view) {

        String userId = edit_userid.getText().toString();
        String password = edit_password.getText().toString();
        String password2 = edit_password2.getText().toString();

        if(!password.equals(password2)) {
            showAlert("Your passwords don't match!");
            return;
        }

        RestService.getRestService().signUp(userId, password, signupHandler);
    }

    private void showAlert(String message) {

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private final Handler signupHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String messageString = (String)msg.obj;
            JsonObject message = (JsonObject) Json.parse(messageString);

            if(message.getBoolean("success", true)) {

                String userId = message.getString("userId", null);
                String password = message.getString("password", null);
                RestService.getRestService().setCredentials(userId, password);

                alertDialogBuilder
                        .setMessage("Sign up successful!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else {

                String cause = message.getString("cause", "UNKNOWN");
                switch(cause) {
                    case "ID_NOT_UNIQUE":
                        showAlert("Sorry, that name is already taken.");
                        break;
                    default:
                        showAlert("There was a problem during sign up. Try again later.");
                        break;
                }
            }
        }
    };
}
