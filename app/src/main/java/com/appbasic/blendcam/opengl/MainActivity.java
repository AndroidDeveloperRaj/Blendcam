package com.appbasic.blendcam.opengl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.appbasic.blendcam.R;
import com.appbasic.blendcam.opengl.CameraView;

import static com.appbasic.blendcam.opengl.ParticlesRenderer.rotate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    public static int screenWidth,screenHeight;

    private RelativeLayout main;
    CameraView cameraview;

    Button record,takesnap,switchcam,button_environment,button_sticker;


    public static float progress;

   public static  boolean record_start=false;
    public static boolean takesnapnow=false;
    public static boolean seebarprogresschanged=false;
    public static boolean environment=true;
    public static boolean sticker=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics screenMatrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screenMatrics);
        screenWidth = screenMatrics.widthPixels;
        screenHeight = screenMatrics.heightPixels;


        main=(RelativeLayout)findViewById(R.id.main);
        cameraview =new CameraView(MainActivity.this);
        main.addView(cameraview);


        record =(Button)findViewById(R.id.record);
        takesnap=(Button)findViewById(R.id.take_snap);
        switchcam=(Button)findViewById(R.id.switch_cam);
        button_environment=(Button)findViewById(R.id.button_environment);
        button_sticker=(Button)findViewById(R.id.button_sticker);

        record.setOnClickListener(this);
        takesnap.setOnClickListener(this);
        switchcam.setOnClickListener(this);
        button_environment.setOnClickListener(this);
        button_sticker.setOnClickListener(this);



    }


    @Override
    protected void onResume() {

        super.onResume();
        cameraview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraview.onPause();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.record :

                               if(!record_start){

                                   cameraview.startVideo();
                                   record.setText("Stop");
                                   record_start=true;
                                }

                                else{
                                   cameraview.stopVideo();
                                   record.setText("Start");
                                   record_start=false;
                               }

                break;

            case R.id.take_snap :

                takesnapnow=true;
                break;

            case R.id.switch_cam :
                cameraview.switch_camera();

                break;

            case R.id.button_environment:

              /*  if(environment && !sticker){
                    button_environment.setText("Enable Environment");
                    environment=false;
                    sticker=true;
                }
                else if(!environment && sticker){
                    button_environment.setText("Disable Environment");
                    environment=true;
                    sticker=false;
                }*/
                environment=true;
                break;


            case R.id.button_sticker:


              ParticlesRenderer.touchedX = 0;
                ParticlesRenderer.touchedY = 0;
                ParticlesRenderer.touchedZ = 1f;
                ParticlesRenderer.rotate=0f;
              /*  if(!sticker){
                    button_environment.setText("Disable Stiker");
                    sticker=true;
                    environment=false;
                }
                else{
                    button_environment.setText("Enable Sticker");
                    sticker=false;
                    environment=true;
                }*/
                sticker=true;
                break;

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        seebarprogresschanged=true;
        if(i>1){
            progress=(float)(i/10.0);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
