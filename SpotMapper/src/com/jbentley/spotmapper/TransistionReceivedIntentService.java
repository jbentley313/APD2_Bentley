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
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

//public class TransistionReceivedIntentService extends IntentService{
//
//	
//	public TransistionReceivedIntentService(){
//		super("TransistionReceivedIntentService");
//	}
//
//	@Override
//	protected void onHandleIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		if(LocationClient.hasError(intent)){
//			
//			int errorCode = LocationClient.getErrorCode(intent);
//			
//			Log.e("ReceiveTransitionsIntentService", "Location Services error: " + Integer.toString(errorCode));
//			
//			
//		} else {
//			int transition = LocationClient.getGeofenceTransition(intent);
//			
//			if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
//				List<Geofence> triggerList = LocationClient.getTriggeringGeofences(intent);
//				
//				   String[] geofenceIds = new String[triggerList.size()];
//	                for (int index = 0; index < triggerList.size() ; index++) {
//	                    geofenceIds[index] = triggerList.get(index).getRequestId();
//	                }
//	                String ids = TextUtils.join(",",geofenceIds);
//	                String transitionType = getTransitionString(transition);
//
//	                sendNotification(ids);
//
//	                
//
//	            
//	            } else {
//	                
//	            	
//	            }
//			
//			
//	        }
//			
//		}
//
//	private void sendNotification( String ids) {
//		// TODO Auto-generated method stub
//		Intent notificationIntent =
//                new Intent(getApplicationContext(),MainNavActivity.class);
//		
//		 TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//		 
//		 stackBuilder.addParentStack(MainNavActivity.class);
//		 
//		 
//		 
//		 PendingIntent notificationPendingIntent =
//	                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//		 
//		 NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//		 
//		// Set the notification contents
//	        builder.setSmallIcon(R.drawable.ic_action_alerts_and_states_warning)
//	               .setContentTitle(
//	                       getString(R.string.entering_a_geofence_,
//	                                ids))
//	               .setContentText(getString(R.string.entering_a_geofence_))
//	               .setContentIntent(notificationPendingIntent);
//
//	        // Get an instance of the Notification manager
//	        NotificationManager mNotificationManager =
//	            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//	        // Issue the notification
//	        mNotificationManager.notify(0, builder.build());
//		
//	}
//
//	private String getTransitionString(int transition) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
	
//}
