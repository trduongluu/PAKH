package vn.com.vhc.pakh;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import info.LinkAPI;
import info.Request;

public class RequestofUser extends AppCompatActivity {

    String user;

    ListView requestView;
    RequestAdapter rqAdapter;
    LayoutInflater inflater;
    ImageButton infoRQ;

    ArrayList<Request> rqList;
    LinkAPI linkapi = new LinkAPI();
    String linkToSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestof_user);

        dataSearch();

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        requestView = (ListView) findViewById(R.id.requestUserList);

        rqList = new ArrayList<Request>();
        new SearchRequest().execute(linkToSearch);

        View header = inflater.inflate(R.layout.show_request_header, null);
        requestView.addHeaderView(header);
        header.setBackgroundColor(Color.parseColor("#30336b"));

        infoRQ = (ImageButton) findViewById(R.id.chuthich);
        infoRQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAlerDialog();
            }
        });

        requestView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ID = rqList.get(position-1).getStt();
                String ticketID = rqList.get(position-1).getTicketid();

                Intent intent = new Intent(RequestofUser.this, XuLy.class);
                intent.putExtra("ID", ID);
                intent.putExtra("TicketID", ticketID);

                RequestofUser.this.startActivity(intent);
            }
        });
    }

    private void dataSearch() {
        user = getIntent().getExtras().getString("User");
        linkToSearch = linkapi.linkSearchRQ+"start_req_date="+"03-01-2017"+"&end_req_date="+"15-03-2018"+"&pro_user="+user;
    }

    private class SearchRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    content = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        content.append(line);
                    }
                    br.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return content.toString();
        }

        protected  void onPostExecute(String s){
            super.onPostExecute(s);
            getlistUserRQ(s);
            if (rqList.size()==0) {
                Toast.makeText(getApplicationContext(), "Không có yêu cầu nào.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getlistUserRQ(String s){
        try {
            JSONArray jsonAr = new JSONArray(s);
            for (int i = 0; i < jsonAr.length(); i++){
                JSONObject obj = jsonAr.getJSONObject(i);
                String stt = obj.getString("id");
                String ticketid = obj.getString("ticket_id");
                String reqTitle = obj.getString("req_title");
                String reqDepCode = obj.getString("req_dep_code");
                String reqUser = obj.getString("req_user");
                String reqDate = obj.getString("req_date");
                String proDepCode = obj.getString("pro_dep_code");
                String proUser = obj.getString("pro_user");
                String reqStatus = obj.getString("req_status");
                String proPlan = obj.getString("pro_plan");
                String proActua = obj.getString("pro_actua");
                String reqContent = obj.getString("req_content");
                String proContent = obj.getString("pro_content");
                String reqSysCode = obj.getString("req_system_code");

                Request request = new Request(stt, ticketid, reqTitle, reqDepCode, reqUser, reqDate,
                        proDepCode, proUser, reqStatus, proPlan, proActua, reqContent, proContent, reqSysCode);
                rqList.add(request);
                rqAdapter = new RequestAdapter(getApplicationContext(), R.layout.show_rqchild, rqList);
                requestView.setAdapter(rqAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }

    private void displayAlerDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.note_request, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setNegativeButton("Ẩn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
