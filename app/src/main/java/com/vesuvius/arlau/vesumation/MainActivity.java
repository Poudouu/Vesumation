package com.vesuvius.arlau.vesumation;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import Moka7.*;

public class MainActivity extends AppCompatActivity {


    //Global Parameters
    String PLCAddress="192.168.1.100";
    int PLCRack=0;
    int PLCSlot=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String data_address_to_read="M4.4";
        //Output for the read value and ID of the textView to display it.
        String outString="";
        int outStringId=0;

        //ID of the textView to display it the error message
        int errorStringId=0;

        //True, the data data_address_to_read will be forced
        boolean DataAsToBeForced=false;
        //Value forced in cas of DataAsToBeForced
        boolean bitValueToForce=false;
        int intValueToForce=0;
        float floatValueToForce=0;

        //Data type for merker, input and output. As to be defined before call parse_data_merker_IO
        boolean isMerker=true;
        boolean isInput=false;
        boolean isOutput=false;

        //Define container to stock the parsed datas.
        ParseDataResult Result;
        //Call of one of the two functions depending of the data type
        //Result=parse_data_db(data_address_to_read);
        Result = parse_data_merker_IO(data_address_to_read,isMerker,isInput,isOutput);

        //If an error occured during the data parsing, we update directly the outString and the errorMessage strings and display them without calling the readWriteData function.
        // In normal operation, if the data is parsed and read successfully, the update is done by the onPostExecute function of the PlcReadWrite class.
        if(!(Result.errorMessage.equals(""))){
            outString="";
            TextView txout = (TextView) findViewById(outStringId);
            txout.setText(outString);

            TextView txout1=(TextView)findViewById(errorStringId);
            txout1.setText(Result.errorMessage);

        }else {
            readWriteData(Result, outString,outStringId,errorStringId, DataAsToBeForced,bitValueToForce,intValueToForce,floatValueToForce);

        }
    }

    public void readWriteData(ParseDataResult Result, String outString, int outStringId,int errorStringId, boolean DataAsToBeForced, boolean bitValueToForce, int  intValueToForce, float floatValueToForce){
        //create a container for task parameters.
        ReadWritekParams params = new ReadWritekParams(Result.data_type,Result.errorMessage,outString,Result.Db, Result.bit,Result.Word,outStringId,errorStringId,DataAsToBeForced, bitValueToForce, intValueToForce, floatValueToForce );
        //Create a new instance of the PlcReadWrite class and execute it.
        new PlcReadWrite().execute(params);
    }

    //Container with for the PlcReadWrite task parameters.
    private static class ReadWritekParams {
        String data_type;
        String errorMessage;
        String outString;
        int Db;
        int bit;
        int Word;
        int outStringId;
        int errorStringId;
        boolean DataAsToBeForced;
        boolean bitValueToForce;
        int intValueToForce;
        float floatValueToForce;

        ReadWritekParams(String data_type, String errorMessage,String outString,int Db,int bit,int Word,int outStringId,int errorStringId, boolean DataAsToBeForced, boolean bitValueToForce, int intValueToForce, float floatValueToForce) {
            this.data_type = data_type;
            this.errorMessage = errorMessage;
            this.outString = outString;
            this.Db = Db;
            this.bit = bit;
            this.Word = Word;
            this.outStringId = outStringId;
            this.errorStringId = errorStringId;
            this.DataAsToBeForced = DataAsToBeForced;
            this.bitValueToForce = bitValueToForce;
            this.intValueToForce = intValueToForce;
            this.floatValueToForce = floatValueToForce;
        }
    }

    private  class PlcReadWrite extends AsyncTask<ReadWritekParams, Void, String> {
        String outString;
        String errorMessage;
        int outStringId;
        int errorStringId;
        int res;

        @Override
        protected String doInBackground(ReadWritekParams... params) {
            S7Client client=new S7Client();
            byte[] data_1=new byte[2];
            byte[] data_2=new byte[2];
            byte[] data_3=new byte[4];
            int AreaType=0;

            try {
                client.SetConnectionType(S7.S7_BASIC);
                res = client.ConnectTo(PLCAddress, PLCRack, PLCSlot);

                if(res==0){

                    if ((params[0].data_type).equals("X")) {
                        AreaType = S7.S7AreaDB;
                    }
                    if ((params[0].data_type).equals("M")) {
                        AreaType = S7.S7AreaMK;
                    }
                    if ((params[0].data_type).equals("I")) {
                        AreaType = S7.S7AreaPE;
                    }
                    if ((params[0].data_type).equals("O")) {
                        AreaType = S7.S7AreaPA;
                    }
                    if (((params[0].data_type).equals("X")) || ((params[0].data_type).equals("M")) || ((params[0].data_type).equals("I")) || ((params[0].data_type).equals("O"))) {
                        res = client.ReadArea(AreaType, params[0].Db, params[0].Word, 1, data_1);
                        if (params[0].bit < 8) {
                            if (params[0].DataAsToBeForced && (!((params[0].data_type).equals("I")))) {
                                S7.SetBitAt(data_1, 0, params[0].bit, params[0].bitValueToForce);
                                res = client.WriteArea(AreaType, params[0].Db, params[0].Word, 1, data_1);
                            }
                            params[0].outString = S7.GetBitAt(data_1, 0, params[0].bit) + "";
                            params[0]. errorMessage = "";
                        } else {
                            params[0].outString = "";
                            params[0].errorMessage = "ERR: Wrong input data";
                        }
                    }

                    if ((params[0].data_type).equals("W")){
                        res=client.ReadArea(S7.S7AreaDB,params[0].Db,params[0].Word,2,data_2);

                        if(params[0].DataAsToBeForced) {
                            S7.SetWordAt(data_2, 0, params[0].intValueToForce);
                            res = client.WriteArea(S7.S7AreaDB, params[0].Db,params[0].Word, 2, data_2);
                            params[0].errorMessage = "";
                        }
                        params[0].outString=S7.GetWordAt(data_2, 0)+"";
                        params[0].errorMessage="";
                    }


                    if ((params[0].data_type).equals("D")){
                        res=client.ReadArea(S7.S7AreaDB,params[0].Db,params[0].Word,2,data_3);

                        if(params[0].DataAsToBeForced) {
                            S7.SetFloatAt(data_3, 0, params[0].floatValueToForce);
                            res = client.WriteArea(S7.S7AreaDB, params[0].Db, params[0].Word, 2, data_3);
                            params[0].errorMessage = "";
                        }
                        params[0].outString= S7.GetFloatAt(data_3, 0)+"";
                        params[0].errorMessage="";
                    }

                    if ((!((params[0].data_type).equals("X")))&&(!((params[0].data_type).equals("W")))&&(!((params[0].data_type).equals("D")))&&(!((params[0].data_type).equals("M")))&&(!((params[0].data_type).equals("I")))&&(!((params[0].data_type).equals("O")))){
                        params[0].errorMessage="ERR: Wrong data input.";
                        params[0].outString="";
                    }

                    if(res!=0) {
                        params[0].errorMessage = "Data couldn't be read.";
                        params[0].outString = "";
                    }


                }else{
                    params[0].errorMessage="ERR: No connection with the PLC";
                    params[0].outString="";
                }

            } catch (Exception e) {
                params[0].errorMessage="ERR: No connection with the PLC";
                params[0].outString="";
                Thread.interrupted();
            }
            client.Disconnect();

            outString= params[0].outString;
            errorMessage=params[0].errorMessage;
            outStringId=params[0].outStringId;
            errorStringId=params[0].errorStringId;

            return "executed";
        }
        protected void onPostExecute(String result){

            TextView txout = (TextView) findViewById(outStringId);
            txout.setText(outString);

            TextView txout1=(TextView)findViewById(errorStringId);
            txout1.setText(errorMessage);
        }

    }

    //Container for parameters returned by the parse functions.
    public class ParseDataResult {
        int Db;
        int bit;
        int Word;
        String errorMessage;
        String data_type;
    }
    //Function to parse a db data type
    public  ParseDataResult parse_data_db(String data_db ){

        ParseDataResult Result = new ParseDataResult();
        String[] token=null;
        String[] token1=null;
        String[] token2=null;
        String[] token3=null;
        String[] token4=null;
        String Db_s="";
        String Word_s="";
        String bit_s="";

        Result. errorMessage="";

        try {
            String split = "[.]";
            token = data_db.split(split);
            //Parse db number
            String split1 = "[B]";
            token1 = token[0].split(split1);

            Db_s=token1[1];
            Result.Db=Integer.parseInt(Db_s);
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result. errorMessage="ERR: Wrong data input.";

        }
        try{
            //Parse bit
            String split2="[.]";
            token2=token[1].split(split2);

            bit_s=token[2];
            Result.bit=Integer.parseInt(bit_s);

        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        try{
            //Parse data_type
            String split3="[B]";
            token3=token2[0].split(split3);
            char data_type_c;
            data_type_c=(token3[1]+"").charAt(0);
            Result.data_type=data_type_c+"";
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="ERR: Wrong data input.";
        }catch(java.lang.NullPointerException e){
            e.printStackTrace();
            Result.errorMessage="ERR: Wrong data input.";

        }
        try{
            //Parse byte
            if(Result.data_type.equals("X")) {
                String split4 = "[X]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            if(Result.data_type.equals("W")) {
                String split4 = "[W]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            if(Result.data_type.equals("D")) {
                String split4 = "[D]";
                token4 = token3[1].split(split4);
                Word_s=token4[1];
            }
            try {
                Result.Word = Integer.parseInt(Word_s);
            }catch(java.lang.NumberFormatException e){
                e.printStackTrace();
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="ERR: Wrong data input.";
        }
        return Result;
    }

    //Function to parse a merker, input or output data
    public  ParseDataResult parse_data_merker_IO(String data_db, boolean isMerker, boolean isInput, boolean isOutput ) {

        ParseDataResult Result = new ParseDataResult();
        String[] token = null;
        String[] token1 = null;
        String Word_s = "";
        String bit_s = "";
        String split1="";

        Result.errorMessage = "";

        try{
            //Parse bit
            String split="[.]";
            token=data_db.split(split);

            bit_s=token[1];
            Result.bit=Integer.parseInt(bit_s);

        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="ERR: Wrong data input.";
        }

        if(isMerker) {
            split1 = "[M]";
            Result.data_type = "M";
        }
        if(isInput) {
            split1 = "[I]";
            Result.data_type = "I";
        }
        if(isOutput) {
            split1 = "[O]";
            Result.data_type = "O";
        }
        token1 = token[0].split(split1);
        Word_s=token1[1];
        try {
            Result.Word = Integer.parseInt(Word_s);
        }catch(java.lang.NumberFormatException e){
            e.printStackTrace();
            Result.errorMessage="ERR: Wrong data input.";
        }
        return Result;
    }

}
