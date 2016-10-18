package com.grab.wifigrabber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Třída která zařídí aby listView zobrazil seznam položek, které lze rozkliknout pro podrobnosti.
 */
public class DescriptionListAdapter extends ArrayAdapter
{
    protected List<String> Titles;
    protected List<String> Descriptions;
    protected int descHideLayoutId;
    protected int titleTextViewId;
    protected int descTextViewId;
    protected int resource;
    /**
     * Konstruktor
     * @param context
     * @param resource - Layout použitý pro jednotlivé prvky
     * @param titleTextViewId - id textView, do kterého patří název položky
     * @param descTextViewId - id textView do kterého patří podrobnosti o položce
     * @param descHideLayoutId - id layoutu, který obsahuje položky co se mají po kliknutí objevit/schovat
     * @param Titles - seznam jednotlivých položek
     * @param Descriptions - seznam podrobností pro položky
     */
    public DescriptionListAdapter(Context context, int resource,int titleTextViewId,int descTextViewId,
            int descHideLayoutId,List<String> Titles,List<String> Descriptions)
    {
        super(context,resource,titleTextViewId,Titles);
        this.Titles = Titles;
        this.Descriptions = Descriptions;
        this.titleTextViewId = titleTextViewId;
        this.descTextViewId = descTextViewId;
        this.resource = resource;
        this.descHideLayoutId = descHideLayoutId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        if(convertView != null) // pokud jsme na miste ktere je inicializovane, pouze vyplnime hodnoty
        {
            setValuesToView(convertView,position);
            return convertView;
        }
        else // pokud ne vytvoříme nový
        {
            final View view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            setValuesToView(view, position);
            TextView t = (TextView)view.findViewById(titleTextViewId);

            t.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    LinearLayout t = (LinearLayout) view.findViewById(descHideLayoutId);
                    if(t.getVisibility() == View.GONE)
                    {
                        t.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        t.setVisibility(View.GONE);
                    }
                }
            });

            LinearLayout l = (LinearLayout) view.findViewById(descHideLayoutId);
            l.setVisibility(View.GONE);

            return view;
        }
    }

    /**
     * Metoda, která naplní daný view hodnotamy
     * @param targetView - view který se má naplnit
     * @param index - index hodnoty z listu, který se má použít
     */
    void setValuesToView(View targetView,int index)
    {
        TextView t = (TextView)targetView.findViewById(titleTextViewId);
        t.setText(Titles.get(index));
        t = (TextView)targetView.findViewById(descTextViewId);
        t.setText(Descriptions.get(index));
    }

    public void setTitles(List<String> Titles)
    {
        this.Titles = Titles;
        clear();
        addAll(Titles);
    }

    public void setDescriptions(List<String> Descriptions)
    {
        this.Descriptions = Descriptions;
        super.notifyDataSetChanged();
    }
}
