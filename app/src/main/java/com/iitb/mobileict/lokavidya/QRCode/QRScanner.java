package com.iitb.mobileict.lokavidya.QRCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.iitb.mobileict.lokavidya.ui.VideoPlayerActivity;

public class QRScanner extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanQR();
    }

    //product qr code mode
    public void scanQR() {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(QRScanner.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();

               /* *//*Uri uri = Uri.parse(contents);
                Intent intent_vid = new Intent(Intent.ACTION_VIEW, uri);*//*


                try {
                    startActivity(intent_vid);
                }
                catch(Exception e){

                    Toast t=Toast.makeText(this,"Invalid video URL",Toast.LENGTH_LONG);
                    t.show();

                }*/
                Intent playVideo = new Intent(this, VideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("VIDEO_ID", "115");
                bundle.putString("ZIP_URL"," ruralict.cse.iitb.ac.in/Downloads/lokavidya/zip/llx9296.zip");
                playVideo.putExtras(bundle);
                Log.d("BrowseAndViewVideos", "Calling Video Player");
                SharedPreferences sharepref = PreferenceManager.getDefaultSharedPreferences(getApplication());
                SharedPreferences.Editor editor = sharepref.edit();
                editor.remove("playVideoName");
                editor.remove("playVideoURL");
                editor.remove("playVideoDesc");
                editor.putString("playVideoName", "waterpurifier-English" );
                editor.putString("playVideoURL", "ruralict.cse.iitb.ac.in/Downloads/lokavidya/videos/waterpurifier-Englisharajpvf.mp4");
                editor.putString("playVideoDesc", "pumping by pedalling"); //TODO add description if possible

                Log.d("BrowseAndViewVideos", "Logging URL:+" + sharepref.getString("playVideoURL", "NA"));
                Log.d("BrowseAndViewVideos", "Logging URL:+" + sharepref.getString("playVideoName", "NA"));
                Log.d("BrowseAndViewVideos", "Logging URL:+" + sharepref.getString("playVideoDesc", "NA"));
                editor.commit();

                startActivity(playVideo);

                //toast.show();
            }
        }
        finish();

    }


}
