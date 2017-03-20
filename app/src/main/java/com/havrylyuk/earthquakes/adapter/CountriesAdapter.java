package com.havrylyuk.earthquakes.adapter;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.data.EarthquakesContract.CountriesEntry;


/**
 *
 * Created by Igor Havrylyuk on 08.03.2017.
 */

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ItemHolder> {

    public interface ItemClickListener {
        void onItemClick(long id);
    }

    private ItemClickListener listener;
    private Cursor cursor;

    public CountriesAdapter(ItemClickListener listener) {
        this.listener = listener;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        cursor.moveToPosition(position);
        final long id = cursor.getLong(cursor.getColumnIndex(CountriesEntry._ID));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(id);
                }
            }
        });
        String country = cursor.getString(cursor.getColumnIndex(CountriesEntry.COUNTRY_COUNTRY_NAME));
        holder.country.setText(country);
        String capital = cursor.getString(cursor.getColumnIndex(CountriesEntry.COUNTRY_CAPITAL));
        holder.capital.setText(capital);
        String flagUrl = "http://www.geonames.org/flags/m/"+
                cursor.getString(cursor.getColumnIndex(CountriesEntry.COUNTRY_COUNTRY_CODE))
                        .toLowerCase()+".png";
        holder.flag.setImageURI(Uri.parse(flagUrl));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private  View view;
        private SimpleDraweeView flag;
        private  TextView country;
        private  TextView capital;

        public ItemHolder(View view) {
            super(view);
            this.view = view;
            this.flag = (SimpleDraweeView) view.findViewById(R.id.list_item_icon);
            this.country = (TextView) view.findViewById(R.id.list_item_name);
            this.capital = (TextView) view.findViewById(R.id.list_item_sub_name);
        }
    }
}
