package com.example.android.radiobuttoncenterimage;


import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class FileUtils {


    public static File getExternalPictureStorageDirectory(Resources resources, String   appExternalDirectoryName)
    {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            //External storage is not mounted
            return null;
        }

        File storageDir = getPictureDirectory(resources, appExternalDirectoryName);
        if (storageDir != null)
        {
            if (!storageDir.mkdirs())
            {
                if (!storageDir.exists())
                {
                    //failed to create directory
                    return null;
                }
            }
        }
        return storageDir;
    }



    private static File getPictureDirectory(Resources resources, String albumName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
        {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        }
        else
        {
            // Standard storage location for digital camera files
            final String CAMERA_DIR = resources.getString(R.string.standard_camera_directory);
            return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
        }
    }
}
