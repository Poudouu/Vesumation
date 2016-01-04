package com.vesuvius.arlau.vesumation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vesuvius.arlau.vesumation.Moka7.*;

import org.w3c.dom.Text;

public class AutomationActivity extends AppCompatActivity {

    Point p;

    //DB XX DBB XX . XX
    //1  2  3   4  5 6

    static int INDEX_READWRITE_VALUE=9;
    static int INDEX_START_DYN_LAYOUT=50;
    final Context cont = this;

    String PLCAddress="192.168.1.100";
    int PLCRack=0;
    int PLCSlot=3;
    String Ip1="", Ip2="",Ip3="",Ip4="";

    int i = 0;
    boolean setup = false;
    boolean layoutexisting;
    String valueOutputReadWritePlc;
    S7Client client=new S7Client();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = INDEX_START_DYN_LAYOUT;
        setup = false;
        if (layoutexisting) {

        }else{
            setContentView(R.layout.automation);

        }
        layoutexisting=true;
    }

    public void setIpAddress(View view){
        showPopup(AutomationActivity.this, p,view);
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        ImageView button = (ImageView) findViewById(R.id.setipbutton);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    public void showPopupforce(View view){

        // Inflate the popup_layout.xml
        RelativeLayout rl = (RelativeLayout) view.getParent();
        final Dialog dialog = new Dialog(cont);
        TextView variable_type_tv = (TextView) rl.findViewById(R.id.var_type_confirmed_layout);
        String variable_type = variable_type_tv.getText().toString();
        TextView variable_number_tv = (TextView) rl.findViewById(R.id.var_number_confirmed_layout);
        String variable_number = variable_number_tv.getText().toString();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView db_type_tv = (TextView) rl.findViewById(R.id.db_type_confirmed_layout);
        String db_type = db_type_tv.getText().toString();

        if (variable_type.equals("M")||variable_type.equals("Q") || db_type.equals("DBX")){
            dialog.setContentView(R.layout.pop_force_boolean);
            ImageView confirmForce = (ImageView) dialog.findViewById(R.id.confirmforce);
            confirmForce.setOnClickListener(ClickForceBoolean(rl, dialog, variable_type, variable_number));
            final ImageView redbut = (ImageView) dialog.findViewById(R.id.forcefalse);
            final ImageView greenbut = (ImageView) dialog.findViewById(R.id.forcetrue);
            redbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redbut.setImageResource(R.drawable.red_button_pressed);
                    greenbut.setImageResource(R.drawable.green_button);
                }
            });
            greenbut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    redbut.setImageResource(R.drawable.red_button);
                    greenbut.setImageResource(R.drawable.gree_button_pressed);
                }
            });
        }
        if(variable_type.equals("DB") && (db_type.equals("DBB")||db_type.equals("DBW")))
        {
            dialog.setContentView(R.layout.pop_force);
            ImageView confirmForce = (ImageView) dialog.findViewById(R.id.confirmforce);
            EditText edt = (EditText)dialog.findViewById(R.id.editforce);
            edt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            confirmForce.setOnClickListener(ClickForce(rl, dialog));
        }
        if(variable_type.equals("DB") && (db_type.equals("DBD")))
        {
            dialog.setContentView(R.layout.pop_force);
            EditText edt = (EditText)dialog.findViewById(R.id.editforce);
            edt.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ImageView confirmForce = (ImageView) dialog.findViewById(R.id.confirmforce);
            confirmForce.setOnClickListener(ClickForce(rl, dialog));
        }

        dialog.show();
    }

    private void showPopup(final Activity context, Point p, View view) {

        // Inflate the popup_layout.xml
        //LayoutInflater inflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.pop_config_var_layout, null);
        String[] Ip = PLCAddress.split("\\.");
        EditText edt1;
        edt1 = (EditText) layout.findViewById(R.id.editTxtIp1);
        edt1.setHint(Ip[0]);
        edt1 = (EditText) layout.findViewById(R.id.editTxtIp2);
        edt1.setHint(Ip[1]);
        edt1 = (EditText) layout.findViewById(R.id.editTxtIp3);
        edt1.setHint(Ip[2]);
        edt1 = (EditText) layout.findViewById(R.id.editTxtIp4);
        edt1.setHint(Ip[3]);
        edt1 = (EditText) layout.findViewById(R.id.ediTxtRack);
        edt1.setHint(String.valueOf(PLCRack));
        edt1 = (EditText) layout.findViewById(R.id.ediTxtSlot);
        edt1.setHint(String.valueOf(PLCSlot));
        // Creating the PopupWindow

        final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setContentView(layout);
        popup.setFocusable(true);
        popup.setOutsideTouchable(isRestricted());
        ImageView image = (ImageView) layout.findViewById(R.id.confirmip);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = "";
                EditText edt;
                edt = (EditText) layout.findViewById(R.id.editTxtIp1);
                Ip1 = edt.getText().toString();
                edt = (EditText) layout.findViewById(R.id.editTxtIp2);
                Ip2 = edt.getText().toString();
                edt = (EditText) layout.findViewById(R.id.editTxtIp3);
                Ip3 = edt.getText().toString();
                edt = (EditText) layout.findViewById(R.id.editTxtIp4);
                Ip4 = edt.getText().toString();
                if (Ip1.equals("") || Ip2.equals("") || Ip3.equals("") || Ip4.equals("")) {
                } else {
                    PLCAddress = Ip1 + "." + Ip2 + "." + Ip3 + "." + Ip4;
                }
                edt = (EditText) layout.findViewById(R.id.ediTxtRack);
                temp = edt.getText().toString();
                if (temp.equals("")) {
                } else {
                    PLCRack = Integer.parseInt(temp);
                }

                edt = (EditText) layout.findViewById(R.id.ediTxtSlot);
                temp = edt.getText().toString();
                if (temp.equals("")) {
                } else {
                    PLCSlot = Integer.parseInt(temp);
                }
                popup.dismiss();
                View vew = AutomationActivity.this.getCurrentFocus();
                if (vew != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(vew.getWindowToken(), 0);
                }

            }
        });
        LinearLayout ln = (LinearLayout) view.getParent();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            popup.showAsDropDown(view);
        }else{
            popup.showAtLocation(layout, Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        }
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

    public void createLayout2(int param){
        LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        switch (param) {
            case 1:
                view = inflater.inflate(R.layout.layout_new_db, null);
                lt.addView(view);
                assignDynamicId(i, view, param,0);
                break;
            case 2:
                view = inflater.inflate(R.layout.layout_new_m, null);
                lt.addView(view);
                assignDynamicId(i,view,param,0);
                break;
            case 3:
                view = inflater.inflate(R.layout.layout_new_q, null);
                lt.addView(view);
                assignDynamicId(i,view,param,0);
                break;
            case 4:
                view = inflater.inflate(R.layout.layout_new_i, null);
                lt.addView(view);
                assignDynamicId(i,view,param,0);
                break;
        }
    }

    private void assignDynamicId(int i, View view, int param,int index) {

        // VIEW = DYNAMICL AYOUT
        Spinner spinner;
        view.setId(i);
        ImageView deletebutton=null,confirmbutton=null;

        switch(param) {
            case 1:
                spinner = (Spinner) view.findViewById(R.id.spinnerdb);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Spinner temp = (Spinner) view.getParent();
                        RelativeLayout parentview = (RelativeLayout) temp.getParent();
                        TextView text = (TextView) view;

                        EditText txt = (EditText) parentview.findViewById(R.id.bitdbx);
                        TextView dot = (TextView) findViewById(R.id.dot);
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
                //FORCE BUTTON
                //I XX . XX
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;

            case 2:
                //I XX . XX
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;
            case 3:
                //I XX . XX
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;
            case 4:
                //I XX . XX
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;
        }

    }

    View.OnClickListener confirmLayout(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView var_type,var_number;
                String param,byteM="",bitM="",DBnum="",DBbyte="",DBbit="";
                EditText editText;
                ImageView refreshbutton,forcebutton,editbutton;
                View view = null;

                RelativeLayout lt = (RelativeLayout) imageView.getParent();
                ViewGroup parent = (ViewGroup) lt.getParent();
                int idContainer = lt.getId();
                int index = parent.indexOfChild(lt);
                var_type = (TextView) lt.findViewById(R.id.var_type);
                param=var_type.getText().toString();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(param.equals("DB")){
                    Spinner spinner = (Spinner) lt.findViewById(R.id.spinnerdb);
                    String DB_type = spinner.getSelectedItem().toString();

                    if(DB_type.equals("DBX")) {
                        //Get View
                        view = inflater.inflate(R.layout.layout_boolean, null);
                        //Variable Type
                        var_type = (TextView) view.findViewById(R.id.var_type_confirmed_layout);
                        var_type.setText(param);
                        //Get Byte and Bit to build TextView Variable Type
                        editText = (EditText) lt.findViewById(R.id.dbnumber);
                        DBnum = editText.getText().toString();
                        editText = (EditText) lt.findViewById(R.id.bytedbx);
                        DBbyte = editText.getText().toString();
                        editText = (EditText) lt.findViewById(R.id.bitdbx);
                        DBbit = editText.getText().toString();
                        var_number = (TextView) view.findViewById(R.id.var_number_confirmed_layout);
                        TextView db_number = (TextView) view.findViewById(R.id.db_number_confirmed_layout);
                        TextView db_type = (TextView) view.findViewById(R.id.db_type_confirmed_layout);
                        db_number.setVisibility(View.VISIBLE);
                        db_type.setVisibility(View.VISIBLE);
                        db_number.setText(DBnum+".");
                        db_type.setText(DB_type.toString());
                        var_number.setText(DBbyte + "." + DBbit);
                        //Set position of Variable value Textview + ID
                        //EditBUTTON
                        editbutton = (ImageView) view.findViewById(R.id.editbutton);
                        editbutton.setOnClickListener(ClickEdit(editbutton));
                        //REFRESH BUTTON
                        refreshbutton = (ImageView) view.findViewById(R.id.refreshbut);
                        refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                    }
                    if(DB_type.equals("DBB")||DB_type.equals("DBD")||DB_type.equals("DBW")){
                        //Get View
                        view = inflater.inflate(R.layout.layout_boolean, null);
                        //Variable Type
                        var_type = (TextView) view.findViewById(R.id.var_type_confirmed_layout);
                        var_type.setText(param);
                        //Get Byte and Bit to build TextView Variable Type
                        editText = (EditText) lt.findViewById(R.id.dbnumber);
                        DBnum = editText.getText().toString();
                        editText = (EditText) lt.findViewById(R.id.bytedbx);
                        DBbyte = editText.getText().toString();
                        TextView db_number = (TextView) view.findViewById(R.id.db_number_confirmed_layout);
                        TextView db_type = (TextView) view.findViewById(R.id.db_type_confirmed_layout);
                        var_number = (TextView) view.findViewById(R.id.var_number_confirmed_layout);

                        db_number.setVisibility(View.VISIBLE);
                        db_type.setVisibility(View.VISIBLE);
                        db_number.setText(DBnum);
                        db_type.setText(DB_type.toString());
                        var_number.setText(DBbyte);
                        //Set position of Variable value Textview + ID
                        //EditBUTTON
                        editbutton = (ImageView) view.findViewById(R.id.editbutton);
                        editbutton.setOnClickListener(ClickEdit(editbutton));
                        //REFRESH BUTTON
                        refreshbutton = (ImageView) view.findViewById(R.id.refreshbut);
                        refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                    }
                }
                if(param.equals("Q")){
                    //Get View
                    view = inflater.inflate(R.layout.layout_boolean, null);
                    //Variable Type
                    var_type = (TextView) view.findViewById(R.id.var_type_confirmed_layout);
                    var_type.setText(param);
                    //Get Byte and Bit to build TextView Variable Type
                    editText = (EditText) lt.findViewById(R.id.qbyte);
                    byteM=editText.getText().toString();
                    editText = (EditText) lt.findViewById(R.id.qbit);
                    bitM=editText.getText().toString();
                    var_number = (TextView) view.findViewById(R.id.var_number_confirmed_layout);
                    var_number.setText(byteM + "." + bitM);
                    //Set position of Variable value Textview + ID
                    //EditBUTTON
                    editbutton = (ImageView)view.findViewById(R.id.editbutton);
                    editbutton.setOnClickListener(ClickEdit(editbutton));
                    //REFRESH BUTTON
                    refreshbutton = (ImageView) view.findViewById(R.id.refreshbut);
                    refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));
        }
                if(param.equals("M")){
                    //Get View
                    view = inflater.inflate(R.layout.layout_boolean, null);
                    //Variable Type
                    var_type = (TextView) view.findViewById(R.id.var_type_confirmed_layout);
                    var_type.setText(param);
                    //Get Byte and Bit to build TextView Variable Type
                    editText = (EditText) lt.findViewById(R.id.mbyte);
                    byteM=editText.getText().toString();
                    editText = (EditText) lt.findViewById(R.id.mbit);
                    bitM=editText.getText().toString();
                    var_number = (TextView) view.findViewById(R.id.var_number_confirmed_layout);
                    var_number.setText("" + byteM + "." + bitM);
                    //Set position of Variable value Textview + ID
                    //EditBURRON
                    editbutton = (ImageView)view.findViewById(R.id.editbutton);
                    editbutton.setOnClickListener(ClickEdit(editbutton));
                    //REFRESH BUTTON
                    refreshbutton = (ImageView) view.findViewById(R.id.refreshbut);
                    refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));


                }
                if(param.equals("I")){
                    //Get View
                    view = inflater.inflate(R.layout.layout_boolean, null);
                    //Variable Type
                    var_type = (TextView) view.findViewById(R.id.var_type_confirmed_layout);
                    var_type.setText(param);
                    //Get Byte and Bit to build TextView Variable Type
                    editText = (EditText) lt.findViewById(R.id.ibyte);
                    byteM=editText.getText().toString();
                    editText = (EditText) lt.findViewById(R.id.ibit);
                    bitM=editText.getText().toString();
                    var_number = (TextView) view.findViewById(R.id.var_number_confirmed_layout);
                    var_number.setText("" + byteM + "." + bitM);
                    //Set position of Variable value Textview + ID
                    //EditBURRON
                    editbutton = (ImageView)view.findViewById(R.id.editbutton);
                    editbutton.setOnClickListener(ClickEdit(editbutton));
                    //REFRESH BUTTON
                    refreshbutton = (ImageView) view.findViewById(R.id.refreshbut);
                    refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));

                    forcebutton = (ImageView) view.findViewById(R.id.forcebut);
                    forcebutton.setVisibility(View.INVISIBLE);
                    forcebutton.setEnabled(false);
                }
                view.setId(idContainer);
                parent.removeView(lt);
                parent.addView(view, index);
                View vew = AutomationActivity.this.getCurrentFocus();
                if (vew != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(vew.getWindowToken(), 0);
                }     }
        };
    }

    private View.OnClickListener ClickEdit(final ImageView editbutton) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView var_type;
                String param;
                ImageView deletebutton,confirmbutton;
                View view = null;

                RelativeLayout lt = (RelativeLayout) editbutton.getParent();
                ViewGroup parent = (ViewGroup) lt.getParent();
                int idContainer = lt.getId();
                int index = parent.indexOfChild(lt);
                var_type = (TextView) lt.findViewById(R.id.var_type_confirmed_layout);
                param=var_type.getText().toString();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if(param.equals("I")){
                    view = inflater.inflate(R.layout.layout_new_i, null);
                }
                if(param.equals("Q")){
                    view = inflater.inflate(R.layout.layout_new_q,null);
                }
                if(param.equals("DB")){
                    view = inflater.inflate(R.layout.layout_new_db,null);
                }
                if(param.equals("M")){
                    view = inflater.inflate(R.layout.layout_new_m,null);
                }

                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));
                view.setId(idContainer);
                parent.removeView(lt);
                parent.addView(view, index);
            }
        };
    }

    View.OnClickListener ClickDelete(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RelativeLayout rl = (RelativeLayout) imageView.getParent();
                LinearLayout lt = (LinearLayout) rl.getParent();
                lt.removeView(rl);
                }
        };
    }

    View.OnClickListener ClickRefresh(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                RelativeLayout rl = (RelativeLayout) imageView.getParent();
                int idParent = rl.getId();
                LinearLayout lt = (LinearLayout) rl.getParent();
                TextView variable_type_tv = (TextView) rl.findViewById(R.id.var_type_confirmed_layout);
                TextView variable_number_tv = (TextView) rl.findViewById(R.id.var_number_confirmed_layout);
                TextView variable_readwrite = (TextView) rl.findViewById(R.id.value_readwrite);
                String variable_type_string = variable_type_tv.getText().toString();
                String variable_number_String = variable_number_tv.getText().toString();
                String data_address_to_read=variable_type_string+variable_number_String;
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

                boolean isMerker=false,isInput=false,isDB=false,isOutput=false;
                if(variable_type_string.equals("M")) {
                    isMerker = true;
                }
                if(variable_type_string.equals("I")) {
                    isInput = true;
                }
                if(variable_type_string.equals("Q")){
                    isOutput=true;
                }
                if(variable_type_string.equals("DB")){
                    isDB = true;
                }
                //Define container to stock the parsed datas.
                ParseDataResult Result;
                //Call of one of the two functions depending of the data type
                if(isDB==true){
                    Result=parse_data_db(data_address_to_read);
                }else {
                    Result = parse_data_merker_IO(data_address_to_read, isMerker, isInput, isOutput);
                }
                //If an error occured during the data parsing, we update directly the outString and the errorMessage strings and display them without calling the readWriteData function.
                // In normal operation, if the data is parsed and read successfully, the update is done by the onPostExecute function of the PlcReadWrite class.
                if(!(Result.errorMessage.equals(""))){
                    valueOutputReadWritePlc=Result.errorMessage;
                }else {
                    readWriteData(Result, outString,outStringId,errorStringId, DataAsToBeForced,bitValueToForce,intValueToForce,floatValueToForce);
                }

                variable_readwrite.setText(valueOutputReadWritePlc);
            }


        };

    }

    View.OnClickListener ClickForceBoolean(final RelativeLayout ll, final Dialog dialog, final String variable_type, final String variable_number) {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Define container to stock the parsed datas.
                boolean isMerker=false, isInput=false, isOutput=false,DataAsToBeForced=true;
                int intValueToForce=0;
                float floatValueToForce=0;
                boolean bitValueToForce=false;
                String outString="";
                int outStringId=0;
                int errorStringId=outStringId;
                ParseDataResult Result;
                //Construction of the datas required to force
                //data address
                String data_address_to_read=variable_type+variable_number;
                //bitValuetoForce
                ImageView greenbut = (ImageView) dialog.findViewById(R.id.forcetrue);
                ImageView redbut = (ImageView) dialog.findViewById(R.id.forcefalse);
                // Data type
                TextView variable_type = (TextView) ll.findViewById(R.id.var_type_confirmed_layout);
                if (variable_type.getText().equals("M")){isMerker=true;}
                if (variable_type.getText().equals("Q")){isOutput=true;}

                if(greenbut.getDrawable().getConstantState().equals(R.drawable.gree_button_pressed) && redbut.getDrawable().getConstantState().equals(R.drawable.red_button) ){
                    bitValueToForce=true;
                }else{
                    if(redbut.getDrawable().getConstantState().equals(R.drawable.red_button_pressed) && greenbut.getDrawable().getConstantState().equals(R.drawable.green_button)){
                        bitValueToForce=false;
                    }else{}
                }

                Result = parse_data_merker_IO(data_address_to_read, isMerker, isInput, isOutput);

                //If an error occured during the data parsing, we update directly the outString and the errorMessage strings and display them without calling the readWriteData function.
                // In normal operation, if the data is parsed and read successfully, the update is done by the onPostExecute function of the PlcReadWrite class.
                if(!(Result.errorMessage.equals(""))){
                    valueOutputReadWritePlc=Result.errorMessage;
                } else {
                    readWriteData(Result, outString, outStringId, errorStringId, DataAsToBeForced, bitValueToForce, intValueToForce, floatValueToForce);
                }
                TextView variable_readwrite = (TextView) ll.findViewById(R.id.value_readwrite);
                variable_readwrite.setText(valueOutputReadWritePlc);
                dialog.dismiss();
            }
        };

    }

    View.OnClickListener ClickForce(final RelativeLayout ll, final Dialog dialog) {

        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Define container to stock the parsed datas.
                boolean isMerker=false, isInput=false, isOutput=false,DataAsToBeForced=true;
                int intValueToForce=0;
                float floatValueToForce=0;
                boolean bitValueToForce=false;
                String outString="";
                int outStringId=0;
                int errorStringId=outStringId;
                String variable_type, db_number, db_type, variable_number;
                TextView variable_type_tv, db_number_tv, db_type_tv, variable_number_tv;
                ParseDataResult Result;
                //Construction of the datas required to force
                //data address
                variable_number_tv = (TextView) ll.findViewById(R.id.var_number_confirmed_layout);
                db_number_tv = (TextView) ll.findViewById(R.id.db_number_confirmed_layout);
                db_type_tv = (TextView) ll.findViewById(R.id.db_type_confirmed_layout);
                variable_type_tv = (TextView) ll.findViewById(R.id.var_type_confirmed_layout);
                variable_type=variable_type_tv.toString();
                db_number=db_number_tv.toString();
                db_type=db_type_tv.toString();
                variable_number=variable_number_tv.toString();
                String data_address_to_read=variable_type+db_number+"."+db_type+variable_number;
                //bitValuetoForce
                EditText value_to_force_edt = (EditText) dialog.findViewById(R.id.editforce);
                if(db_type_tv.getText().equals("DBB")||db_type_tv.getText().equals("DBW")){intValueToForce=Integer.parseInt(value_to_force_edt.getText().toString());}
                if(db_type_tv.getText().equals("DBD")){floatValueToForce=Float.parseFloat(value_to_force_edt.getText().toString());}
                // Data type

                Result = parse_data_db(data_address_to_read);

                //If an error occured during the data parsing, we update directly the outString and the errorMessage strings and display them without calling the readWriteData function.
                // In normal operation, if the data is parsed and read successfully, the update is done by the onPostExecute function of the PlcReadWrite class.
                if(!(Result.errorMessage.equals(""))){
                    valueOutputReadWritePlc=Result.errorMessage;
                } else {
                    readWriteData(Result, outString, outStringId, errorStringId, DataAsToBeForced, bitValueToForce, intValueToForce, floatValueToForce);
                }
                TextView variable_readwrite = (TextView) ll.findViewById(R.id.value_readwrite);
                variable_readwrite.setText(valueOutputReadWritePlc);
                dialog.dismiss();
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


    public  class PlcReadWrite extends AsyncTask<ReadWritekParams, Void, String> {
        String outString;
        String errorMessage;
        int outStringId;
        int errorStringId;
        int res;

        @Override
        protected String doInBackground(ReadWritekParams... params) {

            byte[] data_1=new byte[2];
            byte[] data_2=new byte[2];
            byte[] data_3=new byte[4];
            int AreaType=0;
            try {
                client.SetConnectionType(S7.S7_BASIC);
                res = client.ConnectTo("192.168.1.100",0,3);

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
                            params[0].errorMessage = "ERR: Wrong input.";
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
                        params[0].errorMessage="ERR: Wrong input.";
                        params[0].outString="";
                    }

                    if(res!=0) {
                        params[0].errorMessage = "Data couldn't be read.";
                        params[0].outString = "";
                    }


                }else{
                    params[0].errorMessage="ERR: No connect.";
                    params[0].outString="";
                }

            } catch (Exception e) {
                params[0].errorMessage="ERR: No connect.";
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
                valueOutputReadWritePlc=outString;
            }else {
                valueOutputReadWritePlc=errorMessage;
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
            split1 = "[Q]";
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
