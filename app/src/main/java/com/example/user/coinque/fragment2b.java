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
import android.widget.Toast;

import helpers.MqttHelper;


public class fragment2b extends Fragment {

    MqttHelper mqttHelper;

    EditText et_target, et_target_hari;
    Button btn_pasang_target;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment2b, container, false);
        et_target = (EditText) v.findViewById(R.id.et_target);
        et_target_hari = v.findViewById(R.id.et_target_hari);
        btn_pasang_target = (Button) v.findViewById(R.id.btn_pasang_target);
        mqttHelper = new MqttHelper(getActivity().getApplicationContext());


        btn_pasang_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                String target = et_target.getText().toString();
                String target_hari = et_target_hari.getText().toString();

                int targetInt = Integer.parseInt(target);
                int target_hari_int = Integer.parseInt(target_hari);

                editor = pref.edit();
                editor.putInt("target_shared", targetInt);
                editor.putInt("target_hari_shared", target_hari_int);
                editor.commit(); // commit changes
                mqttHelper.mqttPublish("1");
                Toast.makeText(getActivity().getApplicationContext(), "Set Target Berhasil", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity().getApplicationContext(), Home.class);
                startActivity(i);
                getActivity().finish();

            }
        });



        return v;
    }


}
