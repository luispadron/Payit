package lpadron.me.project1_payit.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RalewayTextView extends TextView{

    public RalewayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Raleway-Regular.ttf"));
    }
}
