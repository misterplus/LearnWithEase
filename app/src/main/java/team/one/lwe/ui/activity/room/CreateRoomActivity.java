package team.one.lwe.ui.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.popupmenu.NIMPopupMenu;
import com.netease.nim.uikit.common.ui.popupmenu.PopupMenuItem;
import com.netease.nim.uikit.common.util.C;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.nos.NosService;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.EnterRoomData;
import team.one.lwe.bean.RoomBasic;
import team.one.lwe.bean.RoomInfo;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;

public class CreateRoomActivity extends LWEUI {

    private Uri uri, uriResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_room_create, true);
        setToolBar(R.id.toolbar, options);
        Spinner spinnerMaxUsers = findViewById(R.id.spinnerMaxUsers);
        Spinner spinnerTimeStudy = findViewById(R.id.spinnerTimeStudy);
        Spinner spinnerTimeRest = findViewById(R.id.spinnerTimeRest);
        Spinner spinnerContentStudy = findViewById(R.id.spinnerContentStudy);

        String[] maxUserValues = getResources().getStringArray(R.array.lwe_spinner_max_users);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, maxUserValues);
        adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerMaxUsers.setAdapter(adapter);

        String[] timeValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, timeValues);
        adapter2.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeStudy.setAdapter(adapter2);

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_rest);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, restValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(adapter3);

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, contentValues);
        adapter4.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(adapter4);

        SwitchMaterial switchFriendsOnly = findViewById(R.id.switchFriendsOnly);

        EditText editTextRoomName = findViewById(R.id.editTextRoomName);

        ImageButton buttonCover = findViewById(R.id.buttonCover);
        List<PopupMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new PopupMenuItem(0, "拍照"));
        menuItems.add(new PopupMenuItem(1, "从相册选择"));
        NIMPopupMenu menu = new NIMPopupMenu(this, menuItems, item -> {
            switch (item.getTag()) {
                case 0: {
                    File caption = new File(this.getExternalCacheDir(), "caption_cover.jpg");
                    try {
                        if (caption.exists())
                            caption.delete();
                        caption.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (Build.VERSION.SDK_INT >= 24)
                        uri = FileProvider.getUriForFile(this, "team.one.lwe.ipc.provider.file", caption);
                    else
                        uri = Uri.fromFile(caption);
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 0);
                    break;
                }
                case 1: {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });
        buttonCover.setOnClickListener(v -> menu.show(buttonCover));

        Button buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextRoomName.getText().toString();
                int maxUsers = spinnerMaxUsers.getSelectedItemPosition() + 2;
                int timeStudy = spinnerTimeStudy.getSelectedItemPosition();
                int timeRest = spinnerTimeRest.getSelectedItemPosition();
                int contentStudy = spinnerContentStudy.getSelectedItemPosition();
                boolean friendsOnly = switchFriendsOnly.isChecked();
                if (name.length() > 10 || name.length() < 1) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_room_name);
                } else if (uriResult == null) {
                    ToastHelper.showToast(getBaseContext(), R.string.lwe_error_room_cover);
                } else {
                    File result = new File(uriResult.getPath());
                    DialogMaker.showProgressDialog(CreateRoomActivity.this, getString(R.string.lwe_progress_room_create), false);
                    NIMClient.getService(NosService.class).upload(result, C.MimeType.MIME_PNG).setCallback(new RegularCallback<String>(CreateRoomActivity.this) {
                        @Override
                        public void onSuccess(String url) {
                            RoomInfo ext = new RoomInfo(maxUsers, timeStudy, timeRest, contentStudy, friendsOnly, url);
                            RoomBasic room = new RoomBasic();
                            room.setName(name);
                            room.setExt(ext);
                            new NetworkThread(editTextRoomName) {
                                @Override
                                public ASResponse doRequest() {
                                    return APIUtils.createRoom(room);
                                }

                                @Override
                                public void onSuccess(ASResponse asp) {
                                    DialogMaker.dismissProgressDialog();
                                    EnterRoomData enterRoomData = asp.getChatroom();
                                    Intent intent = new Intent();
                                    intent.putExtra("enterRoomData", new Gson().toJson(enterRoomData));
                                    setResult(1, intent);
                                    finish();
                                }
                            }.start();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0: {
                    File cropped = new File(this.getExternalCacheDir(), "cover_cropped.png");
                    try {
                        if (cropped.exists())
                            cropped.delete();
                        cropped.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uriCropped = Uri.fromFile(cropped);
                    UCrop.of(uri, uriCropped)
                            .withAspectRatio(4, 3)
                            .withMaxResultSize(1200, 900)
                            .start(this);
                    break;
                }
                case 1: {
                    uri = data.getData();
                    File cropped = new File(this.getExternalCacheDir(), "cover_cropped.png");
                    try {
                        if (cropped.exists())
                            cropped.delete();
                        cropped.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uriCropped = Uri.fromFile(cropped);
                    UCrop.of(uri, uriCropped)
                            .withAspectRatio(4, 3)
                            .withMaxResultSize(1200, 900)
                            .start(this);
                    break;
                }
                case UCrop.REQUEST_CROP: {
                    uriResult = UCrop.getOutput(data);
                    ImageView imageCover = findViewById(R.id.imageCover);
                    imageCover.setImageURI(uriResult);
                    break;
                }
                case UCrop.RESULT_ERROR: {
                    try {
                        throw UCrop.getError(data);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }
}
