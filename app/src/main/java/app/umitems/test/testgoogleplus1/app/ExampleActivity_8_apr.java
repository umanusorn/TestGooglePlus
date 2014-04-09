package app.umitems.test.testgoogleplus1.app;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;


public class ExampleActivity_8_apr extends Activity implements
                                              ConnectionCallbacks, OnConnectionFailedListener,
                                              GoogleApiClient.ConnectionCallbacks,
                                              GoogleApiClient.OnConnectionFailedListener, View
		                                              .OnClickListener{

/* Request code used to invoke sign in user interactions. */
private static final int RC_SIGN_IN = 0;

/* Client used to interact with Google APIs. */
//private GoogleApiClient mPlusClient;
private GoogleApiClient mPlusClient;
//private PlusClient mPlusClient;
/* A flag indicating that a PendingIntent is in progress and prevents
 * us from starting further intents.
 */
private boolean mIntentInProgress;

/* Track whether the sign-in button has been clicked so that we know to resolve
 * all issues preventing sign-in without waiting.
 */
private boolean mSignInClicked;

/* Store the connection result from onConnectionFailed callbacks so that we can
 * resolve them when the user clicks sign-in.
 */
private ConnectionResult mConnectionResult;
View btn;

public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_example);
	mPlusClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API,null)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();

	/*mPlusClient = new PlusClient.Builder(this,this,this)
			//.addScope(Plus.SCOPE_PLUS_LOGIN)
			.build();*/

		btn = findViewById(R.id.sign_in_button);
		btn.setOnClickListener(this);
}

protected void onStart() {
	super.onStart();
	mPlusClient.connect();
}

/* A helper method to resolve the current ConnectionResult error. */
public void resolveSignInError() {
	Log.d("","resolveSignInError()");
	if (mConnectionResult.hasResolution()) {
		Log.d("","resolveSignInError() mConnectionResult.hasResolution()");
		try {
			mIntentInProgress = true;
			/*startIntentSenderForResult(mConnectionResult.getIntentSender(),
			                           RC_SIGN_IN, null, 0, 0, 0);*/

			mConnectionResult.startResolutionForResult(this, // your activity
			                                RC_SIGN_IN);
		} catch (IntentSender.SendIntentException e) {
			// The intent was canceled before it was sent.  Return to the default
			// state and attempt to connect to get an updated ConnectionResult.
			Log.d("","resolveSignInError() mConnectionResult.hasResolution() catch");
			mIntentInProgress = false;
			mPlusClient.connect();
		}
	}
}


protected void onStop() {
	super.onStop();

	if (mPlusClient.isConnected()) {
		mPlusClient.disconnect();
	}
}

@Override public void onConnected(Bundle bundle){
	// We've resolved any connection errors.  mPlusClient can be used to
	// access Google APIs on behalf of the user.
	mSignInClicked = false;
	Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
}

@Override public void onConnectionSuspended(int i){
	mPlusClient.connect();
}

@Override public void onDisconnected(){

}

@Override
public void onConnectionFailed(ConnectionResult result) {
	if (!mIntentInProgress) {
		// Store the ConnectionResult so that we can use it later when the user clicks
		// 'sign-in'.
		mConnectionResult = result;

		if (mSignInClicked) {
			// The user has already clicked 'sign-in' so we attempt to resolve all
			// errors until the user is signed in, or they cancel.
			resolveSignInError();
		}
	}
}
/*@Override
public void onConnectionFailed(ConnectionResult result){
	if (!mIntentInProgress && result.hasResolution()) {
		try {
			mIntentInProgress = true;*//*
			startIntentSenderForResult(result.getIntentSender(),
			                           RC_SIGN_IN, null, 0, 0, 0);*//*

			result.startResolutionForResult(this, // your activity
			                                RC_SIGN_IN);
		} catch (IntentSender.SendIntentException e) {
			// The intent was canceled before it was sent.  Return to the default
			// state and attempt to connect to get an updated ConnectionResult.
			mIntentInProgress = false;
			mPlusClient.connect();
		}
	}
}*/



protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	if (requestCode == RC_SIGN_IN) {
		if (responseCode != RESULT_OK) {
			mSignInClicked = false;
		}

		mIntentInProgress = false;

		if (!mPlusClient.isConnecting()) {
			mPlusClient.connect();
		}
	}
}

@Override public void onClick(View view){

	Log.d("","onClick()");
	if (view.getId() == R.id.sign_in_button
	    && !mPlusClient.isConnecting()) {
		mSignInClicked = true;
		resolveSignInError();
	}
}
}