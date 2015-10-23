package com.vesuvius.arlau.vesumation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import Moka7.*;

public class MainActivity extends AppCompatActivity {
    S7Client client=new S7Client();
    String errorMessage;
    int res;

    String Db_s="";
    int Db=0;
    String Word_s="";
    int Word=0;
    String bit_s="";
    int bit=0;
    String data_type="";
    String outString="";
    String[] token=null;
    String[] token1=null;
    String[] token2=null;
    String[] token3=null;
    String[] token4=null;

    byte[] data_1=new byte[2];
    byte[] data_2=new byte[2];
    byte[] data_3=new byte[4];

    //Global Parameters
    String PLCAddress="192.168.1.100";
    int PLCRack=0;
    int PLCSlot=3;
    boolean DataAsToBeForced=true;
    boolean bitValueToForce=false;
    int intValueToForce=700;
    float floatValueToForce=1200;
    String data_address_to_read="DB4.DBW174";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parse_data_db(data_address_to_read);
        readWriteData();
    }

    public void readWriteData(){
        new PlcReadWrite().execute("");
    }

    private  class PlcReadWrite extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                client.SetConnectionType(S7.S7_BASIC);
                res = client.ConnectTo(PLCAddress, PLCRack, PLCSlot);

                if(res==0){
                    if ((data_type).equals("X")) {
                        res = client.ReadArea(S7.S7AreaDB, Db, Word, 1, data_1);
                        if (bit < 8) {
                            if (DataAsToBeForced) {
                                S7.SetBitAt(data_1, 0, bit, bitValueToForce);
                                res = client.WriteArea(S7.S7AreaDB, Db, Word, 1, data_1);
                            }
                            outString = S7.GetBitAt(data_1, 0, bit) + "";
                            errorMessage = "";
                        } else {
                            outString = "";
                            errorMessage = "ERR: Wrong input data";
                        }
                    }

                    if ((data_type).equals("W")){
                        res=client.ReadArea(S7.S7AreaDB,Db,Word,2,data_2);

                        if(DataAsToBeForced) {
                            S7.SetWordAt(data_2, 0, intValueToForce);
                            res = client.WriteArea(S7.S7AreaDB, Db, Word, 2, data_2);
                            errorMessage = "";
                        }
                        outString=S7.GetWordAt(data_2, 0)+"";
                        errorMessage="";
                    }


                    if ((data_type).equals("D")){
                        res=client.ReadArea(S7.S7AreaDB,Db,Word,2,data_3);

                        if(DataAsToBeForced) {
                            S7.SetFloatAt(data_3, 0, floatValueToForce);
                            res = client.WriteArea(S7.S7AreaDB, Db, Word, 2, data_3);
                            errorMessage = "";
                        }
                        outString= S7.GetFloatAt(data_3, 0)+"";
                        errorMessage="";
                    }

                    if ((!((data_type).equals("X")))&&(!((data_type).equals("W")))&&(!((data_type).equals("D")))){
                        errorMessage="ERR: Wrong data input.";
                        outString="";
                    }

                    if(res!=0) {
                        errorMessage = "Data couldn't be read.";
                        outString = "";
                    }


                }else{
                    errorMessage="ERR: No connection with the PLC";
                    outString="";
                }

            } catch (Exception e) {
                errorMessage="ERR: No connection with the PLC";
                outString="";
                Thread.interrupted();
            }
            client.Disconnect();
            return "executed";
        }
        protected void onPostExecute(String result){
            TextView txout = (TextView) findViewById(R.id.textView);
            txout.setText(outString);

            TextView txout1=(TextView)findViewById(R.id.textView1);
            txout1.setText(errorMessage);
        }

    }


    public  void parse_data_db(String data_db ){

        try {
            String split = "[.]";
            token = data_db.split(split);
            //Parse db number
            String split1 = "[B]";
            token1 = token[0].split(split1);

            Db_s=token1[1];
            Db=Integer.parseInt(Db_s);
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            errorMessage="ERR: Wrong data input.";
            outString="";
        }
        try{
            //Parse bit
            String split2="[.]";
            token2=token[1].split(split2);

            bit_s=token[2];
            bit=Integer.parseInt(bit_s);

        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        try{
            //Parse data_type
            String split3="[B]";
            token3=token2[0].split(split3);
            char data_type_c;
            data_type_c=(token3[1]+"").charAt(0);
            data_type=data_type_c+"";
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            errorMessage="ERR: Wrong data input.";
            outString="";
        }catch(java.lang.NullPointerException e){
            e.printStackTrace();
            errorMessage="ERR: Wrong data input.";
            outString="";
        }
        try{
            //Parse byte
            if(data_type.equals("X")) {
                String split4 = "[X]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            if(data_type.equals("W")) {
                String split4 = "[W]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            if(data_type.equals("D")) {
                String split4 = "[D]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            try {
                Word = Integer.parseInt(Word_s);
            }catch(java.lang.NumberFormatException e){
                e.printStackTrace();
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            errorMessage="ERR: Wrong data input.";
            outString="";
        }
    }

}
