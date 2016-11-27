package motiolabs.myfast;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import motiolabs.myfast.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tv_password, tv_registrasi, tv_loginmsg;
    EditText et_username, et_password;

    SharedPreferences pref;
    //private DBDataSource dataSource;
    private ProgressDialog pDialog;
    int param_num = 0;
    int login_num = 0;
    String sess_uid, sess_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new btnListener());

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        pref = getSharedPreferences(Constants.fast_shared, Context.MODE_PRIVATE);
        tv_loginmsg = (TextView) findViewById(R.id.loginMsg);
        tv_loginmsg.setVisibility(View.GONE);

        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_password.setOnClickListener(new txtviewListener());
        tv_registrasi = (TextView) findViewById(R.id.tv_registrasi);
        tv_registrasi.setOnClickListener(new txtviewListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class btnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLogin:
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();

                    String error = "";
                    if (username.isEmpty()) {
                        error += "- Username kosong\n";
                    }
                    if (password.isEmpty()) {
                        error += "- Password kosong\n";
                    }

                    if (error.equals("")) {
                        //submitLogin();

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        tv_loginmsg.setVisibility(View.VISIBLE);
                        tv_loginmsg.setText("Username dan password harus diisi !");
                    }
                    break;
            }
        }
    }

    private class txtviewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_password:
                    Intent i1 = new Intent(LoginActivity.this, ForgetPassActivity.class);
                    finish();
                    startActivity(i1);
                    break;
                case R.id.tv_registrasi:
                    Intent i2 = new Intent(LoginActivity.this, RegisterActivity.class);
                    finish();
                    startActivity(i2);
                    break;
            }
        }
    }

    public void submitLogin() {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        StringRequest strRequest = new StringRequest
                (Request.Method.POST, Constants.url_login, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Constants.log_app, Constants.url_login);
                        Log.d(Constants.log_app, response);
                        try {
                            JSONObject json = new JSONObject(response);
                            String code = json.getString("code");
                            String message = json.getString("message");

                            if (code.equals("100")) {
                                JSONObject jdata = json.getJSONObject("data");
                                String o_uid = jdata.getString("users_id");
                                String o_session = jdata.getString("session");

                                sess_uid = o_uid;
                                sess_session = o_session;

                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(Constants.spref_ouid, o_uid);
                                editor.putString(Constants.spref_osession, o_session);
                                editor.commit();

                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                tv_loginmsg.setVisibility(View.VISIBLE);
                                tv_loginmsg.setText(message + " (" + code + ")");
                            }
                            pDialog.dismiss();
                            btnLogin.setEnabled(true);
                        } catch (Throwable t) {
                            Log.e(Constants.log_app, "Could not parse JSON : \"" + response + "\"");
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        btnLogin.setEnabled(true);

                        String strError = "";
                        Class klass = error.getClass();
                        if (klass == com.android.volley.AuthFailureError.class) {
                            Log.d(Constants.log_app, "AuthFailureError");
                            strError = "Auth Failure";
                        } else if (klass == com.android.volley.NetworkError.class) {
                            Log.d(Constants.log_app, "NetworkError");
                            strError = "Network Error";
                        } else if (klass == com.android.volley.NoConnectionError.class) {
                            Log.d(Constants.log_app, "NoConnectionError");
                            strError = "No Connection";
                        } else if (klass == com.android.volley.ServerError.class) {
                            Log.d(Constants.log_app, "ServerError");
                            strError = "Server Error";
                        } else if (klass == com.android.volley.TimeoutError.class) {
                            Log.d(Constants.log_app, "TimeoutError");
                            strError = "Time Out Connection";
                        } else if (klass == com.android.volley.ParseError.class) {
                            Log.d(Constants.log_app, "ParseError");
                            strError = "Parse Error";
                        } else if (klass == com.android.volley.VolleyError.class) {
                            Log.d(Constants.log_app, "General error");
                            strError = "General Error";
                        }
                        Toast.makeText(LoginActivity.this, strError, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                Log.d(Constants.log_app, params.toString());
                return params;
            }
        };

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Attempting login...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        btnLogin.setEnabled(false);
        Volley.newRequestQueue(this).add(strRequest);
    }


}