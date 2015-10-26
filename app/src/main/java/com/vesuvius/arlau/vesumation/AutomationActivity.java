package com.vesuvius.arlau.vesumation;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vesuvius.arlau.vesumation.Moka7.*;

public class AutomationActivity extends AppCompatActivity {

    EditText eText;
    String var_type, value;
    String str;
    //DB XX DBB XX . XX
    //1  2  3   4  5 6
    static int INDEX_VAR_TYPE=1;
    static int INDEX_VAR_BYTE=2;
    static int INDEX_SPINNERDB=3;
    static int INDEX_DBX_BYTE_M_BIT=4;
    static int INDEX_DOT=5;
    static int INDEX_DBX_BIT=6;
    static int INDEX_DELETE_BUTTON=7;
    static int INDEX_TV_VALUE=8;
    static int INDEX_READWRITE_VALUE=9;
    static int INDEX_REFRESH_BUTTON=10;
    static int INDEX_FORCE_BUTTON=11;

    String PLCAddress="192.168.1.100";
    int PLCRack=0;
    int PLCSlot=3;

    int i = 0;
    boolean setup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = 1;
        setup = false;
        setContentView(R.layout.automation);
    }

    public void showPopMenu(View view){

        PopupMenu menu = new PopupMenu(this,view);
        menu.getMenuInflater().inflate(R.menu.menu_db, menu.getMenu());

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.db_id:
                            createLayout2(1);
                            break;
                        case R.id.m_id:
                            createLayout2(2);
                            break;
                        case R.id.q_id:
                            createLayout2(3);
                            break;
                        case R.id.i_id:
                            createLayout2(4);
                            break;

                    }
                    i=i+20;
                    return true;
                }
            });

            menu.show();
        }

    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }




    public void createLayout2(int param){
        LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        switch (param) {
            case 1:
                view = inflater.inflate(R.layout.layout_new_boolean, null);
                lt.addView(view);
                assignDynamicId(i, view, param);
                break;
            case 2:
                view = inflater.inflate(R.layout.layout_new_m, null);
                lt.addView(view);
                assignDynamicId(i,view,param);
                break;
            case 3:
                view = inflater.inflate(R.layout.layout_new_q, null);
                lt.addView(view);
                assignDynamicId(i,view,param);
                break;
            case 4:
                view = inflater.inflate(R.layout.layout_new_i, null);
                lt.addView(view);
                assignDynamicId(i,view,param);
                break;
        }
    }

    private void assignDynamicId(int i, View view, int param) {


        TextView tv,tv2,tv3;
        EditText editText1,editText2,editText3,editText4;
        Spinner spinner;
        RelativeLayout.LayoutParams params;
        view.setId(i);
        ImageView deletebutton=null,refreshbutton=null,forcebutton=null;


        switch(param) {
            case 1:
                //DB=VARTYPE
                //XX = VAR_BYTE
                //DBB = SPINNERDB
                //DB XX DBB XX . XX
                //1  2  3   4  5 6
                //DB
                tv = (TextView) view.findViewById(R.id.var_type);
                tv.setId(i + INDEX_VAR_TYPE);
                //XX
                editText1 = (EditText) view.findViewById(R.id.dbnumber);
                params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv.getId());
                editText1.setId(i + INDEX_VAR_BYTE);
                //DBX DBB DBW DBD
                spinner = (Spinner) view.findViewById(R.id.spinnerdb);
                params = (RelativeLayout.LayoutParams)spinner.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText1.getId());
                spinner.setId(i + INDEX_SPINNERDB);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int viewid = parent.getId();
                        viewid = viewid - INDEX_SPINNERDB;
                        TextView text = (TextView) view;

                        EditText txt = (EditText) findViewById(viewid + INDEX_DBX_BIT);
                        TextView dot = (TextView) findViewById(viewid + INDEX_DOT);
                        if (text.getText().equals("DBX")) {
                            txt.setVisibility(View.VISIBLE);
                            dot.setVisibility(View.VISIBLE);

                        }
                        if (text.getText().equals("DBW")) {
                            txt.setVisibility(View.INVISIBLE);
                            dot.setVisibility(View.INVISIBLE);
                        }
                        if (text.getText().equals("DBB")) {
                            txt.setVisibility(View.INVISIBLE);
                            dot.setVisibility(View.INVISIBLE);
                        }
                        if (text.getText().equals("DBD")) {
                            txt.setVisibility(View.INVISIBLE);
                            dot.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //XX
                editText2 = (EditText) view.findViewById(R.id.bytedbx);
                params = (RelativeLayout.LayoutParams)editText2.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, spinner.getId());
                editText2.setId(i + INDEX_DBX_BYTE_M_BIT);
                //dot
                tv2 = (TextView) view.findViewById(R.id.dot);
                params = (RelativeLayout.LayoutParams)tv2.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText2.getId());
                tv2.setId(i + INDEX_DOT);
                //XX
                editText3 = (EditText) view.findViewById(R.id.bitdbx);
                params = (RelativeLayout.LayoutParams)editText3.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv2.getId());
                editText3.setId(i + INDEX_DBX_BIT);
                //DELETE LINE
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setId(i+INDEX_DELETE_BUTTON);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //VALUE :
                tv3 = (TextView) findViewById(R.id.tvvalue);
                tv3.setId(i+INDEX_TV_VALUE);
                //VALUE READ WRITE
                editText4 = (EditText) findViewById(R.id.value_readwrite);
                params = (RelativeLayout.LayoutParams)editText4.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv3.getId());
                editText4.setId(i+INDEX_READWRITE_VALUE);
                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
                forcebutton.setOnClickListener(ClickForce(forcebutton));

                //REFRESH BUTTON
                refreshbutton = (ImageView)findViewById(R.id.refreshbut);
                params = (RelativeLayout.LayoutParams)refreshbutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, forcebutton.getId());
                refreshbutton.setId(i + INDEX_REFRESH_BUTTON);
                refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                break;

            case 2:
                //M XX . XX
                tv = (TextView) view.findViewById(R.id.var_type);
                tv.setId(i + INDEX_VAR_TYPE);
                //XX
                editText1 = (EditText) view.findViewById(R.id.mbyte);
                params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv.getId());
                editText1.setId(i + INDEX_VAR_BYTE);
                //XX
                editText2 = (EditText) view.findViewById(R.id.mbit);
                params = (RelativeLayout.LayoutParams)editText2.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText1.getId());
                editText2.setId(i + INDEX_DBX_BYTE_M_BIT);
                // Value :
                tv3 = (TextView) findViewById(R.id.tvvalue);
                params = (RelativeLayout.LayoutParams)tv3.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText2.getId());
                tv3.setId(i+INDEX_TV_VALUE);
                //VALUE READ WRITE
                editText4 = (EditText) findViewById(R.id.value_readwrite);
                params = (RelativeLayout.LayoutParams)editText4.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv3.getId());
                editText4.setId(i+INDEX_READWRITE_VALUE);
                //DELETE LINE
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setId(i + INDEX_DELETE_BUTTON);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //REFRESH BUTTON
                refreshbutton = (ImageView)findViewById(R.id.refreshbut);
                params = (RelativeLayout.LayoutParams)refreshbutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, deletebutton.getId());
                refreshbutton.setId(i + INDEX_REFRESH_BUTTON);
                refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));
                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
                forcebutton.setOnClickListener(ClickForce(forcebutton));

                break;
            case 3:
                //Q XX . XX
                tv = (TextView) view.findViewById(R.id.var_type);
                tv.setId(i+INDEX_VAR_TYPE);
                //XX
                editText1 = (EditText) view.findViewById(R.id.qbyte);
                params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF,tv.getId());
                editText1.setId(i+INDEX_VAR_BYTE);
                //XX
                editText2 = (EditText) view.findViewById(R.id.qbit);
                params = (RelativeLayout.LayoutParams)editText2.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText1.getId());
                editText2.setId(i+INDEX_DBX_BYTE_M_BIT);
                // Value :
                tv3 = (TextView) findViewById(R.id.tvvalue);
                params = (RelativeLayout.LayoutParams)tv3.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText2.getId());
                tv3.setId(i+INDEX_TV_VALUE);
                //VALUE READ WRITE
                editText4 = (EditText) findViewById(R.id.value_readwrite);
                params = (RelativeLayout.LayoutParams)editText4.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv3.getId());
                editText4.setId(i+INDEX_READWRITE_VALUE);
                //DELETE LINE
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setId(i + INDEX_DELETE_BUTTON);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //REFRESH BUTTON
                refreshbutton = (ImageView)findViewById(R.id.refreshbut);
                params = (RelativeLayout.LayoutParams)refreshbutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, deletebutton.getId());
                refreshbutton.setId(i + INDEX_REFRESH_BUTTON);
                refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
                forcebutton.setOnClickListener(ClickForce(forcebutton));

                break;
            case 4:
                //I XX . XX
                tv = (TextView) view.findViewById(R.id.var_type);
                tv.setId(i+INDEX_VAR_TYPE);
                //XX
                editText1 = (EditText) view.findViewById(R.id.ibyte);
                params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF,tv.getId());
                editText1.setId(i+INDEX_VAR_BYTE);
                //XX
                editText2 = (EditText) view.findViewById(R.id.ibit);
                params = (RelativeLayout.LayoutParams)editText2.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText1.getId());
                editText2.setId(i+INDEX_DBX_BYTE_M_BIT);
                // Value :
                tv3 = (TextView) findViewById(R.id.tvvalue);
                params = (RelativeLayout.LayoutParams)tv3.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, editText2.getId());
                tv3.setId(i+INDEX_TV_VALUE);
                //VALUE READ WRITE
                editText4 = (EditText) findViewById(R.id.value_readwrite);
                params = (RelativeLayout.LayoutParams)editText4.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, tv3.getId());
                editText4.setId(i+INDEX_READWRITE_VALUE);
                //DELETE LINE
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setId(i + INDEX_DELETE_BUTTON);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //REFRESH BUTTON
                refreshbutton = (ImageView)findViewById(R.id.refreshbut);
                params = (RelativeLayout.LayoutParams)refreshbutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, deletebutton.getId());
                refreshbutton.setId(i + INDEX_REFRESH_BUTTON);
                refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
                forcebutton.setOnClickListener(ClickForce(forcebutton));

                break;
        }

    }

    View.OnClickListener ClickDelete(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int idParent = imageView.getId()-INDEX_DELETE_BUTTON;
                LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
                RelativeLayout rl = (RelativeLayout) findViewById(idParent);
                lt.removeView(rl);
                }
        };
    }

    View.OnClickListener ClickRefresh(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int idParent = imageView.getId()-INDEX_DELETE_BUTTON;
                LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
                RelativeLayout rl = (RelativeLayout) findViewById(idParent);
                //TODO Build string
                String data_address_to_read="M4.4";
                //Output for the read value and ID of the textView to display it.
                String outString="";
                //ID where the outString should be written (Value : )
                int outStringId=idParent+INDEX_READWRITE_VALUE;

                //ID of the textView to display it the error message
                int errorStringId=outStringId;

                //True, the data data_address_to_read will be forced
                boolean DataAsToBeForced=false;
                //Value forced in cas of DataAsToBeForced
                boolean bitValueToForce=false;
                int intValueToForce=0;
                float floatValueToForce=0;

                //Data type for merker(M), input and output. As to be defined before call parse_data_merker_IO
                //TODO handle M, Q and I depending value of tv (1)
                boolean isMerker=true;
                boolean isInput=false;
                boolean isOutput=false;

                //Define container to stock the parsed datas.
                ParseDataResult Result;
                //Call of one of the two functions depending of the data type
                //TODO Star parse DB or MERKER depending test above
                //Result=parse_data_db(data_address_to_read);
                Result = parse_data_merker_IO(data_address_to_read,isMerker,isInput,isOutput);

                //If an error occured during the data parsing, we update directly the outString and the errorMessage strings and display them without calling the readWriteData function.
                // In normal operation, if the data is parsed and read successfully, the update is done by the onPostExecute function of the PlcReadWrite class.
                if(!(Result.errorMessage.equals(""))){
                    EditText txout1=(EditText)findViewById(errorStringId);
                    txout1.setText(Result.errorMessage);

                }else {
                    readWriteData(Result, outString,outStringId,errorStringId, DataAsToBeForced,bitValueToForce,intValueToForce,floatValueToForce);

                }
            }

        };
    }

    View.OnClickListener ClickForce(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int idParent = imageView.getId()-INDEX_DELETE_BUTTON;
                LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
                RelativeLayout rl = (RelativeLayout) findViewById(idParent);
            }
        };
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

            if(errorMessage.equals("")) {
                EditText txout = (EditText) findViewById(outStringId);
                txout.setText(outString);
            }else {
                EditText txout1 = (EditText) findViewById(errorStringId);
                String cheval="";
                txout1.setText(errorMessage);
            }
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
