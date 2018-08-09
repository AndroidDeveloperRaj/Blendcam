package com.appbasic.blendcam.opengl;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.appbasic.blendcam.R;

import java.io.File;
import java.util.ArrayList;

public class Utils {

    public static int framePosition = 0;
    public static Bitmap crop_Bitmap;
    public static boolean isStopVideo = false;
    public static int[] images = {R.drawable.glass1,R.drawable.glass2,R.drawable.glass3,R.drawable.glass4};
    public static int[] full_screen_images={
            R.drawable.hearts_00000,R.drawable.hearts_00001,R.drawable.hearts_00002,R.drawable.hearts_00003,R.drawable.hearts_00004,
            R.drawable.hearts_00005,R.drawable.hearts_00006,R.drawable.hearts_00007,R.drawable.hearts_00008,R.drawable.hearts_00009,
            R.drawable.hearts_00010,R.drawable.hearts_00011,R.drawable.hearts_00012,R.drawable.hearts_00013,R.drawable.hearts_00014,
            R.drawable.hearts_00015,R.drawable.hearts_00016,R.drawable.hearts_00017,R.drawable.hearts_00018,R.drawable.hearts_00019,
            R.drawable.hearts_00020,R.drawable.hearts_00021,R.drawable.hearts_00022,R.drawable.hearts_00023,R.drawable.hearts_00024,
            R.drawable.hearts_00025,R.drawable.hearts_00026,R.drawable.hearts_00027,R.drawable.hearts_00028,R.drawable.hearts_00029,
            R.drawable.hearts_00030,R.drawable.hearts_00031,R.drawable.hearts_00032,R.drawable.hearts_00033,R.drawable.hearts_00034,
            R.drawable.hearts_00035,R.drawable.hearts_00036,R.drawable.hearts_00037,R.drawable.hearts_00038,R.drawable.hearts_00039,
            R.drawable.hearts_00040

    };


    public static int[] old_photo_movie ={

            R.drawable.oldmovie_photo_00000,R.drawable.oldmovie_photo_00001,R.drawable.oldmovie_photo_00002,R.drawable.oldmovie_photo_00003,R.drawable.oldmovie_photo_00004,R.drawable.oldmovie_photo_00005,
            R.drawable.oldmovie_photo_00006,R.drawable.oldmovie_photo_00007,R.drawable.oldmovie_photo_00008,R.drawable.oldmovie_photo_00009,R.drawable.oldmovie_photo_00010,R.drawable.oldmovie_photo_00011,
            R.drawable.oldmovie_photo_00012,R.drawable.oldmovie_photo_00013,R.drawable.oldmovie_photo_00014,R.drawable.oldmovie_photo_00015,R.drawable.oldmovie_photo_00016,R.drawable.oldmovie_photo_00017,
            R.drawable.oldmovie_photo_00018,R.drawable.oldmovie_photo_00019,R.drawable.oldmovie_photo_00020,R.drawable.oldmovie_photo_00021,R.drawable.oldmovie_photo_00022,R.drawable.oldmovie_photo_00023,
            R.drawable.oldmovie_photo_00024,R.drawable.oldmovie_photo_00025,R.drawable.oldmovie_photo_00026,R.drawable.oldmovie_photo_00027,R.drawable.oldmovie_photo_00028,R.drawable.oldmovie_photo_00029,R.drawable.oldmovie_photo_00030,
            R.drawable.oldmovie_photo_00031,R.drawable.oldmovie_photo_00032,R.drawable.oldmovie_photo_00033,R.drawable.oldmovie_photo_00034,R.drawable.oldmovie_photo_00035,R.drawable.oldmovie_photo_00036,R.drawable.oldmovie_photo_00037,
            R.drawable.oldmovie_photo_00038,R.drawable.oldmovie_photo_00039,R.drawable.oldmovie_photo_00040,R.drawable.oldmovie_photo_00041,R.drawable.oldmovie_photo_00042,R.drawable.oldmovie_photo_00043,R.drawable.oldmovie_photo_00044,
            R.drawable.oldmovie_photo_00044,R.drawable.oldmovie_photo_00045,R.drawable.oldmovie_photo_00046,R.drawable.oldmovie_photo_00047,R.drawable.oldmovie_photo_00048,R.drawable.oldmovie_photo_00049,R.drawable.oldmovie_photo_00050,
            R.drawable.oldmovie_photo_00051,R.drawable.oldmovie_photo_00052,R.drawable.oldmovie_photo_00053,R.drawable.oldmovie_photo_00054,R.drawable.oldmovie_photo_00055,R.drawable.oldmovie_photo_00056,
            R.drawable.oldmovie_photo_00057,R.drawable.oldmovie_photo_00058,R.drawable.oldmovie_photo_00059,R.drawable.oldmovie_photo_00060,R.drawable.oldmovie_photo_00061,R.drawable.oldmovie_photo_00062,R.drawable.oldmovie_photo_00063,
            R.drawable.oldmovie_photo_00064,R.drawable.oldmovie_photo_00065,R.drawable.oldmovie_photo_00066,R.drawable.oldmovie_photo_00067,R.drawable.oldmovie_photo_00068,R.drawable.oldmovie_photo_00069,R.drawable.oldmovie_photo_00070,
            R.drawable.oldmovie_photo_00071,R.drawable.oldmovie_photo_00072,R.drawable.oldmovie_photo_00073,R.drawable.oldmovie_photo_00074,R.drawable.oldmovie_photo_00075,R.drawable.oldmovie_photo_00076,R.drawable.oldmovie_photo_00077,
            R.drawable.oldmovie_photo_00078,R.drawable.oldmovie_photo_00079,R.drawable.oldmovie_photo_00080,R.drawable.oldmovie_photo_00081,R.drawable.oldmovie_photo_00082,R.drawable.oldmovie_photo_00083,
            R.drawable.oldmovie_photo_00084,R.drawable.oldmovie_photo_00085,R.drawable.oldmovie_photo_00086,R.drawable.oldmovie_photo_00087,R.drawable.oldmovie_photo_00088,R.drawable.oldmovie_photo_00089,R.drawable.oldmovie_photo_00090,R.drawable.oldmovie_photo_00091,
            R.drawable.oldmovie_photo_00092,R.drawable.oldmovie_photo_00093,R.drawable.oldmovie_photo_00094,R.drawable.oldmovie_photo_00095,R.drawable.oldmovie_photo_00096,R.drawable.oldmovie_photo_00097,
            R.drawable.oldmovie_photo_00099,R.drawable.oldmovie_photo_00100


    };


}
