package com.havrylyuk.earthquakes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;


/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakesAdapter extends RecyclerView.Adapter<EarthquakesAdapter.ItemHolder> {

    public interface ItemClickListener {
        void onItemClick(long id);
    }

    private ItemClickListener listener;
    private Context context;
    private Cursor cursor;

    public EarthquakesAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.earthquakes_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        cursor.moveToPosition(position);
        final long id = cursor.getLong(cursor.getColumnIndex(EarthquakesEntry._ID));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(id);
                }
            }
        });
        String datetime = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_DATE_TIME));
        holder.datetime.setText(datetime);
        float depth = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_DEPTH));
        float lng = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LNG));
        float lat = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LAT));
        holder.details.setText(context.getString(R.string.format_depth, depth, lng, lat));
        String magnitude = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_MAGNITUDE));
        holder.magnitude.setText(magnitude);

    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private  View view;
        private TextView datetime;
        private  TextView details;
        private  TextView magnitude;

        public ItemHolder(View view) {
            super(view);
            this.view = view;
            this.magnitude = (TextView) view.findViewById(R.id. list_item_magnitude );
            this.datetime = (TextView) view.findViewById(R.id.list_item_name);
            this.details =    (TextView) view.findViewById(R.id.list_item_sub_name);


        }
    }
}
