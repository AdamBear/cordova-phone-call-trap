package io.gvox.phonecalltrap;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
    private iscall = false;
    MediaRecorder mediaRecorder = new MediaRecorder();

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
                if(iscall){
                    mediaRecorder.stop();
                    iscall=false;
                }
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                msg = "OFFHOOK";
                iscall = true;
                try {
                    recordCallComment();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    mediaRecorder.stop();
                }
                break;

            case TelephonyManager.CALL_STATE_RINGING:
                msg = "RINGING";
                break;
        }

        JSONObject r = new JSONObject();
        try {
            r.put("state", msg);
            r.put("incomingNumber", incomingNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(PluginResult.Status.OK, r);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }

    public void recordCallComment() throws IOException{
        //这里AudioSource.MIC可以改为AudioSource.VOICE_CALL, 把音源变
        //电话通话内容, 但似乎很多机都不支持通话录音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder
                .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder
                .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        audioFile = File.createTempFile("record_", ".amr");
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.start();
    }
}
