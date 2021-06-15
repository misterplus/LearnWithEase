package team.one.lwe.ui.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

import java.util.HashMap;
import java.util.Map;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.callback.RegularCallback;
import team.one.lwe.ui.callback.UpdateCallback;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;
import team.one.lwe.util.RoomUtils;

public class RoomSettingActivity extends LWEUI {

    private Map<String, Object> ext = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_setting);
        Intent intent = getIntent();
        String roomId = intent.getStringExtra("roomId");
        LWEToolBarOptions options = new LWEToolBarOptions(R.string.lwe_title_room_setting, true);
        setToolBar(R.id.toolbar, options);

        Spinner spinnerTimeStudy = findViewById(R.id.spinnerTimeStudy);
        Spinner spinnerTimeRest = findViewById(R.id.spinnerTimeRest);
        Spinner spinnerContentStudy = findViewById(R.id.spinnerContentStudy);
        String[] timeValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, timeValues);
        adapter.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeStudy.setAdapter(adapter);
        spinnerTimeStudy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RoomUtils.updateTimeStudy(ext, roomId, i).setCallback(new UpdateCallback(getBaseContext(), "timeStudy"));
                new NetworkThread(spinnerContentStudy) {
                    @Override
                    public ASResponse doRequest() {
                        return APIUtils.updateTimeStudy(roomId, i);
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {

                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_rest);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, restValues);
        adapter2.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(adapter2);
        spinnerTimeRest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RoomUtils.updateTimeRest(ext, roomId, i).setCallback(new UpdateCallback(getBaseContext(), "timeRest"));
                new NetworkThread(spinnerContentStudy) {
                    @Override
                    public ASResponse doRequest() {
                        return APIUtils.updateTimeRest(roomId, i);
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {

                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, contentValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(adapter3);
        spinnerContentStudy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RoomUtils.updateContentStudy(ext, roomId, i).setCallback(new UpdateCallback(getBaseContext(), "contentStudy"));
                new NetworkThread(spinnerContentStudy) {
                    @Override
                    public ASResponse doRequest() {
                        return APIUtils.updateContentStudy(roomId, i);
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {

                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SwitchMaterial switchFriendsOnly = findViewById(R.id.switchFriendsOnly);
        switchFriendsOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            RoomUtils.updateFriendsOnly(ext, roomId, b).setCallback(new UpdateCallback(getBaseContext(), "friendsOnly"));
        });

        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RegularCallback<ChatRoomInfo>(this) {
            @Override
            public void onSuccess(ChatRoomInfo info) {
                ext = info.getExtension();
                spinnerTimeStudy.setSelection((Integer) ext.get("timeStudy"));
                spinnerTimeRest.setSelection((Integer) ext.get("timeRest"));
                spinnerContentStudy.setSelection((Integer) ext.get("contentStudy"));
                switchFriendsOnly.setChecked((Boolean) ext.get("friendsOnly"));
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.showToast(getBaseContext(), R.string.lwe_error_room_setting);
            }
        });
    }
}
