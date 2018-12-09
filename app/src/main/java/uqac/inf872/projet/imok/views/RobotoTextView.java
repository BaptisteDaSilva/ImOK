package uqac.inf872.projet.imok.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import uqac.inf872.projet.imok.R;

public class RobotoTextView extends AppCompatTextView {

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.applyStyle(context, attrs);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.applyStyle(context, attrs);
    }

    // ---

    private void applyStyle(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
        int cf = a.getInteger(R.styleable.RobotoTextView_fontNameTextView, 0);
        int fontName;
        switch (cf) {
            case 1:
                fontName = R.string.Roboto_Bold;
                break;
            case 2:
                fontName = R.string.Roboto_Light;
                break;
            case 3:
                fontName = R.string.Roboto_Medium;
                break;
            case 4:
                fontName = R.string.Roboto_Thin;
                break;
            default:
                fontName = R.string.Roboto_Medium;
                break;
        }

        String customFont = getResources().getString(fontName);

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + customFont + ".ttf");
        setTypeface(tf);
        a.recycle();
    }
}
