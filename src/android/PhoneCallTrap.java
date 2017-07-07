package io.gvox.phonecalltrap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;


public class PhoneCallTrap extends CordovaPlugin {

    CallStateListener listener;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        prepareListener();

        listener.setCallbackContext(callbackContext);

        return true;
    }

    private void prepareListener() {
        if (listener == null) {
            listener = new CallStateListener();
            TelephonyManager TelephonyMgr = (TelephonyManager) cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}

class CallStateListener extends PhoneStateListener {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        if (callbackContext == null) return;

        String msg = "";

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
            msg = "IDLE";
            break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
            msg = "OFFHOOK";
            break;

            case TelephonyManager.CALL_STATE_RINGING:
            msg = "RINGING";
            break;
        }

        JSONObject r = new JSONObject();
        r.put("state", msg);
        r.put("incomingNumber", incomingNumber);

        PluginResult result = new PluginResult(PluginResult.Status.OK, r);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }

 class OutgoingReceiver extends BroadcastReceiver {
 	public OutgoingReceiver() {}

 	@Override
 	public void onReceive(Context context, Intent intent) {
 		String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

 		Toast.makeText(ctx, "Outgoing: "+number, Toast.LENGTH_LONG).show();
 	}
 }


 class CallDetectService extends Service {
 	private CallHelper callHelper;

 	public CallDetectService() {}

 	@Override
 	public int onStartCommand(Intent intent, int flags, int startId) {
 		callHelper = new CallHelper(this);
 		int res = super.onStartCommand(intent, flags, startId);

 		callHelper.start();

 		return res;
 	}

 	@Override
 	public void onDestroy() {
 		super.onDestroy();
 		callHelper.stop();
 	}

 	@Override
 	public IBinder onBind(Intent intent) {
 		// not supporting binding
 		return null;
 	}
 }
}
