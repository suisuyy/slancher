package com.yourdomain.launcherapp;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.graphics.Rect;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.GridLayout;
import android.widget.GridView;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import android.util.Log;
import android.content.Intent;
import android.widget.AdapterView;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.Button;
import android.webkit.WebView;
import android.widget.EditText;
import android.view.KeyEvent;
import android.webkit.WebViewClient;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.webkit.WebChromeClient;
import android.webkit.PermissionRequest;
import android.os.Build;
import androidx.annotation.NonNull;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.webkit.ConsoleMessage;


public class LauncherActivity extends Activity {
    private PackageManager packageManager;
    private GridView appsGrid;
    private List<AppDetail> appsList;

	private WebView webview1, webview2, webview3;
	private WebView[] webViews;
	private int currentWebViewIndex = 0;
	private String[] urls = new String[3]; // Array to hold the URLs for each WebView
	private EditText urlInput;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
		}



        packageManager = getPackageManager();
        appsGrid = (GridView) findViewById(R.id.apps_grid);
		appsGrid.setOnTouchListener(new View.OnTouchListener() {
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


//		setViewSizeByPercentage(appsGrid,100,60);

		Button refreshButton = findViewById(R.id.refresh_button);
		refreshButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					refreshAppList(); // Refresh the app list when the button is clicked
				}
			});

		loadApps();
		loadWeb();
		addEventListener();
		addGridListeners();

		
    }
	private void setViewSizeByPercentage(View v, int w, int h){
		ViewGroup.LayoutParams layoutParams=v.getLayoutParams();

		layoutParams.width=w /100 * getResources().getDisplayMetrics().widthPixels;
		layoutParams.height=h/100 *getResources().getDisplayMetrics().heightPixels;
		v.setLayoutParams((layoutParams));
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Apply settings to WebView and GridView
		SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
		String defaultUrl = prefs.getString("defaultUrl", "https://voigptcn.pages.dev");
		int webViewWidth = prefs.getInt("webViewWidth", ViewGroup.LayoutParams.MATCH_PARENT);
		int webViewHeightPercentage = prefs.getInt("webViewHeightPercentage", 80);
		int gridViewColumns = prefs.getInt("gridViewColumns", 8);


		/*ViewGroup.LayoutParams webViewParams = webView.getLayoutParams();
		webViewParams.width = webViewWidth;
		webViewParams.height = webViewHeightPercentage * getResources().getDisplayMetrics().heightPixels / 100;
		webView.setLayoutParams(webViewParams);
		webView.loadUrl(defaultUrl);*/

		GridView gridView = findViewById(R.id.apps_grid);
		gridView.setNumColumns(gridViewColumns);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Permission granted, set up the WebView to record audio
				loadWeb();
				showtoast("audio granted");
			} else {
				// Permission denied, inform the user that audio recording won't work
				showtoast("audio not granted");
				
				}
		}
	}
	
	
	private  void  loadWeb(){
		webview1 = findViewById(R.id.webview1);
		webview2 = findViewById(R.id.webview2);
		webview3 = findViewById(R.id.webview3);
		webViews = new WebView[]{webview1, webview2, webview3};
		urlInput=findViewById((R.id.url_input));

		// Load saved URLs or default ones
		SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
		urls[0] = prefs.getString("url1", "https://google.com");
		urls[1] = prefs.getString("url2", "https://bing.com");
		urls[2] = prefs.getString("url3", "https://chat.openai.com");

		// Load the initial URL into the first WebView
		webview1.loadUrl(urls[0]);
		urlInput.setText(urls[0]);
		webview1.setVisibility(View.VISIBLE);

		for (WebView webView:
			 webViews) {
			setWebView(webView);


			int webViewWidth = prefs.getInt("webViewWidth", ViewGroup.LayoutParams.MATCH_PARENT);
			int webViewHeightPercentage = 50;


		ViewGroup.LayoutParams webViewParams = webView.getLayoutParams();
		webViewParams.width = webViewWidth;
		webViewParams.height = webViewHeightPercentage * getResources().getDisplayMetrics().heightPixels / 100;
		webView.setLayoutParams((webViewParams));

		}
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView(WebView webView){
		// Enable JavaScript if required by the web content
        webView.getSettings().setJavaScriptEnabled(true);

		webView.getSettings().setDomStorageEnabled(true);
		//webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
		webView.getSettings().setDatabasePath(appCachePath);
		webView.getSettings().setMediaPlaybackRequiresUserGesture(false); // May be necessary to play audio without user interaction

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
						  showtoast(consoleMessage.message());
					return super.onConsoleMessage(consoleMessage);
				}
				
			});

		webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true; // Indicates WebView to handle the URL
				}
				
				@Override
				public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
					super.onReceivedError(view, request, error);
					// Handle error
					showtoast(error.toString());
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
								break;
						}
					}
					return false;
				}
			});

        // Set up the button click listener to load the URL
        final EditText urlInput = findViewById(R.id.url_input);
		
        Button openUrlButton = findViewById(R.id.open_url_button);

        openUrlButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WebView webView=webViews[currentWebViewIndex];
					webView.setVisibility(View.VISIBLE);
					String url = urlInput.getText().toString().trim();
					if (!url.isEmpty()) {
						if (!url.startsWith("http://") && !url.startsWith("https://")) {
							url = "http://" + url; // Add scheme if missing
						}
						webView.clearCache(true);
						webView.loadUrl(url); // Load the URL in the WebView
					}
				}
			});
			
		
			
			
		
	}

	private void addEventListener(){
		Button settingsButton = findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LauncherActivity.this, SettingsActivity.class);
				StartFreeFormIntent.start(LauncherActivity.this,(intent));
			}
		});

		Button increaseHeightButton = findViewById(R.id.increase_height_button);
		increaseHeightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebView webView=webViews[currentWebViewIndex];

				ViewGroup.LayoutParams params = webView.getLayoutParams();
				params.height += dpToPx(50); // Increase height by 10dp
				webView.setLayoutParams(params);
			}
		});

		Button decreaseHeightButton = findViewById(R.id.decrease_height_button);
		decreaseHeightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebView webView=webViews[currentWebViewIndex];
				ViewGroup.LayoutParams params = webView.getLayoutParams();
				params.height -= dpToPx(50); // Decrease height by 10dp
				webViews[currentWebViewIndex].setLayoutParams(params);
			}
		});


		Button changeWebViewButton = findViewById(R.id.change_webview_button);
		changeWebViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save the current URL before switching
				urls[currentWebViewIndex] = urlInput.getText().toString();
				saveUrls();

				// Hide all WebViews
				for (WebView wv : webViews) {
					wv.setVisibility(View.GONE);
				}

				// Increment index to switch to the next WebView
				currentWebViewIndex = (currentWebViewIndex + 1) % webViews.length;
				changeWebViewButton.setText("switch"+currentWebViewIndex);

				// Show the next WebView and update the URL input field
				webViews[currentWebViewIndex].setVisibility(View.VISIBLE);
				urlInput.setText(urls[currentWebViewIndex]);
//				webViews[currentWebViewIndex].loadUrl(urls[currentWebViewIndex]);
			}
		});

		// Add listeners for other buttons and functionality



}

	// Helper method to convert dp to pixels
	private int dpToPx(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}

	private void saveUrls() {
		SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("url1", urls[0]);
		editor.putString("url2", urls[1]);
		editor.putString("url3", urls[2]);
		editor.apply();
	}

	private  void freeLaunch(Intent intent){

			// Define the bounds in which the Activity will be launched into.
			Rect bounds = new Rect(100, 100, 300, 300);

			// Set the bounds as an activity option.
			ActivityOptions options = ActivityOptions.makeBasic();
			options.setLaunchBounds(bounds);

			// Start the LaunchBoundsActivity with the specified options
			startActivity(intent, options.toBundle());
			return;


/*// Check if the device is running on Android Nougat (7.0, API level 24) or higher
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// Set flag to indicate the activity should be launched in freeform mode


			Rect rect = new Rect(0, 0, 500, 500); // Example bounds
			ActivityOptions options = ActivityOptions.makeBasic();
			options.setLaunchBounds(rect);

			try {
				Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
				method.invoke(options, 5);
			}catch (Exception e){
				Log.i("FreeForm", "getActivityOptions error = " + e);
			}
			// Start the activity with the specified options
			startActivity(intent, options.toBundle());
		} else {
			// If the device is not running on Android Nougat or higher,
			// or does not support freeform mode, launch the activity normally
			startActivity(intent);
		}*/
	}
    private void loadApps() {
		appsList=new ArrayList<>();
		
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(packageManager);
            app.packageName = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(packageManager);
            app.isPinned = loadPinnedStatus(app.packageName.toString());
			
			
			appsList.add(app);
        }

        Collections.sort(appsList);
		for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(packageManager);
            app.packageName = ri.activityInfo.packageName;
            app.icon = ri.activityInfo.loadIcon(packageManager);
            app.isPinned = loadPinnedStatus(app.packageName.toString());

			if(app.isPinned){
				appsList.add(0,app);
				
				appsList.add(app);
				
			}

        }

		appsGrid.setAdapter(new AppsAdapter(this, appsList));
		
    }


    private void addGridListeners() {
        appsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					AppDetail app = appsList.get(position);

					// Log the package name to verify it's correct
					Log.d("LauncherActivity", "Package name: " + app.packageName);

					try {
						Intent intent = packageManager.getLaunchIntentForPackage(app.packageName.toString());
						if (intent != null) {
							// Add flags if necessary (e.g., FLAG_ACTIVITY_NEW_TASK)
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							// Start the activity
							StartFreeFormIntent.start(LauncherActivity.this,intent);
						} else {
							// Log an error or show a message if the intent is null
							Log.e("LauncherActivity", "Intent is null for package: " + app.packageName);
							Toast.makeText(LauncherActivity.this, "Cannot launch the app.", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// Catch any exceptions and log them
						Log.e("LauncherActivity", "Error launching app", e);
						Toast.makeText(LauncherActivity.this, "Error launching app.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
		appsGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					AppDetail app = (AppDetail) parent.getItemAtPosition(position);
					
					toggleAppPinned(position);
					((BaseAdapter) appsGrid.getAdapter()).notifyDataSetChanged(); // Refresh the grid view
					return true;
				}
			});
    }
	
	private void toggleAppPinned(int position) {
		AppDetail app = appsList.get(position);
		app.isPinned = !app.isPinned; // Toggle the pinned status

		// Persist the pinned status, e.g., in SharedPreferences
		savePinnedStatus(app.packageName.toString(), app.isPinned);
   refreshAppList();
		Toast toast = Toast.makeText(getApplicationContext(), " " + app.isPinned, Toast.LENGTH_SHORT);
		toast.show();
   
		
	}

	private void savePinnedStatus(String packageName, boolean isPinned) {
		SharedPreferences prefs = getSharedPreferences("PinnedApps", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(packageName, isPinned);
		editor.apply();
	}

	private boolean loadPinnedStatus(String packageName) {
		SharedPreferences prefs = getSharedPreferences("PinnedApps", MODE_PRIVATE);
		return prefs.getBoolean(packageName, false);
	}
	private void refreshAppList() {
		loadApps(); // Load installed apps again
/*
		appsGrid.setAdapter(new AppsAdapter(this, appsList));
*/
		//((BaseAdapter) appsGrid.getAdapter()).notifyDataSetChanged(); // Notify the adapter to refresh the grid
	}
	
	public void showtoast(String msg){
		Toast toast = Toast.makeText(getApplicationContext(), " " + msg, Toast.LENGTH_LONG);
		toast.show();
		
		
	}
	
	
}
