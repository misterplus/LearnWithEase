package team.one.lwe.ui.listener;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

public class TextLockListener implements View.OnClickListener {

    EditText editText;

    public TextLockListener(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void onClick(View view) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        editText.setTextColor(Color.GRAY);
    }
}
