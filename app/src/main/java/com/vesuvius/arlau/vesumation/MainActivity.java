package com.vesuvius.arlau.vesumation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Moka7.*;

public class MainActivity extends AppCompatActivity {
    S7Client client=new S7Client();
    String errorMessage;
    int res=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void connect_to_PLC(View view){
        try{
            client.SetConnectionType(S7.S7_BASIC);
            res=client.ConnectTo("192.168.1.100",0,3);
        }catch (Exception e){
            errorMessage="EXC: "+e.toString();
            Thread.interrupted();

        }
    }

    public void disconnect_from_PLC(View view){
            client.Disconnect();
    }

    public String  read_data_db(String data_db ){
        String data_value="";
        String Db_s="";
        int Db=0;
        String Word_s="";
        int Word=0;
        String bit_s="";
        int bit=0;
        String data_type="";
        String outString="";


        try {
            String split = "[.]";
            String[] token = data_db.split(split);
            //Parse db number
            String split1 = "[B]";
            String[] token1 = token[0].split(split1);

            Db_s=token1[1];
            Db=Integer.parseInt(Db_s);

            //Parse bit
            String split2="[.]";
            String [] token2=token[1].split(split2);

            bit_s=token2[1];
            bit=Integer.parseInt(bit_s);

            //Parse data_type
            String split3="[]";
            String [] token3=token2[0].split(split3);
            data_type=token3[2];

            //Parse byte
            if(data_type.equals("X")) {
                String split4 = "[X]";
                String[] token4 = token2[0].split(split4);
                Word_s=token4[1];
            }
            if(data_type.equals("W")) {
                String split4 = "[W]";
                String[] token4 = token2[0].split(split4);
                Word_s=token4[1];
            }
            if(data_type.equals("D")) {
                String split4 = "[D]";
                String[] token4 = token2[0].split(split4);
                Word_s=token4[1];
            }
            Word=Integer.parseInt(Word_s);

        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            errorMessage="ERR: Wrong data input.";
        }

        //Get data if a connection is available
        if(res==0){
            if ((data_type).equals("X")){
                byte[] data=new byte[2];
                res=client.ReadArea(S7.S7AreaDB,Db,Word,1,data);
                if (bit<8) {
                    return S7.GetBitAt(data, 0, bit) + "";
                }
                if (bit>7) {
                    outString=S7.GetBitAt(data, 1, bit) + "";
                }
            }

            if ((data_type).equals("W")){
                byte[] data=new byte[2];
                res=client.ReadArea(S7.S7AreaDB,Db,Word,1,data);
                outString=S7.GetWordAt(data, 0)+"";
            }


            if ((data_type).equals("D")){
                byte[] data=new byte[4];
                res=client.ReadArea(S7.S7AreaDB,Db,Word,2,data);
                outString= S7.GetFloatAt(data, 0)+"";
            }

            if ((!((data_type).equals("X")))&&(!((data_type).equals("X")))&&(!((data_type).equals("X")))){
                errorMessage="ERR: Wrong data input.";
                outString="";
            }

        }else{
            errorMessage="ERR: No connection with the PLC";
        }
        return outString;
    }

}
