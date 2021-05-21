package team.one.lwe.ui.callback;

import android.content.Context;
import android.util.Log;


public class UpdateCallback extends VoidSuccessCallback {

    private final String type;

    public UpdateCallback(Context context, String type) {
        super(context);
        this.type = type;
    }

    @Override
    public void onSuccess(Void param) {
        Log.i(context.getPackageName(), String.format("update %s success", type));
    }
}
