package com.goodcom.printer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.goodcom.gcprinter.GcPrinterUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GcPrinterDemo extends Activity {
    private final static int OPEN_PICTURE = 1;

    EditText mTvTitle;
    EditText mEdText;
    EditText mTvItem1Number;
    EditText mTvItem1Name;
    EditText mTvItem1Amt;
    EditText mTvItem1Opt1Name;
    EditText mTvItem1Opt1Amt;
    EditText mTvItem1Opt2Name;
    EditText mTvItem1Opt2Amt;
    EditText mTvItem2Number;
    EditText mTvItem2Name;
    EditText mTvItem2Amt;
    EditText mTvItem2Opt1Name;
    EditText mTvItem2Opt1Amt;
    EditText mTvBarcode;
    Button mBtnBmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvTitle = findViewById(R.id.ticket_title);
        mTvItem1Number =findViewById(R.id.item1_left);
        mTvItem1Name =findViewById(R.id.item1_mid);
        mTvItem1Amt =findViewById(R.id.item1_right);
        mTvItem2Number =findViewById(R.id.item2_left);
        mTvItem2Name =findViewById(R.id.item2_mid);
        mTvItem2Amt =findViewById(R.id.item2_right);
        mTvItem1Opt1Name =findViewById(R.id.item1_opt1_name);
        mTvItem1Opt1Amt =findViewById(R.id.item1_opt1_amt);
        mTvItem1Opt2Name = findViewById(R.id.item1_opt2_name);
        mTvItem1Opt2Amt = findViewById(R.id.item1_opt2_amt);
        mTvItem2Opt1Name = findViewById(R.id.item2_opt1_name);
        mTvItem2Opt1Amt = findViewById(R.id.item2_opt1_amt);
        mTvBarcode=findViewById(R.id.barcode);

        mEdText = findViewById(R.id.ed_text);
        Button btPrint = findViewById(R.id.btn_print);
        mBtnBmp = findViewById(R.id.btn_bmp);


        btPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GcPrinterUtils.drawCustom(mTvTitle.getText().toString(),GcPrinterUtils.fontBig,GcPrinterUtils.alignCenter);
                GcPrinterUtils.drawOneLine();
                GcPrinterUtils.drawNewLine();
                GcPrinterUtils.drawText(mTvItem1Number.getText().toString(),GcPrinterUtils.fontSmallBold,
                        mTvItem1Name.getText().toString(),GcPrinterUtils.fontSmallBold,
                        mTvItem1Amt.getText().toString(),GcPrinterUtils.fontSmallBold);
                GcPrinterUtils.drawLeftRight(mTvItem1Opt1Name.getText().toString(),0,
                        mTvItem1Opt1Amt.getText().toString(),0);
                GcPrinterUtils.drawLeftRight(mTvItem1Opt2Name.getText().toString(),0,
                        mTvItem1Opt2Amt.getText().toString(),0);
                GcPrinterUtils.drawNewLine();
                GcPrinterUtils.drawText(mTvItem2Number.getText().toString(),GcPrinterUtils.fontSmallBold,
                        mTvItem2Name.getText().toString(),GcPrinterUtils.fontSmallBold,
                        mTvItem2Amt.getText().toString(),GcPrinterUtils.fontSmallBold);
                GcPrinterUtils.drawLeftRight(mTvItem2Opt1Name.getText().toString(),0,
                        mTvItem2Opt1Amt.getText().toString(),0);
                GcPrinterUtils.drawNewLine();
                GcPrinterUtils.drawBarcode(mTvBarcode.getText().toString(),GcPrinterUtils.alignCenter,GcPrinterUtils.barcodeQrCode);
                GcPrinterUtils.drawOneLine();
                GcPrinterUtils.drawCustom(mEdText.getText().toString(),0,0);
                GcPrinterUtils.drawOneLine();
                GcPrinterUtils.drawCustom("Thanks!",0,GcPrinterUtils.alignCenter);
                GcPrinterUtils.printText(getApplicationContext(),true);
            }
        });

        mBtnBmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, OPEN_PICTURE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(GcPrinterDemo.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GcPrinterDemo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    String path;
    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
             case OPEN_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bmp=null;
                    path=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                        bmp=readBmpApiQ(this,uri);
                    }
                    else {
                        if ("file".equalsIgnoreCase(uri.getScheme())) {
                            path = uri.getPath();
                        }
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            path = getPath(this, uri);
                        } else {
                            path = getRealPathFromURI(uri);
                        }
                    }
                    if(path!=null) {
                        bmp = getLoacalBitmap(path);
                    }
                    if(bmp!=null) {
                        GcPrinterUtils.printBitmap(getApplicationContext(),bmp,GcPrinterUtils.alignCenter,true);
                    }
                    else {
                        Toast.makeText(GcPrinterDemo.this, "Fail to read picture file", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }

    }
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * Android 10
     * @param context context
     * @param uri uri
     * @return bitmap
     */
    private static Bitmap readBmpApiQ(Context context, Uri uri) {

        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            try {
                FileInputStream fis = new FileInputStream(uri.getPath());
                return BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = context.getContentResolver();
            @SuppressLint("Recycle") Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                Bitmap bitmap=null;
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(is);
                } catch ( IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
        }
        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}