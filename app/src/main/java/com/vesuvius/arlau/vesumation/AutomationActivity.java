package com.vesuvius.arlau.vesumation;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
                params.addRule(RelativeLayout.RIGHT_OF,tv.getId());
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
                forcebutton.setId(i+INDEX_FORCE_BUTTON);
                //REFRESH BUTTON
                refreshbutton = (ImageView)findViewById(R.id.refreshbut);
                params = (RelativeLayout.LayoutParams)refreshbutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, forcebutton.getId());
                refreshbutton.setId(i + INDEX_REFRESH_BUTTON);
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
                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);

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
                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
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
                //FORCE BUTTON
                forcebutton = (ImageView) findViewById(R.id.forcebut);
                params = (RelativeLayout.LayoutParams)forcebutton.getLayoutParams();
                params.addRule(RelativeLayout.LEFT_OF, refreshbutton.getId());
                forcebutton.setId(i + INDEX_FORCE_BUTTON);
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

}
