package com.liquidchoco.contact.customClass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.liquidchoco.contact.singleton.InterfaceManager;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class RobotoRegularEditText extends EditText {
    public RobotoRegularEditText(Context ctx, AttributeSet attributeSet){
        super(ctx,attributeSet);
        if (!isInEditMode()) {
            this.setTypeface(InterfaceManager.sharedInstance().getRobotoRegularTypeFace());
        }
    }
}
