package com.liquidchoco.contact.customClass;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.liquidchoco.contact.singleton.InterfaceManager;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class RobotoRegularTextView extends TextView {
    public RobotoRegularTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            this.setTypeface(InterfaceManager.sharedInstance().getRobotoRegularTypeFace());
        }
    }

    public RobotoRegularTextView(Context context) {
        super(context, null);
    }
}
