package com.jbentley.spotmapper;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

public class TransistionReceivedIntentService extends IntentService{


	public TransistionReceivedIntentService(){
		super("TransistionReceivedIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Intent broadcastIntent = new Intent();

		//add category to broadcast intent
		broadcastIntent.addCategory("CATEGORY_LOCATION_SERVICES");





		if(LocationClient.hasError(intent)){

			int errorCode = LocationClient.getErrorCode(intent);

			Log.e("ReceiveTransitionsIntentService", "Location Services error: " + Integer.toString(errorCode));

			// Get the error message
			String errorMessage = "ReceiveTransitionsIntentService Location Services error: " + Integer.toString(errorCode);

			// Set the action and error message for the broadcast intent
			broadcastIntent.setAction("GEOFENCE_ERROR")
			.putExtra("EXTRA_GEOFENCE_STATUS", errorMessage);

			// Broadcast the error *locally* to other components in this app
			LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);


		} else {
			int transition = LocationClient.getGeofenceTransition(intent);

			if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
				List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);

				String[] geofenceIds = new String[triggerList.size()];
				for (int index = 0; index < triggerList.size() ; index++) {
					geofenceIds[index] = triggerList.get(index).getRequestId();
				}
//				String ids = TextUtils.join(",",geofenceIds);
//				String transitionType = getTransitionString(transition);
//
//				sendNotification(transitionType, ids);




			} else {
				Log.e("SpotMapper TRIS",
						"Invalid transition type");

			}


		}

	}

//	private void sendNotification(String transitionType, String ids) {
//		// TODO Auto-generated method stub
//		Intent notificationIntent =
//				new Intent(getApplicationContext(),MainNavActivity.class);
//
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//		stackBuilder.addParentStack(MainNavActivity.class);
//
//		stackBuilder.addNextIntent(notificationIntent);
//
//		PendingIntent notificationPendingIntent =
//				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//		// Set the notification contents
//		builder.setSmallIcon(R.drawable.ic_action_alerts_and_states_warning)
//		.setContentTitle(
//				getString(R.string.entering_a_geofence_,
//						ids))
//						.setContentText(getString(R.string.entering_a_geofence_))
//						.setContentIntent(notificationPendingIntent);
//
//		// Get an instance of the Notification manager
//		NotificationManager mNotificationManager =
//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//		// Issue the notification
//		mNotificationManager.notify(0, builder.build());
//
//	}
//
//	private String getTransitionString(int transition) {
//		// TODO Auto-generated method stub
//		 switch (transition) {
//
//         case Geofence.GEOFENCE_TRANSITION_ENTER:
//             return "Transition ENTERED";
//
//         case Geofence.GEOFENCE_TRANSITION_EXIT:
//             return "Transition EXITED";
//
//         default:
//             return "Transition UNKNOWN";
//     }
//	}



}
