package com.yourdomain.launcherapp;


import static android.content.Context.DOWNLOAD_SERVICE;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AllFunctionWebviewSetter extends WebView {
    private static final int REQUEST_SELECT_FILE = 100;
    public static int FILECHOOSER_RESULTCODE=101;
    public static ValueCallback uploadMessage;


    public AllFunctionWebviewSetter(Context context){
        super(context);
    }



    //put this to your Activity to support file upload, or just call it from onActivityResult in your activity
    public static void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        //for webview file pick
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == uploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != Activity.RESULT_OK ?
                    null : intent.getData();
            uploadMessage.onReceiveValue(result);
            uploadMessage = null;
        }


    }

    //call this in oncreate to request permissions
    public  static void requestPermissions(Activity activity) {
        List<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ContextCompat.checkSelfPermission(activity.getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.RECORD_AUDIO);
            }

            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.CAMERA);
            }

            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (!Settings.System.canWrite(activity.getApplicationContext())) {
                // If the WRITE_SETTINGS permission is not granted, open the system settings to request permission
                permissionsToRequest.add(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            }

            if (!permissionsToRequest.isEmpty()) {
                String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
                activity.requestPermissions(permissionsArray, 1);
            }
        }
    }


    //use the method to setup your webview easily
    public static   void  setWebView(WebView webView, Activity activity,Context context,EditText urlInput){
        // Enable JavaScript if required by the web content
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDatabasePath(context.getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setMediaPlaybackRequiresUserGesture(false); // May be necessary to play audio without user interaction
        webSettings.setGeolocationEnabled(true);
        webView.setInitialScale(100);

       // String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
       // webSettings.setUserAgentString(desktopUserAgent);
       // webSettings.setBuiltInZoomControls(true);
        //webView.setInitialScale(50);



        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // The onTouchEvent will be triggered before the ScrollView touches, so we can disable the scroll here
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Disables the ScrollView to intercept touch events
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allows ScrollView to intercept touch events
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle WebView touch events
                v.onTouchEvent(event);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Always grant permission since this is controlled by a permission prompt to the user
                callback.invoke(origin, true, false);
            }
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                // showtoast(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                uploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try
                {
                    activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                uploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                uploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }




        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // Update EditText when a new page finishes loading
                //   urlInput.setText(url);

            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Update EditText when a new page finishes loading
                urlInput.setText(url);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true; // Indicates WebView to handle the URL
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Handle error
                showtoast(error.toString(),activity);
            }


        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {


                try {

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // Get download service and enqueue file
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                    }

                    // To save downloaded file to device's Download folder
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                    // Enqueue the download. The download will start automatically once the download manager is ready
                    long downloadID = downloadManager.enqueue(request);

                    // Optionally, you can track the download progress here using the download ID
                }
                catch (Exception e){
                    //showtoast(e.toString());
                }
            }
        });



        // Handle back navigation in WebView
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }

                        case  KeyEvent.KEYCODE_HOME:
                            //not work to home key

                            break;
                    }
                }
                return false;
            }
        });






    }

    public static void showtoast(String msg, Activity activity){
        Toast toast = Toast.makeText(activity.getApplicationContext(), " " + msg, Toast.LENGTH_SHORT);
        toast.show();


    }


}
