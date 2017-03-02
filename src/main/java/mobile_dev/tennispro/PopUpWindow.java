package mobile_dev.tennispro;

import android.app.ActionBar;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by eli on 4/20/2015.
 */
public class PopUpWindow {
    private Context _context;
    private PopupWindow _popupWindow;


    public PopUpWindow(Context context){
       _context=context;
    }

    public void popWindow() {


        final View popupView = View.inflate(_context, R.layout.popup, null);

        _popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        _popupWindow.setAnimationStyle(R.style.Animation);

        popupView.post(new Runnable() {
            public void run() {
                _popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 0);

            }

        });
    }
    public void dismiss()
    {
        _popupWindow.dismiss();
    }


    public void setTexts(String headline,String sub)
    {
        ((TextView)  _popupWindow.getContentView().findViewById(R.id.headline)).setText(headline);
        ((TextView)  _popupWindow.getContentView().findViewById(R.id.sub)).setText(sub);


    }
}
