package com.example.user.coinque;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import helpers.MqttHelper;


public class fragment2 extends Fragment {
    MqttHelper mqttHelper;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView tv_target, tv_terkumpul;

    Button btn_buka;
    int target, terkumpul;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment2, container, false);

        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        tv_target = (TextView) v.findViewById(R.id.tv_target);
        tv_terkumpul = (TextView) v.findViewById(R.id.tv_terkumpul);

        target =  pref.getInt("target_shared", 0);
        terkumpul = pref.getInt("nominal_shared", 0);

        tv_target.setText("Rp "+target);
        tv_terkumpul.setText("Rp "+terkumpul);


        btn_buka = (Button) v.findViewById(R.id.buttonKirim);


        mqttHelper = new MqttHelper(getActivity().getApplicationContext());

        btn_buka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target =  pref.getInt("target_shared", 0);
                terkumpul = pref.getInt("nominal_shared", 0);
                if(target < terkumpul){
                    mqttHelper.mqttPublish("0");
                    Toast.makeText(getActivity().getApplicationContext(), "Celengan Terbuka", Toast.LENGTH_SHORT).show();
                    editor.putInt("target_shared", 0);
                    editor.putInt("nominal_shared", 0);
                    editor.commit();
                    Intent i = new Intent(getActivity().getApplicationContext(), Home.class);
                    startActivity(i);
                    getActivity().finish();

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Targetmu belum tercapai", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }






}
