package com.grab.wifigrabber;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Třída, která přidává funkcionalitu pro tlačítko.
 */
public class DescriptionButtonListAdapter extends DescriptionListAdapter
{
    int buttonId;
    MainActivity activity;
    /**
     * Konstruktor
     *
     * @param context
     * @param resource         - Layout použitý pro jednotlivé prvky
     * @param titleTextViewId  - id textView, do které patří název položky
     * @param descTextViewId   - id textView do kterého patří podrobnosti o položce
     * @param descHideLayoutId - id layoutu, který obsahuje položky co se mají po kliknutí objevit/schovat
     * @param Titles           - seznam jednotlivých položek
     * @param Descriptions     - seznam podrobností pro položky
     */
    public DescriptionButtonListAdapter(Context context, int resource, int titleTextViewId, int descTextViewId,
            int descHideLayoutId, List<String> Titles, List<String> Descriptions,int buttonId,MainActivity activity)
    {
        super(context, resource, titleTextViewId, descTextViewId, descHideLayoutId, Titles, Descriptions);
        this.buttonId = buttonId;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final View res = super.getView(position,convertView,parent);

        if(convertView == null)
        {
            Button b = (Button) res.findViewById(buttonId);

            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    TextView title = (TextView) res.findViewById(titleTextViewId);
                    activity.buttonPress(String.valueOf(title.getText()));
                }
            });
        }

        return res;
    }
}
