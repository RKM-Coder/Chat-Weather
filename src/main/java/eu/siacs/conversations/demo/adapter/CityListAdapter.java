package eu.siacs.conversations.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;



import java.util.ArrayList;

import eu.siacs.conversations.R;

import static android.view.View.inflate;

/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public class CityListAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private ArrayList<String> listOfItem;

    public CityListAdapter(Context context, ArrayList<String> listOfItem) {
        this.context = context;
        this.listOfItem = listOfItem;
    }

    @Override
    public int getCount() {
        return listOfItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = inflate(context, R.layout.city_spinner_layout, null);
        TextView cityName = view.findViewById(R.id.txt_city_name);

        cityName.setText(listOfItem.get(position).toString());
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflate(context, R.layout.city_spinner_layout, null);
        TextView cityName = view.findViewById(R.id.txt_city_name);

        cityName.setText(listOfItem.get(position).toString());
        return view;
    }

    public void setValues(ArrayList<String> listOfItem){
        this.listOfItem.clear();
        this.listOfItem.addAll(listOfItem);
        this.notifyDataSetChanged();
    }
}
