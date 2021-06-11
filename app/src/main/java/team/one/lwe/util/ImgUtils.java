package team.one.lwe.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.nos.model.NosThumbParam;

import java.io.File;

import team.one.lwe.R;
import team.one.lwe.ui.callback.RegularCallback;

public class ImgUtils {
    public static void loadRoomCover(Context context, ImageView imageCover, String url) {
        NosThumbParam nosThumbParam = new NosThumbParam();
        nosThumbParam.height = 90;
        nosThumbParam.width = 120;
        File cover = new File(context.getExternalCacheDir() + "/cover", String.format("%s.png", url));
        NIMClient.getService(NosService.class).download(url, nosThumbParam, cover.getAbsolutePath()).setCallback(new RegularCallback<Void>(context) {
            @Override
            public void onSuccess(Void param) {
                imageCover.setBackground(Drawable.createFromPath(cover.getAbsolutePath()));
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToast(context, R.string.lwe_error_download_cover);
            }
        });
    }
}
