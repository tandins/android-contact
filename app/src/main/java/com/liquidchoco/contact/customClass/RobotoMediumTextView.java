package com.liquidchoco.contact.customClass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.liquidchoco.contact.singleton.InterfaceManager;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class RobotoMediumTextView extends TextView {
    public RobotoMediumTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        if (!isInEditMode()) {
            this.setTypeface(InterfaceManager.sharedInstance().getRobotoMediumTypeFace());
        }
    }

    public RobotoMediumTextView(Context context) {
        super(context, null);
        if (!isInEditMode()) {
        }
    }
}
