package com.yourdomain.launcherapp;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.google.android.material.internal.ViewUtils.dpToPx;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
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
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
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

import com.yourdomain.launcherapp.libs.LauncherModel;
import com.yourdomain.launcherapp.libs.Utils;


public class LauncherActivity extends Activity {
	private PackageManager packageManager;
    private GridView appsGrid;
    private List<AppDetail> appsList;

	private ScrollView scrollView;
	private WebView webview1, webview2, webview3, webview4, webview5;
	private WebView[] webViews;
	private int currentWebViewIndex = 0;
	private EditText urlInput;

	public LauncherModel launcherModel;


	//webview
	private ValueCallback<Uri> mUploadMessage;
	public ValueCallback<Uri[]> uploadMessage;
	public static final int REQUEST_SELECT_FILE = 100;
	private final static int FILECHOOSER_RESULTCODE = 101;



	@Override
    protected void onCreate(Bundle savedInstanceState) {
		showtoast("oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);


		scrollView=findViewById(R.id.rootScroolView);

        packageManager = getPackageManager();

		launcherModel=new LauncherModel(this);

		setupAppgridView();
		loadWeb();
		addEventListener();

		AllFunctionWebviewSetter.requestPermissions(this);


		
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Handle the Home button press
			// Perform your custom logic here
			Utils.setViewSizeByPercentageOfScreen(LauncherActivity.this,webViews[currentWebViewIndex],launcherModel.webViewWidth,
					launcherModel.webViewHeight);

			return false; // Consume the event
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Utils.setViewSizeByPercentageOfScreen(LauncherActivity.this,webViews[currentWebViewIndex],launcherModel.webViewWidth,
				launcherModel.webViewHeight);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		// The Home button was pressed or the user switched to another app
		// Handle your logic here
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{

		AllFunctionWebviewSetter.onActivityResult(requestCode,resultCode,intent);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				showtoast(permissions+ " granted");
			} else {
				showtoast(permissions+ " not granted");
		    }
	}


	//

	private void  setupAppgridView(){
		appsGrid=findViewById(R.id.apps_grid);
		appsGrid.setNumColumns(launcherModel.appgridViewColums);

		Utils.setViewSizeByPercentageOfScreen(this,appsGrid,launcherModel.appgridViewWidth,
				launcherModel.appgridViewHeight);
		addGridListeners();
		loadApps();


	}
	private  void  loadWeb(){
		webview1 = findViewById(R.id.webview1);
		webview2 = findViewById(R.id.webview2);
		webview3 = findViewById(R.id.webview3);
		webview4 = findViewById(R.id.webview4);
		webview5 = findViewById(R.id.webview5);

		webViews = new WebView[]{webview1, webview2, webview3, webview4,webview5 };
		urlInput=findViewById((R.id.url_input));

		// Load saved URLs or default ones
		SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);


		// Load the initial URL into the first WebView
		urlInput.setText(launcherModel.urls[0]);
		webview1.setVisibility(View.VISIBLE);

		for (WebView webView:
			 webViews) {
			//setWebView(webView);
			AllFunctionWebviewSetter.setWebView(webView,LauncherActivity.this,LauncherActivity.this.getApplicationContext(),urlInput);
			Utils.setViewSizeByPercentageOfScreen(this,webView,launcherModel.webViewWidth,
					launcherModel.webViewHeight);

		}
		webview1.loadUrl(launcherModel.urls[0]);
		
	}
	


	private void addEventListener(){
		Button settingsButton = findViewById(R.id.settings_button);
		Button fullscreenButton=findViewById(R.id.fullscreenButton);
		Button refreshButton = findViewById(R.id.refresh_button);
		Button increaseHeightButton = findViewById(R.id.increase_height_button);

		SearchView searchView = findViewById(R.id.search_view);

		// Set up the OnQueryTextListener to listen for query text changes
		searchView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				searchView.requestFocus();
			}
		});

		final EditText urlInput = findViewById(R.id.url_input);

		Button openUrlButton = findViewById(R.id.open_url_button);
		openUrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebView webView=webViews[currentWebViewIndex];
				webView.setVisibility(View.VISIBLE);
				String mobileUserAgent = "Mozilla/5.0 (Linux; Android 10; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Mobile Safari/537.36";
				webView.getSettings().setUserAgentString(mobileUserAgent);

				String url = urlInput.getText().toString().trim();
				if (!url.isEmpty()) {

					if (!url.startsWith("http://") && !url.startsWith("https://")) {
						url = launcherModel.searchEnginUrl + url; // Add scheme if missing
					}
					webView.clearCache(true);
					webView.loadUrl(url); // Load the URL in the WebView
					launcherModel.urls[currentWebViewIndex]=url;
					launcherModel.saveSettings();
				}
				showtoast("long click to change to desktop mode ");
			}
		});

		openUrlButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				WebView webView=webViews[LauncherActivity.this.currentWebViewIndex];
				 String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
				 webView.getSettings().setUserAgentString(desktopUserAgent);
				 webView.loadUrl(urlInput.getText().toString());
				return true;
			}
		});
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// Handle the query text submission (e.g., perform a search)
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// Handle the query text change (e.g., filter data based on the new text)
				// newText contains the updated query text
				// You can perform your desired actions here
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				searchView.requestFocus();

				if(newText.length()<=0){
					loadApps();
					return true;
				}

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
					if(app.label.toString().toLowerCase().contains(newText.toLowerCase())){
						appsList.add(app);

					}
				}

				Collections.sort(appsList);


				appsGrid.setAdapter(new AppsAdapter(LauncherActivity.this, appsList,launcherModel.appLabelSize));

				return true;
			}
		});


		refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showtoast("refresh applist, long press to restart app");
				refreshAppList(); // Refresh the app list when the button is clicked
			}
		});
		refreshButton.setOnLongClickListener((View.OnLongClickListener) v -> {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
			return true;
		});



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

		fullscreenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Toggle fullscreen mode

					if ((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0) {
						// Currently in fullscreen mode, revert back to normal mode
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

					} else {
						// Currently in normal mode, switch to fullscreen mode
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
						setViewSizeToFullScreen(webViews[currentWebViewIndex]);

					}

				int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
				int newUiOptions = uiOptions;



					newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


				getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

			}
		});
		settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LauncherActivity.this, SettingsActivity.class);

//				StartFreeFormIntent.start(LauncherActivity.this,(intent));
				FreeFormUtils.start(LauncherActivity.this,(intent));
			}
		});

		increaseHeightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launcherModel.webViewHeight+=10;
				launcherModel.saveSettings();
				for (WebView webview :
						webViews) {
					Utils.setViewSizeByPercentageOfScreen(LauncherActivity.this,webview,
							launcherModel.webViewWidth,
							launcherModel.webViewHeight
					);
				}
			}
		});
		increaseHeightButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				launcherModel.currentScale+=20;
				launcherModel.saveSettings();
				WebView webView=webViews[currentWebViewIndex];
				webView.setInitialScale(launcherModel.currentScale);
				showtoast(String.valueOf(launcherModel.currentScale));
				return true;
			}
		});

		Button decreaseHeightButton = findViewById(R.id.decrease_height_button);
		decreaseHeightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launcherModel.webViewHeight-=10;
				launcherModel.saveSettings();
				for (WebView webview :
						webViews) {
					Utils.setViewSizeByPercentageOfScreen(LauncherActivity.this,webview,
							launcherModel.webViewWidth,
							launcherModel.webViewHeight
					);
				}
			}
		});
		decreaseHeightButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				launcherModel.currentScale-=20;
				launcherModel.saveSettings();
				WebView webView=webViews[currentWebViewIndex];
				webView.setInitialScale(launcherModel.currentScale);
				showtoast(String.valueOf(launcherModel.currentScale));
				return true;
			}
		});



		Button changeWebViewButton = findViewById(R.id.change_webview_button);
		changeWebViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save the current URL before switching
				launcherModel.urls[currentWebViewIndex] = urlInput.getText().toString();
				launcherModel.saveSettings();

				// Increment index to switch to the next WebView
				currentWebViewIndex = (currentWebViewIndex + 1) % webViews.length;
				changeWebViewButton.setText("▷"+currentWebViewIndex);

				// Show the next WebView and update the URL input field
				urlInput.setText(launcherModel.urls[currentWebViewIndex]);

				// Hide all WebViews
				for (WebView wv : webViews) {
					wv.setVisibility(View.GONE);
				}
				webViews[currentWebViewIndex].setVisibility(View.VISIBLE);
			}


		});
		changeWebViewButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				launcherModel.urls[currentWebViewIndex] = urlInput.getText().toString();
				launcherModel.saveSettings();



				// Increment index to switch to the next WebView
				currentWebViewIndex = 0;
				changeWebViewButton.setText("▷"+currentWebViewIndex);

				// Show the next WebView and update the URL input field
				urlInput.setText(launcherModel.urls[currentWebViewIndex]);
				// Hide all WebViews
				for (WebView wv : webViews) {
					wv.setVisibility(View.GONE);
				}
				webViews[currentWebViewIndex].setVisibility(View.VISIBLE);

				return true;
			}
		});

		// Add listeners for other buttons and functionality
		 SeekBar soundSeekBar;
		 AudioManager audioManager;

		soundSeekBar = findViewById(R.id.soundSeekBar);

		// Get AudioManager system service
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		// Set the maximum value of the SeekBar to the maximum volume
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		soundSeekBar.setMax(maxVolume);

		// Set the current volume to the SeekBar
		int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundSeekBar.setProgress(currentVolume);

		// Set a listener to handle changes in the SeekBar
		soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Update the volume when the SeekBar is changed
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Not needed for this example
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Not needed for this example
			}
		});


		SeekBar brightnessSeekBar;
		brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
		// Set the maximum value of the SeekBar to the maximum brightness value
		int maxBrightness = 255;
		brightnessSeekBar.setMax(maxBrightness);

		// Get the current screen brightness
		int currentBrightness = Settings.System.getInt(
				getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS,
				maxBrightness);



		// Set the current brightness to the SeekBar
		brightnessSeekBar.setProgress(currentBrightness);

		// Set a listener to handle changes in the SeekBar
		brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// Update the screen brightness when the SeekBar is changed

				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);

				// Apply the new brightness setting
				WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
				layoutParams.screenBrightness = progress / (float) brightnessSeekBar.getMax();
				getWindow().setAttributes(layoutParams);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// Not needed for this example
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// Not needed for this example
			}
		});

		 Button hideUrlButton=findViewById(R.id.hideUrlButton);
		hideUrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (urlInput.getVisibility() == View.VISIBLE) {
					// If the view is currently visible, hide it
					urlInput.setVisibility(View.GONE);
				} else {
					// If the view is currently hidden, show it
					urlInput.setVisibility(View.VISIBLE);
				}
			}
		});

		SeekBar seekBar = findViewById(R.id.font_size_slider);
		TextView fontSizeValue = findViewById(R.id.font_size_value);
		seekBar.setProgress(100);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					WebView webView = webViews[currentWebViewIndex]; // Replace this with your WebView ID
					webView.getSettings().setTextZoom(progress);
					fontSizeValue.setText(String.format("%d", progress));
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

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

		appsGrid.setAdapter(new AppsAdapter(this, appsList,launcherModel.appLabelSize));
		
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
							//StartFreeFormIntent.start(LauncherActivity.this,(intent));

							FreeFormUtils.start(LauncherActivity.this,intent);
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
		Toast toast = Toast.makeText(getApplicationContext(), " " + msg, Toast.LENGTH_SHORT);
		toast.show();
		
		
	}

	private void setViewSizeToFullScreen(View view) {
		// Get screen height and width
		int screenHeight = getResources().getDisplayMetrics().heightPixels;
		int screenWidth = getResources().getDisplayMetrics().widthPixels;

		// Set the height and width of the view to 100% of the screen
		view.getLayoutParams().height = screenHeight;
		view.getLayoutParams().width = screenWidth;
	}

	private  void addHomeFunction(){
		if (getIntent().getAction() != null &&
				getIntent().getAction().equals("com.android.launcher.action.INSTALL_SHORTCUT")) {
			handleShortcutIntent(getIntent());
		} else {
			// Handle other cases or continue with normal launcher behavior
		}
	}

	private void handleShortcutIntent(Intent intent) {
		// Extract information from the intent
		String shortcutLabel = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
		Parcelable iconResourceParcelable = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);

		showtoast("test"+shortcutLabel);
		urlInput.setText("added");
		// Do something with the shortcut information, e.g., add it to your launcher
		// You can use shortcutLabel for the label and iconResourceParcelable for the icon

		// Optionally, you can also retrieve the Intent associated with the shortcut
		Intent shortcutIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

		// Now, you can use shortcutIntent to launch the app or perform any desired action
	}




}


