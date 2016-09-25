package com.example.android.radiobuttoncenterimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

    private Activity mActivity;
    private Context mContext;
    private Resources mResources;
    private Callback mCallback;

    //Saved as member variable in onClick->requestImageCapture and used in onActivityForResult->createObjectBlock
    private File mCaptureImageFile;
    private String mCaptureImageFileName;
    private String mCaptureImageMimeType;

    //Saved as member variable in onActivityForResult->getImageNameAndMimetype and used in onActivityForResult->createObjectBlock
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
        mActivity = getActivity();
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

        PlatformUtils.checkAndAskForPermission(mActivity, android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE);
        Intent intent = new Intent(intentName);
        if (intent.resolveActivity(mActivity.getPackageManager()) == null) {
            Toast.makeText(mContext, mResources.getString(R.string.no_take_picture_app_installed), Toast.LENGTH_SHORT).show();
            return;
        }
        //Create a file where we will store the capture result
        mCaptureImageFileName = FileUtils.createImageFileName(mResources);
        mCaptureImageMimeType = getResources().getString(R.string.default_image_mimetype);
        //We create the file in Gallery directory because the camera app does not have access to our private cache, see https://developer.android.com/guide/topics/data/data-storage.html#filesInternal. We will later create a new file from the resulted bitmap and store it in our app local cache.
        try {
            mCaptureImageFile = FileUtils.createImageFileInAppGalleryDirectory(mContext, mCaptureImageFileName, mCaptureImageMimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Nebo", Thread.currentThread().getStackTrace()[2] + "mCaptureImageFile before capture path" + Uri.fromFile(mCaptureImageFile));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureImageFile));
        //This will start the camera capture appropriate app
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    private void requestImagePick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        PlatformUtils.checkAndAskForPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        intent.setType(mResources.getString(R.string.browse_images_input_type));
        //This will start the appropriate app able to pick a file
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Event if we don't call to super.onActivityResult the MainActivity.onActivityResult will be called that's why we can't have
        // requestCodes with same value in MainActivity and here.
        switch (requestCode) {
            case CAPTURE_IMAGE_REQUEST:
                if (resultCode == mActivity.RESULT_OK) {
                    mCaptureImageFile = resizeImageFile(mCaptureImageFile);
                    File localImageFile = saveGalleryImageFileInLocalCache(mCaptureImageFile, mCaptureImageFileName, mCaptureImageMimeType);
                    mCaptureImageFile.delete();             //Removed gallery file
                    mCaptureImageFile = localImageFile;     //Made mCaptureImageFile point to local file
                    addBlockCapturedImage(mCaptureImageFile, mCaptureImageFileName, mCaptureImageMimeType);
                    return;
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == mActivity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    getImageNameAndMimetype(imageUri); //Will set mPickImageFileName and mPickImageMimeType
                    File pickImageFile = saveGalleryImageFileInLocalCache(imageUri, mPickImageFileName, mPickImageMimeType);
                    pickImageFile = resizeImageFile(pickImageFile);
                    addBlockPickedImage(pickImageFile, mPickImageFileName, mPickImageMimeType);
                }
                break;
            default:
                break;
        }
    }

    private File saveGalleryImageFileInLocalCache(File externalImageFile, String filename, String
            mimeType) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(externalImageFile.getPath());
            File localFile = FileUtils.createImageFileInAppCacheDirectory(mContext, filename, mimeType);
            return FileUtils.saveBitmapInFile(bitmap, localFile, mimeType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File saveGalleryImageFileInLocalCache(Uri externalImageUri, String filename, String mimeType) {
        try {
            Bitmap pickImageBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), externalImageUri);
            File localFile = FileUtils.createImageFileInAppCacheDirectory(mContext, filename, mimeType);
            return FileUtils.saveBitmapInFile(pickImageBitmap, localFile, mimeType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File resizeImageFile(File file) {
        int targetH = mCallback.getTargetImageViewHeight();
        int targetW = mCallback.getTargetImageViewWidth();
        FileUtils.resizeImageFile(file, targetW, targetH);
        return file;
    }


    private void getImageNameAndMimetype(Uri uri) {
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
    }

    private void addBlockCapturedImage(File file, String filename, String mimeType) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        mCallback.addBlock(bitmap);
        mCallback.createObjectBlock(SKETCH_BACKEND_ID, filename, Uri.fromFile(mCaptureImageFile).toString(), mimeType);
    }

    private void addBlockPickedImage(File pickImageFile, String pickImageFileName, String mimeType) {
        String imageUriPath = Uri.fromFile(pickImageFile).toString();
        mCallback.addBlock(BitmapFactory.decodeFile(imageUriPath));
        mCallback.createObjectBlock(SKETCH_BACKEND_ID, pickImageFileName, imageUriPath, mimeType);
    }

}
