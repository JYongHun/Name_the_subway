package com.test.name_the_subway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    private static String KEY = "jGXxIzpsnGpI%2BW%2Fx5W9q%2BA110tuI2bR8oeVBSz4kmuY1bWXEzDcEaSrt6YoKUzzJwThEZBQ4eLPj%2F3RHNx6ByA%3D%3D";

    EditText editSubway;
    Button btnSearch;
    Button btnReset;
    Spinner spinner;
    ArrayList<String> tempChk = new ArrayList<String>();
    ListView listView;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSubway = (EditText)findViewById(R.id.editSubway);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnReset = (Button)findViewById(R.id.btnReset);
        spinner = (Spinner)findViewById(R.id.spinner);
        listView = (ListView)findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tempChk);


        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);



        // Thread로 웹서버에 접속
//        new Thread() {
//            public void run() {
//                getSubway();
//            }
//        }.start();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempChk = new ArrayList<String>();
                adapter.clear();
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,tempChk);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editSubway.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this,"역이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                new GetXMLTask().execute();

            }
        });

    }

//    private void getSubway(){
//        StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList"); /*URL*/
//        try {
//            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+KEY); /*Service Key*/
//            urlBuilder.append("&" + URLEncoder.encode("subwayStationName","UTF-8") + "=" + URLEncoder.encode("서울", "UTF-8")); /*지하철역명*/
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Content-type", "application/json");
//            BufferedReader rd;
//            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//            }
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//            Log.i("TEST",sb.toString());
//
//            //XmlPullParser xpp = getResources().getXml(Integer.parseInt(sb.toString()));
//        } catch (UnsupportedEncodingException | MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            Document doc = null;
            try {
                url = new URL("http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList?serviceKey=jGXxIzpsnGpI%2BW%2Fx5W9q%2BA110tuI2bR8oeVBSz4kmuY1bWXEzDcEaSrt6YoKUzzJwThEZBQ4eLPj%2F3RHNx6ByA%3D%3D&subwayStationName="+URLEncoder.encode(editSubway.getText().toString().trim(), "UTF-8"));
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

//            String s = "";
            NodeList nodeList = doc.getElementsByTagName("item");
            boolean emptyChk = true;

            for(int i = 0; i< nodeList.getLength(); i++){

                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList subwayRoute = fstElmnt.getElementsByTagName("subwayRouteName");
                //선택한것이 아니면 리턴함
                if (!spinner.getSelectedItem().toString().equals(subwayRoute.item(0).getChildNodes().item(0).getNodeValue())) {
                    //Toast.makeText(MainActivity.this,"없을수도있습니다",Toast.LENGTH_SHORT).show();
                    //emptyChk = true;
                    continue;
                }
                //s += "idx = "+  idx.item(0).getChildNodes().item(0).getNodeValue() +"\n";

                NodeList subwayName = fstElmnt.getElementsByTagName("subwayStationName");

                int idx = subwayName.item(0).getChildNodes().item(0).getNodeValue().indexOf("(");
                String subwayChk = subwayName.item(0).getChildNodes().item(0).getNodeValue();
                if(idx != -1 ) {
                    subwayChk = subwayName.item(0).getChildNodes().item(0).getNodeValue().substring(0, idx);
                }

                if (editSubway.getText().toString().trim().equals(subwayChk.trim())) {
                    for(int j=0;j<tempChk.size();j++) {
                        if(tempChk.get(j).equals(editSubway.getText().toString().trim())) {
                            Toast.makeText(MainActivity.this,"이미 했습니다",Toast.LENGTH_SHORT).show();
                            editSubway.setText("");
                            return;
                        }
                    }

                    tempChk.add(subwayName.item(0).getChildNodes().item(0).getNodeValue());
                    Toast.makeText(MainActivity.this,"정답입니다",Toast.LENGTH_SHORT).show();
                    emptyChk = false;
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }// else {
//                    Toast.makeText(MainActivity.this,"없습니다1",Toast.LENGTH_SHORT).show();
//                }
                //s += "gugun = "+  gugun.item(0).getChildNodes().item(0).getNodeValue() +"\n";


            }
            if(emptyChk){
                Toast.makeText(MainActivity.this,"없습니다2",Toast.LENGTH_SHORT).show();
            }

            //extview.setText(s);



            editSubway.setText("");
            super.onPostExecute(doc);
        }
    }
}