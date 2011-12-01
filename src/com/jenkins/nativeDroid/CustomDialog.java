package com.jenkins.nativeDroid;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

/** Class Must extends with Dialog */
/** Implement onClickListener to dismiss dialog when OK Button is pressed */
public class CustomDialog extends Dialog  {
	public CustomDialog(Context context, String status) {
		super(context);
		this.setContentView(R.layout.popup_dialog);
		this.setCancelable(true);
		
		if (status == "New") {
			this.setTitle("New server");
		} else {
			this.setTitle("Edit server");
		}
		
        Button cancelbutton = (Button) findViewById(R.id.Button02);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
                dismiss();
            }
        });
        this.show();
	}
}
