package com.example.user.coinque;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import helpers.MqttHelper;
import pl.droidsonroids.gif.GifTextView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.os.Build;
import android.view.Gravity;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;


public class fragment1 extends Fragment {

    MqttHelper mqttHelper;
    TextView tv_output;
    CountDownTimer yourCountDownTimer;
    GifTextView gifku = null;
    public static final int NOTIFICATION_ID = 1;
    MediaPlayer mp;

    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private Context mContext;
    private Activity mActivity;

    ImageView wadahpakan;
    RadioGroup radiobuttonnominal;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView tv_nominal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment1, container, false);
        mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.cat);

        // Get the application context
        mContext = getActivity().getApplicationContext();

        // Get the activity
        mActivity = getActivity();

        // Get the widgets reference from XML layout
        mRelativeLayout = (RelativeLayout) v.findViewById(R.id.rl);

        wadahpakan = (ImageView) v.findViewById(R.id.wadahPakan);
        radiobuttonnominal = (RadioGroup) v.findViewById(R.id.radioGroupNb);

        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        tv_nominal = (TextView) v.findViewById(R.id.tv_nominal);

        update_nominal();

        tv_output = (TextView) v.findViewById(R.id.data);
        gifku = v.findViewById(R.id.gifku);
//        tv_output.setText("1");
        startMqtt();
        nunggu();
        return v;
    }

    public void update_nominal(){
        int nominal_sementara = pref.getInt("nominal_shared", 0);
        int nominal_target = pref.getInt("target_shared", 0);
        tv_nominal.setText("Rp. "+nominal_sementara+" / Rp. "+ nominal_target );
    }

    public void nunggu(){
        if(yourCountDownTimer != null){
            yourCountDownTimer.cancel();
        }
        yourCountDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                gifku.setBackgroundResource(R.drawable.b3);
//                tv_output.setText("0");
                wadahpakan.setBackgroundResource(R.drawable.tempatmakan);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        yourCountDownTimer.cancel();
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getActivity().getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                gifku.setBackgroundResource(R.drawable.a1);
                nunggu();
                tampilnotification();
                if(mp.isPlaying()){
                    mp.stop();
                    mp = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.cat);
                    mp.start();
                }else {
                    mp.start();
                }
                wadahpakan.setBackgroundResource(R.drawable.tempatmakanfull);
                openDialog();
                tv_output.setText("1");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }


    public void tampilnotification(){

        Intent intent;
        intent = new Intent(getActivity().getApplicationContext(), Home.class);
        //menginisialiasasi intent
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext())
                .setSmallIcon(R.drawable.ic_menu_camera)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)//menghapus notif
                .setContentText("Segera hubungi Teman dekat/tetangga untuk mengechek BOX")
                .setContentTitle("Guncangan terdeteksi pada Box");

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public void openDialog() {
//        final Dialog dialog = new Dialog(getActivity().getApplicationContext()); // Context, this, etc.
//        dialog.setContentView(R.layout.dialog_demo);
//        dialog.setTitle("Masukkan Nominal");
//        dialog.show();

//        mPopupWindow.dismiss();

        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        final View customView = inflater.inflate(R.layout.custom_layout,null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        Button button_nominal = (Button) customView.findViewById(R.id.btn_simpan_nominal);
        button_nominal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radiobuttonnominal = (RadioGroup) customView.findViewById(R.id.radioGroupNb);
                int terpilih = radiobuttonnominal.getCheckedRadioButtonId();
                RadioButton itemTerpilih = (RadioButton) customView.findViewById(terpilih);
                String nominal = itemTerpilih.getText().toString();
                int nominalInt = Integer.parseInt(nominal);
                int nominal_sementara = pref.getInt("nominal_shared", 0);
                nominalInt += nominal_sementara;
                Toast.makeText(mContext, "Horaay aku menabung "+nominal, Toast.LENGTH_SHORT).show();
                editor.putInt("nominal_shared", nominalInt); // Storing integer
                editor.commit(); // commit changes
                update_nominal();
                mPopupWindow.dismiss();

            }
        });


        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);


    }


    public void dismiss(View v){
        mPopupWindow.dismiss();
    }



}
