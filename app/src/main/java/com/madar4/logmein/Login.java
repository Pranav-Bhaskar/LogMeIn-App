package com.madar4.logmein;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class Login extends AsyncTask<Void, String, Boolean> {
	
	private Context context;
	private String username;
	private String password;
	private ProgressDialog progressDialog;
	private WebView webView;
	private Thread timeout;
	private final static String TAG = " :: Login :: ";
	private int state;
	private static final int STATE_LOADING = 0;
	private static final int STATE_LOADED_AIT = 1;
	private static final int STATE_LOADED_LOGIN = 2;
	private static final int STATE_ERROR = 3;
	
	
	public Login(Context context, String username, String password) {
		this.context = context;
		this.username = username;
		this.password = password;
		this.state = STATE_LOADING;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Signing In");
		progressDialog.setMessage("Signing In");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
		this.webView = new WebView(context);
		this.webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.e(TAG, url);
				if(url.equals("https://www.aitpune.com/"))
					state = STATE_LOADED_AIT;
				else
					state = STATE_LOADED_LOGIN;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				timeout = new Thread(new Runnable() {
					@Override
					public void run() {
						try{
							Thread.sleep(10000);
						}catch (Exception e){
							Log.e(TAG, "timeout Exception", e);
						}
						Log.e(TAG, "Changing time : " + state);
						if(Thread.interrupted())
							return;
						if(state == STATE_LOADING)
							state = STATE_ERROR;
					}
				});
				timeout.start();
			}
			
			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				state = STATE_ERROR;
			}
		});
		this.webView.loadUrl("http://www.aitpune.com");
		Log.e(TAG, "Moving forward : " + state);
	}
	
	@Override
	protected Boolean doInBackground(Void... voids) {
		while(state == STATE_LOADING){
			try {
				Log.e(TAG, "Moving sleepy : " + state);
				Thread.sleep(1000);
			}catch (Exception e){
			}
		}
		timeout.interrupt();
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		/*
		webView.evaluateJavascript(
			"(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
			new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String html) {
					Log.d(TAG, html);
				}
			});
		*/
		if(state == STATE_LOADED_LOGIN){
			webView.loadUrl("javascript:var name=document.getElementById('ft_un').value='"+ username +"'");
			timeout.interrupt();
			webView.loadUrl("javascript:var pass=document.getElementById('ft_pd').value='"+ password +"'");
			timeout.interrupt();
			state = STATE_LOADING;
			webView.loadUrl("javascript:document.getElementsByTagName('form')[0].submit()");
			Toast.makeText(context, "Sent Request", Toast.LENGTH_LONG).show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(state == STATE_LOADING){
						try {
							Log.e(TAG, "Moving sleepy : " + state);
							Thread.sleep(1000);
						}catch (Exception e){
						}
					}
					timeout.interrupt();
					if(state == STATE_LOADED_AIT)
						Toast.makeText(context, "LoggedIn", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(context, "Failed to LOGIN", Toast.LENGTH_LONG).show();
				}
			});
		}
		else{
			if(state == STATE_ERROR)
				Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show();
			if(state == STATE_LOADED_AIT)
				Toast.makeText(context, "Already LoggedIn", Toast.LENGTH_LONG).show();
			progressDialog.dismiss();
			progressDialog.cancel();
			
			webView.destroy();
		}
	}
}