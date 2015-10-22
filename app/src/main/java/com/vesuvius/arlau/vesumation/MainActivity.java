package com.vesuvius.arlau.vesumation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Moka7.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    S7Client client=new S7Client();

    public void read_data(View view){
        new PlcReader().execute("");
    }
    private  class PlcReader extends AsyncTask<String, Void, String>{
        String ret="";

        @Override
        protected String doInBackground(String... params){
            try{
                client.SetConnectionType(S7.S7_BASIC);
                int res=client.ConnectTo("192.168.1.100",0,3);

                if(res==0){
                    byte[] data=new byte[4];
                    res=client.ReadArea(S7.S7AreaDB,4,170,2,data);
                    ret="Value of DB4.DBD170: "+S7.GetFloatAt(data,0);

                }else{
                    ret="ERR: "+S7Client.ErrorText(res);
                }
            }catch (Exception e){
                ret="EXC: "+e.toString();
                Thread.interrupted();

                }
            return "executed";
        }

        protected void onPostExecute(String result){
            TextView txout=(TextView)findViewById(R.id.textView);
            txout.setText(ret);
        }
    }

}
