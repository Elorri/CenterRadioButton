package com.example.android.radiobuttoncenterimage;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {


    public static File getExternalPictureStorageDirectory(Resources resources, String appExternalDirectoryName) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //External storage is not mounted
            return null;
        }

        File storageDir = getPictureDirectory(resources, appExternalDirectoryName);
        if (storageDir != null) {
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    //failed to create directory
                    return null;
                }
            }
        }
        return storageDir;
    }


    private static File getPictureDirectory(Resources resources, String albumName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        } else {
            // Standard storage location for digital camera files
            final String CAMERA_DIR = resources.getString(R.string.standard_camera_directory);
            return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
        }
    }


    public static String createImageFileName(Resources resources) {
        String timeStamp = new SimpleDateFormat(resources.getString(R.string.date_format)).format(new Date());
        String imageNamePrefix = resources.getString(R.string.image_name_prefix);
        return imageNamePrefix + timeStamp;
    }

    public static File createImageFileInAppGalleryDirectory(Context context, String
            imageFileName, String mimeType) throws IOException {
        Resources resources = context.getResources();
        String imageNameSuffix = resources.getString(R.string.image_name_suffix, MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType));
        File appGalleryImagesDirectory = FileUtils.getExternalPictureStorageDirectory(resources, resources.getString(R.string.nebo_album));
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "appGalleryImagesDirectory" + appGalleryImagesDirectory);
        return File.createTempFile(imageFileName, imageNameSuffix, appGalleryImagesDirectory);
    }

    public static File createImageFileInAppCacheDirectory(Context context, String imageFileName, String mimeType) throws IOException {
        String imageNameSuffix = context.getResources().getString(R.string.image_name_suffix, MimeTypeMap.getSingleton
                ().getExtensionFromMimeType(mimeType));
        File appImagesDirectory = context.getCacheDir();
        return File.createTempFile(imageFileName, imageNameSuffix, appImagesDirectory);
    }

    public static File scaleToImageViewSize(File imageFile, int targetW, int targetH) {
                /*This function has been entiery copied from Google Sample PhotoIntentActivity https://developer.android.com/training/camera/photobasics.html*/
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        String imagePath = imageFile.getPath();

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        BitmapFactory.decodeFile(imagePath, bmOptions);
        return imageFile;
    }

    public static File saveBitmapInFile(Bitmap bitmap, File file, String mimetype) {
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(mimetype.equals("image/jpeg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
