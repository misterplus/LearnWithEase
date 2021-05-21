package team.one.lwe.ui.callback;

import android.content.Context;


public class VoidSuccessCallback extends RegularCallback<Void> {

    public VoidSuccessCallback(Context context) {
        super(context);
    }

    @Override
    public void onSuccess(Void param) {
        //callback that doesn't have a success response
    }
}
