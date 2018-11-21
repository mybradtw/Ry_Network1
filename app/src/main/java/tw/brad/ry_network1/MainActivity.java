package tw.brad.ry_network1;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView data;
    private StringBuffer dataString;
    private UIHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new UIHandler();
        data = findViewById(R.id.data);
    }

    public void test1(View view) {
        dataString = new StringBuffer();
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("https://www.bradchao.com");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ( (line = reader.readLine()) != null){
                        dataString.append(line + "\n");
                    }
                    reader.close();
                    uiHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.v("brad", e.toString());
                }
            }
        }.start();

    }
    public void test2(View view) {
        dataString = new StringBuffer();
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(
                            "http://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line; StringBuffer tempJSON = new StringBuffer();
                    while ( (line = reader.readLine()) != null){
                        tempJSON.append(line);
                    }
                    reader.close();
                    parseJSON(tempJSON.toString());
                } catch (Exception e) {
                    Log.v("brad", e.toString());
                }
            }
        }.start();

    }

    private void parseJSON(String json){
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                String name = row.getString("Name");
                String tel = row.getString("Tel");
                dataString.append(name + " : " + tel + "\n");
            }
            uiHandler.sendEmptyMessage(0);

        } catch (Exception e) {
            Log.v("brad", e.toString());
        }

    }


    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    data.setText(dataString);
                    break;
            }
        }
    }

}
