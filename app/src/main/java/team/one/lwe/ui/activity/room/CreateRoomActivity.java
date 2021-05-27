package team.one.lwe.ui.activity.room;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;

import team.one.lwe.R;
import team.one.lwe.bean.ASResponse;
import team.one.lwe.bean.RoomInfo;
import team.one.lwe.network.NetworkThread;
import team.one.lwe.ui.activity.LWEUI;
import team.one.lwe.ui.wedget.LWEToolBarOptions;
import team.one.lwe.util.APIUtils;

public class CreateRoomActivity extends LWEUI {
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

        String[] restValues = getResources().getStringArray(R.array.lwe_spinner_time_study);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, restValues);
        adapter3.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerTimeRest.setAdapter(adapter3);

        String[] contentValues = getResources().getStringArray(R.array.lwe_spinner_content_study);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, R.layout.lwe_spinner_item, contentValues);
        adapter4.setDropDownViewResource(R.layout.lwe_spinner_item);
        spinnerContentStudy.setAdapter(adapter4);

        SwitchMaterial switchFriendsOnly = findViewById(R.id.switchFriendsOnly);

        EditText editTextRoomName = findViewById(R.id.editTextRoomName);
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
                RoomInfo ext = new RoomInfo(maxUsers, timeStudy, timeRest, contentStudy, friendsOnly);
                //TODO: send request
                new NetworkThread(editTextRoomName) {
                    @Override
                    public ASResponse doRequest() {
                        //TODO: APIUtils
                        return null;
                    }

                    @Override
                    public void onSuccess(ASResponse asp) {
                        //TODO: go into room
                    }

                    @Override
                    public void onFailed(int code, String desc) {
                        DialogMaker.dismissProgressDialog();
                        //TODO: other on failed checks
                        super.onFailed(code, desc);
                    }

                    @Override
                    public void onException(Exception e) {
                        DialogMaker.dismissProgressDialog();
                        super.onException(e);
                    }
                }.start();
            }
        });
    }
}
