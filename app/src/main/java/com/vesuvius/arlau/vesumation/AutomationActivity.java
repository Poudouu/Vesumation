package com.vesuvius.arlau.vesumation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.vesuvius.arlau.vesumation.Moka7.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AutomationActivity extends AppCompatActivity {

    File file;
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

    Context context=this;
    UserDbHelper userDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

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

        read_infosIpFile();

        userDbHelper = new UserDbHelper(context);
        sqLiteDatabase = userDbHelper.getReadableDatabase();
        cursor=userDbHelper.getInformations(sqLiteDatabase);
        if(cursor.moveToFirst()){
            do{
                int view_id_db;
                int data_type;
                int db_type;
                String Byte_M;
                String Bit_M;
                String DB_num;
                String DB_byte;
                String DB_bit;

                view_id_db=cursor.getInt(0);
                data_type=cursor.getInt(1);
                db_type=cursor.getInt(2);
                Byte_M=cursor.getString(3);
                Bit_M=cursor.getString(4);
                DB_num=cursor.getString(5);
                DB_byte=cursor.getString(6);
                DB_bit=cursor.getString(7);

                switch (data_type){
                    case 1:
                        createLayout2(1,1,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                        // Bug fix. At the start up we need to delete the database row with the old id to replace it by the one with the new allocated dynamic id i.
                        deleteDbRow(view_id_db);
                        addInfoDb(data_type, db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit, i);
                        break;
                    case 2:
                        createLayout2(2,1,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                        // Bug fix. At the start up we need to delete the database row with the old id to replace it by the one with the new allocated dynamic id i.
                        deleteDbRow(view_id_db);
                        addInfoDb(data_type, db_type, Byte_M, Bit_M, DB_num, DB_byte, DB_bit, i);
                        break;
                    case 3:
                        createLayout2(3,1,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                        // Bug fix. At the start up we need to delete the database row with the old id to replace it by the one with the new allocated dynamic id i.
                        deleteDbRow(view_id_db);
                        addInfoDb(data_type, db_type, Byte_M, Bit_M, DB_num, DB_byte, DB_bit, i);
                        break;
                    case 4:
                        createLayout2(4, 1, db_type, Byte_M, Bit_M, DB_num, DB_byte, DB_bit);
                        // Bug fix. At the start up we need to delete the database row with the old id to replace it by the one with the new allocated dynamic id i.
                        deleteDbRow(view_id_db);
                        addInfoDb(data_type, db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit, i);
                        break;
                    default:
                        break;
                }
                i=i+20;

            }while(cursor.moveToNext());

        }
    }

    public void read_infosIpFile(){
        FileInputStream fileInputStream=null;
        byte [] inputBuffer = new byte[1024];
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "VisumationIPConfig.txt");
        String ip="";
        String rack="";
        String slot ="";

        //Read file infos
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(inputBuffer);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Parse string infos
            String infoStringRead = new String(inputBuffer, "UTF-8");
            String delims = "[\n]";
            String[] tokens = infoStringRead.split(delims);
            //Parse name string part
            ip=tokens[0];
            rack=tokens[1];
            slot=tokens[2];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        try {
            int Rack = Integer.parseInt(rack);
            PLCRack=Rack;
            int Slot = Integer.parseInt(slot);
            PLCSlot=Slot;
        }catch(java.lang.NumberFormatException e){
            e.printStackTrace();
        }

            PLCAddress=ip;
    }

    public void register_infos_IPConfig(String IpAdress, int slot, int rack){
        FileOutputStream fileOutputStream = null;
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "VisumationIPConfig.txt");

        String Slot=slot+"";
        String Rack=rack+"";

        //String infosStringWrite=IpAdress+"\n"+Slot+"\n"+Rack;
        String infosStringWrite=IpAdress+"\n"+Slot+"\n"+Rack+"\n";

        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(infosStringWrite.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();
    }

    public void addInfoDb(int Data_type, int BD_type,String Byte_M,String Bit_M,String DB_num,String DB_byte,String DB_bit, int view_id)
    {
        /*

        Function to add infos in the application database.

        Parameters:

        Integers!
        Data_type= 1 -> DB/ Data_type= 2 -> Q/ Data_type= 3 -> I/ Data_type= 4 -> M
        DB_type= 1 -> DBX/ DB_type= 2 -> DBB/ DB_type= 3 -> DBD/ DB_type= 4 -> DBW

        Strings!
        Byte_M -> If memento, input or output number of the byte
        Bit_M -> If memento, input or output, number of the bit
        DB_num -> If DB, number of the DB
        DB_byte -> If DB, number of the byte
        DB_bit -> If DB, number of the bit
         */
        userDbHelper = new UserDbHelper(context);
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.addInformations(Data_type, BD_type, Byte_M, Bit_M, DB_num, DB_byte, DB_bit, sqLiteDatabase, view_id);

        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data saved",Toast.LENGTH_LONG).show();

        userDbHelper.close();
    }

    public void deleteDbRow(int rowToDelete){
        userDbHelper = new UserDbHelper(context);
        sqLiteDatabase=userDbHelper.getWritableDatabase();
        userDbHelper.deleteInfo(rowToDelete, sqLiteDatabase);

        //For debbugging only
        //Toast.makeText(getApplicationContext(),"Data deleted",Toast.LENGTH_LONG).show();

        userDbHelper.close();
    }

    public void setIpAddress(View view){
        showPopup(AutomationActivity.this, p, view);
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
        String dbNum;
        RelativeLayout rl = (RelativeLayout) view.getParent();
        final Dialog dialog = new Dialog(cont);
        TextView variable_type_tv = (TextView) rl.findViewById(R.id.var_type_confirmed_layout);
        String variable_type = variable_type_tv.getText().toString();
        TextView variable_number_tv = (TextView) rl.findViewById(R.id.var_number_confirmed_layout);
        String variable_number = variable_number_tv.getText().toString();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TextView db_type_tv = (TextView) rl.findViewById(R.id.db_type_confirmed_layout);
        String db_type = db_type_tv.getText().toString();
        if(variable_type.equals("DB") ){
            TextView dbNumber = (TextView) rl.findViewById(R.id.db_number_confirmed_layout);
            dbNum = dbNumber.getText().toString();
        }else{
            dbNum="";
        }

        if (variable_type.equals("M")||variable_type.equals("Q") || db_type.equals("DBX")){
            dialog.setContentView(R.layout.pop_force_boolean);
            ImageView confirmForce = (ImageView) dialog.findViewById(R.id.confirmforce);
            confirmForce.setOnClickListener(ClickForceBoolean(rl, dialog, variable_type, variable_number, dbNum));
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
            confirmForce.setOnClickListener(ClickForce(dialog, rl,db_type,dbNum,variable_number));
        }
        if(variable_type.equals("DB") && (db_type.equals("DBD")))
        {
            dialog.setContentView(R.layout.pop_force);
            EditText edt = (EditText)dialog.findViewById(R.id.editforce);
            edt.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ImageView confirmForce = (ImageView) dialog.findViewById(R.id.confirmforce);
            confirmForce.setOnClickListener(ClickForce(dialog, rl, db_type, dbNum, variable_number));
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

        edt1 = (EditText) layout.findViewById(R.id.editTxtIp2);
        edt1.setText("");
        try {
            edt1.setText(Ip[1]);
            //Bug fix. Try to fill first IP[1], if not possible, we don't fill IP[0] with some shit.
            edt1 = (EditText) layout.findViewById(R.id.editTxtIp1);
            edt1.setText("");
            try {
                edt1.setText(Ip[0]);
            }catch(java.lang.ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        edt1 = (EditText) layout.findViewById(R.id.editTxtIp3);
        edt1.setText("");
        try {
            edt1.setText(Ip[2]);
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        edt1 = (EditText) layout.findViewById(R.id.editTxtIp4);
        edt1.setText("");
        try {
            edt1.setText(Ip[3]);
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        edt1 = (EditText) layout.findViewById(R.id.ediTxtRack);
        edt1.setText("");
        try {
            edt1.setText(String.valueOf(PLCRack));
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        edt1 = (EditText) layout.findViewById(R.id.ediTxtSlot);
        edt1.setText("");
        try {
            edt1.setText(String.valueOf(PLCSlot));
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
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

                register_infos_IPConfig(PLCAddress, PLCSlot, PLCRack);

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
                        createLayout2(1,0,0,"","","","","");
                        break;
                    case R.id.m_id:
                        createLayout2(2,0,0,"","","","","");
                        break;
                    case R.id.q_id:
                        createLayout2(3,0,0,"","","","","");
                        break;
                    case R.id.i_id:
                        createLayout2(4,0,0,"","","","","");
                        break;

                }
                i=i+20;
                return true;
            }
        });

        menu.show();
    }

    public void createLayout2(int param, int initDbFlag, int db_type,String Byte_M,String Bit_M,String DB_num,String DB_byte,String DB_bit){
        LinearLayout lt = (LinearLayout) findViewById(R.id.automation_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        switch (param) {
            case 1:
                view = inflater.inflate(R.layout.layout_new_db, null);
                lt.addView(view);
                assignDynamicId(i, view, param,0,initDbFlag,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                break;
            case 2:
                view = inflater.inflate(R.layout.layout_new_m, null);
                lt.addView(view);
                assignDynamicId(i, view, param,0,initDbFlag,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                break;
            case 3:
                view = inflater.inflate(R.layout.layout_new_q, null);
                lt.addView(view);
                assignDynamicId(i, view, param,0,initDbFlag,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                break;
            case 4:
                view = inflater.inflate(R.layout.layout_new_i, null);
                lt.addView(view);
                assignDynamicId(i, view, param,0,initDbFlag,db_type,Byte_M,Bit_M,DB_num,DB_byte,DB_bit);
                break;
        }
    }

    private void assignDynamicId(int i, View view, int param,int index, final int initDbFlag, final int db_type,String Byte_M,String Bit_M, final String DB_num, final String DB_byte, final String DB_bit) {

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

                        if (initDbFlag==1){
                            if(db_type==1){
                                text.setText("DBX");
                                temp.setSelection(0);
                            }
                            if(db_type==2){
                                text.setText("DBB");
                                temp.setSelection(1);

                            }
                            if(db_type==3){
                                text.setText("DBD");
                                temp.setSelection(3);

                            }
                            if(db_type==4){
                                text.setText("DBW");
                                temp.setSelection(2);

                            }
                        }
                        EditText txt = (EditText) parentview.findViewById(R.id.bitdbx);
                        TextView dot = (TextView) parentview.findViewById(R.id.dot);
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

                        if(initDbFlag==1) {
                            EditText bitdbx = (EditText) parentview.findViewById(R.id.bitdbx);
                            EditText bytedbx = (EditText) parentview.findViewById(R.id.bytedbx);
                            EditText dbnumber = (EditText) parentview.findViewById(R.id.dbnumber);
                            if (db_type == 1) {
                                bitdbx.setText(DB_bit);
                                bytedbx.setText(DB_byte);
                                dbnumber.setText(DB_num);
                            }
                            if (db_type == 2 || db_type==3 || db_type==4) {
                                bytedbx.setText(DB_byte);
                                dbnumber.setText(DB_num);
                            }
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
                //M XX . XX
                if(initDbFlag==1) {
                    EditText byteM = (EditText) view.findViewById(R.id.mbyte);
                    EditText bitM = (EditText) view.findViewById(R.id.mbit);

                    byteM.setText(Byte_M);
                    bitM.setText(Bit_M);
                }
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;
            case 3:
                //Q XX . XX
                if(initDbFlag==1) {
                    EditText byteQ = (EditText) view.findViewById(R.id.qbyte);
                    EditText bitQ = (EditText) view.findViewById(R.id.qbit);

                    byteQ.setText(Byte_M);
                    bitQ.setText(Bit_M);
                }
                deletebutton = (ImageView) view.findViewById(R.id.deletebut);
                deletebutton.setOnClickListener(ClickDelete(deletebutton));
                //CONFIRM LAYOUT
                confirmbutton = (ImageView) view.findViewById(R.id.confirmlayout);
                confirmbutton.setOnClickListener(confirmLayout(confirmbutton));

                break;
            case 4:
                //I XX . XX
                if(initDbFlag==1) {
                    EditText byteI = (EditText) view.findViewById(R.id.ibyte);
                    EditText bitI = (EditText) view.findViewById(R.id.ibit);

                    byteI.setText(Byte_M);
                    bitI.setText(Bit_M);
                }
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

                // Used to register infos in the database, see addInfo function for meaning.
                int data_type=0;
                int DB_db_type=0;

                RelativeLayout lt = (RelativeLayout) imageView.getParent();
                ViewGroup parent = (ViewGroup) lt.getParent();
                int idContainer = lt.getId();
                int index = parent.indexOfChild(lt);
                var_type = (TextView) lt.findViewById(R.id.var_type);
                try {
                    param = var_type.getText().toString();

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if(param.equals("DB")){
                        data_type=1;
                        Spinner spinner = (Spinner) lt.findViewById(R.id.spinnerdb);
                        String DB_type = spinner.getSelectedItem().toString();
                        if(DB_type.equals("DBX")) {
                            DB_db_type=1;
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

                            if (DB_type.equals("DBB")){
                                DB_db_type=2;
                            }
                            if (DB_type.equals("DBD")){
                                DB_db_type=3;
                            }
                            if (DB_type.equals("DBW")){
                                DB_db_type=4;
                            }
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
                            db_number.setText(DBnum+".");
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
                        data_type=3;
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
                        refreshbutton.setOnClickListener(ClickRefresh(refreshbutton));               }
                    if(param.equals("M")){
                        data_type=2;
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
                        data_type=4;
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
                    }
                    //Insert infos in the database after confirmation of the form.
                    // public void addInfo(int Data_type, int BD_type,String Byte_M,String Bit_M,String DB_num,String DB_byte,String DB_bit)
                    deleteDbRow(idContainer);
                    i=i+20;
                    addInfoDb(data_type, DB_db_type, byteM, bitM, DBnum, DBbyte, DBbit, i);

                }catch (java.lang.NullPointerException e){
                    e.printStackTrace();
                }
            }

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

                deleteDbRow(idContainer);
            }
        };
    }


    View.OnClickListener ClickDelete(final ImageView imageView){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RelativeLayout rl = (RelativeLayout) imageView.getParent();
                LinearLayout lt = (LinearLayout) rl.getParent();
                int rowToDelete =rl.getId();
                lt.removeView(rl);

                deleteDbRow(rowToDelete);
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

                TextView dbNumber = (TextView) rl.findViewById(R.id.db_number_confirmed_layout);
                TextView dbType = (TextView) rl.findViewById(R.id.db_type_confirmed_layout);

                String variable_type_string = variable_type_tv.getText().toString();
                String variable_number_String = variable_number_tv.getText().toString();

                //Will be used for Q/I and M, for DB, recalculated later.
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
                    data_address_to_read="DB"+dbNumber.getText()+dbType.getText()+variable_number_String;
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

    View.OnClickListener ClickForceBoolean(final RelativeLayout ll, final Dialog dialog, final String variable_type, final String variable_number, final String dbNum) {
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

                //An input can't be forced...
                isInput=false;
                if (variable_type.getText().equals("M")){
                    isMerker=true;
                }
                if (variable_type.getText().equals("Q")){
                    isOutput=true;
                }

                if(greenbut.getDrawable().getConstantState().equals(R.drawable.gree_button_pressed) && redbut.getDrawable().getConstantState().equals(R.drawable.red_button) ){
                    bitValueToForce=true;
                }else{
                    if(redbut.getDrawable().getConstantState().equals(R.drawable.red_button_pressed) && greenbut.getDrawable().getConstantState().equals(R.drawable.green_button)){
                        bitValueToForce=false;
                    }else{}
                }

                //Bug fix. Call the correct parse data if the data type is DB.
                if (variable_type.getText().equals("DB")){
                    data_address_to_read="DB"+dbNum+"DBX"+variable_number;
                    Result = parse_data_db(data_address_to_read);
                }else {
                    Result = parse_data_merker_IO(data_address_to_read, isMerker, isInput, isOutput);
                }

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

    View.OnClickListener ClickForce(final Dialog dialog, final RelativeLayout ll,final String dbType, final String dbNum, final String variable_number) {

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
                String data_address_to_read="DB"+dbNum+dbType+variable_number;
                //bitValuetoForce
                EditText value_to_force_edt = (EditText) dialog.findViewById(R.id.editforce);
                if(dbType.equals("DBB")||dbType.equals("DBW")){intValueToForce=Integer.parseInt(value_to_force_edt.getText().toString());}
                if(dbType.equals("DBD")){
                    floatValueToForce=Float.parseFloat(value_to_force_edt.getText().toString());
                }
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
                            params[0].errorMessage = "Wrong input.";
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
                        params[0].errorMessage="Wrong input.";
                        params[0].outString="";
                    }

                    if(res!=0) {
                        params[0].errorMessage = "Can't read data.";
                        params[0].outString = "";
                    }


                }else{
                    params[0].errorMessage="No connection";
                    params[0].outString="";
                }

            } catch (Exception e) {
                params[0].errorMessage="No connection";
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
            Result. errorMessage="Wrong input.";

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
            try {
                data_type_c = (token3[1] + "").charAt(0);
            }catch(java.lang.StringIndexOutOfBoundsException e){
                data_type_c='B';
            }
            Result.data_type=data_type_c+"";
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";
        }catch(java.lang.NullPointerException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";

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
            if(Result.data_type.equals("B")) {
                Word_s=token3[2];
            }
            try {
                Result.Word = Integer.parseInt(Word_s);
            }catch(java.lang.NumberFormatException e){
                e.printStackTrace();
            }
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";
        }catch (java.lang.NullPointerException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";
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
            Result.errorMessage="Wrong input.";
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
        try {
            token1 = token[0].split(split1);
            Word_s=token1[1];
            Result.Word = Integer.parseInt(Word_s);
        }catch(java.lang.NumberFormatException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";
        }catch(java.lang.ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            Result.errorMessage="Wrong input.";
        }
        return Result;
    }

}
