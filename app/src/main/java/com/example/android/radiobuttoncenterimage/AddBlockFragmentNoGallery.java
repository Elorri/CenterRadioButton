package com.example.android.radiobuttoncenterimage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Elorri on 25/09/2016.
 */
public class AddBlockFragmentNoGallery extends Fragment implements View.OnClickListener {

    // FIXME should be provided by Math, Diagram, SNT layers
    private static final String SKETCH_BACKEND_ID = "com.myscript.snt.drawing";
    private static final String MATH_BACKEND_ID = "com.myscript.math";
    private static final String DIAGRAM_BACKEND_ID = "com.myscript.diagram";

    //Those 2 requestcodes should be distinct from MainActivity.MAIN_ACTIVITY_REQUEST
    private static final int CAPTURE_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private Resources mResources;
    private Callback mCallback;
    private Context mContext;

    //Saved as member variable in onClick->requestImageCapture and used in onActivityForResult
    private File mCaptureImageFile;
    private String mCaptureImageFileName;
    private String mCaptureImageMimeType;

    //Saved as member variable in onClick->requestImagePick and used in onActivityForResult
    private File mPickImageFile;
    private String mPickImageFileName;
    private String mPickImageMimeType;


    //Use by EditingFragment
    public interface Callback {

        void createAndAddBlock(String blockId);

        void createObjectBlock(String blockId, String filename, String url, String mimeType);

        int getTargetImageViewHeight();

        int getTargetImageViewWidth();

        void closeWindowsPopup();

        void addBlock(Bitmap bitmap);
    }


    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mResources = mContext.getResources();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_add_block_popupwindow, container);
        AddBlockView addBlockView = (AddBlockView) view.findViewById(R.id.add_block_view);
        addBlockView.setRepresentation(mResources.getString(R.string.addblockview_vertical_representation));
        addBlockView.setAddBlockFragment(this);
        return view;
    }


    public void onClick(View view) {
        mCallback.closeWindowsPopup();
        switch (view.getId()) {
            case R.id.action_camera:
                requestImageCapture();
                return;
            case R.id.action_picture:
                requestImagePick();
                return;
            case R.id.action_sketch:
                mCallback.createAndAddBlock(SKETCH_BACKEND_ID);
                return;
            case R.id.action_diagram:
                mCallback.createAndAddBlock(DIAGRAM_BACKEND_ID);
                return;
            case R.id.action_math:
                mCallback.createAndAddBlock(MATH_BACKEND_ID);
                return;
            default:
                // unknown button, nothing to do
                return;
        }
    }

    private void requestImageCapture() {
        String intentName = MediaStore.ACTION_IMAGE_CAPTURE;

        checkAndAskForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Intent intent = new Intent(intentName);
        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            Toast.makeText(mContext, mResources.getString(R.string.no_take_picture_app_installed), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Create a file where we will store the capture result
            mCaptureImageFileName = createFileName();
            mCaptureImageMimeType = getResources().getString(R.string.default_image_mimetype);
            //We create the file in Gallery directory because the camera app does not have access to our private cache. We will later create a new file from the resulted bitmap and store it in our app local cache.
            mCaptureImageFile = createImageFileInGalleryDirectory(mCaptureImageFileName, mCaptureImageMimeType);
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "mCaptureImageFile before capture path" + Uri.fromFile(mCaptureImageFile));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //This will start the camera capture appropriate app
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    private String createFileName() {
        String timeStamp = new SimpleDateFormat(mResources.getString(R.string.date_format)).format(new Date());
        String imageNamePrefix = mResources.getString(R.string.image_name_prefix);
        return imageNamePrefix + timeStamp;
    }


    private void requestImagePick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        checkAndAskForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        intent.setType(mResources.getString(R.string.browse_images_input_type));
        //This will start the appropriate app able to pick a file
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void checkAndAskForPermission(String permission) {
        //Before Marshmallow api 23 all permissions were requested at app first installed, asking permission dynamically wasn't possible
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        //The user has already granted the permission we need. We can move on.
        if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //The user has not granted the permission we need. We ask him to do so.
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PackageManager.PERMISSION_GRANTED);
    }


    private File createImageFileInGalleryDirectory(String imageFileName, String mimeType) throws IOException {
        String imageMimeTypePrefix = getResources().getString(R.string.mimetype_image_prefix);
        String mimeTypeSufix = mimeType.substring(imageMimeTypePrefix.length(), mimeType.length());
        String imageNameSuffix = mResources.getString(R.string.image_name_suffix, mimeTypeSufix);
        File cameraDirectoryAlbumNebo = FileUtils.getExternalPictureStorageDirectory(mResources, mResources.getString(R.string.nebo_album));
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "cameraDirectoryAlbumNebo" + cameraDirectoryAlbumNebo);
        File imageF = File.createTempFile(imageFileName, imageNameSuffix, cameraDirectoryAlbumNebo);
        return imageF;
    }

    private File createImageFileInCacheDirectory(String imageFileName, String mimeType) throws IOException {
        String imageNameSuffix = mResources.getString(R.string.image_name_suffix, MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType));
        File albumF = mContext.getCacheDir();
        File imageF = File.createTempFile(imageFileName, imageNameSuffix, albumF);
        return imageF;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Event if we don't call to super.onActivityResult the MainActivity.onActivityResult will be called that's why we can't have
        // requestCodes with same value in MainActivity and here.
        switch (requestCode) {
            case CAPTURE_IMAGE_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    addBlockCapturedImage(mCaptureImageFile, mCaptureImageFileName, mCaptureImageMimeType);
                    return;
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri imageUri = data.getData();
                    getImageNameMimetypeAndSize(imageUri);
                    addBlockPickedImage(mPickImageFile, mPickImageFileName, mPickImageMimeType);
                }
                break;
            default:
                break;
        }
    }

    private void getImageNameMimetypeAndSize(Uri uri) {
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "uri " + uri);
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE};
        final int columnPath = 0;
        final int columnName = 1;
        final int columnMimeType = 2;
        final int columnSize = 3;

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "cursor.count " + cursor.getCount());
        try {
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "");
            if (cursor.moveToFirst()) {
                //cursor.getString(columnPath); //Return null because no right see https://developer.android.com/reference/android/provider/MediaStore.MediaColumns.html#DATA
                String pickImageFileNameWithExtension = cursor.getString(columnName);
                mPickImageMimeType = cursor.getString(columnMimeType);
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mPickImageMimeType);
                mPickImageFileName = pickImageFileNameWithExtension.substring(0, pickImageFileNameWithExtension.length() - extension.length() - 1);
                Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "image size " + cursor.getString(columnSize));
            }
        } finally {
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "");
            cursor.close();
        }

        //Because mPickImagePathFile we this turn around
        try {
            Bitmap pickImageBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "pickImageBitmap " + pickImageBitmap);
            File localFile = createImageFileInCacheDirectory(mPickImageFileName, mPickImageMimeType);
            mPickImageFile = saveBitmapInFile(pickImageBitmap, localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File saveBitmapInFile(Bitmap bmp, File file) {
        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void addBlockCapturedImage(File file, String filename, String mimeType) {
        int targetH = mCallback.getTargetImageViewHeight();
        int targetW = mCallback.getTargetImageViewWidth();
        String imagePath = file.getPath();
        Bitmap bitmap = scaleToImageViewSize(imagePath, targetW, targetH);
        try {
            File localfile = createImageFileInCacheDirectory(filename, mimeType);
            mCaptureImageFile = saveBitmapInFile(bitmap, localfile);
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "after capture  mCaptureImageFile path " + Uri.fromFile(mCaptureImageFile));
            //now that our picture is saved locally, we can delete the public picture
            Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "to be deleted " + file.toString());
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "will add to galery  mCaptureImageFile path " + mCaptureImageFile.getPath());
        mCallback.addBlock(bitmap);
        mCallback.createObjectBlock(SKETCH_BACKEND_ID, filename, Uri.fromFile(mCaptureImageFile).toString(), mimeType);

    }


    private Bitmap scaleToImageViewSize(String photoPath, int targetW, int targetH) {
        /*This function has been entiery copied from Google Sample PhotoIntentActivity https://developer.android.com/training/camera/photobasics.html*/
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
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
        return BitmapFactory.decodeFile(photoPath, bmOptions);

    }


    private void addBlockPickedImage(File pickImageFile, String pickImageFileName, String mimeType) {
        String imagePath = Uri.fromFile(pickImageFile).toString();
        mCallback.addBlock(BitmapFactory.decodeFile(imagePath));
        mCallback.createObjectBlock(SKETCH_BACKEND_ID, pickImageFileName, imagePath, mimeType);
    }

}
