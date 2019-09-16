package com.invent.airport.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.invent.airport.R;
import com.invent.airport.requests.RequestLocationQuery;

import java.util.ArrayList;

public final class ActivitySearchLocationResult extends AirportBaseActivity {
    ListView mListView;
    TextView mTitle;

    ArrayList<RequestLocationQuery.LocationInfo> mData =
            new ArrayList<RequestLocationQuery.LocationInfo>();

    private static final int MENU_PRINT = 1;


    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_result);
        mTitle = (TextView) findViewById(R.id.aty_search_result_title);
        mListView = (ListView) findViewById(R.id.aty_search_result_list);
        mData = getIntent().getParcelableArrayListExtra(ActivitySearchLocation.LOCATION_INFOS);
        mTitle.setText("搜索结果" + mData.size() + "条");
        LocationInfosAdapter adapter = new LocationInfosAdapter();
        mListView.setAdapter(adapter);
        registerForContextMenu(mListView);
    }

    private final class LocationInfosAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v = convertView;
            ViewHodlerLocationInfo viewHolder;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item_location, null);
                viewHolder = new ViewHodlerLocationInfo();
                viewHolder.mLocationName = (TextView) v.findViewById(R.id.location_name);
                viewHolder.mLocationBarcode = (TextView) v.findViewById(R.id.location_barcode);
                viewHolder.mPosition = position;
                v.setTag(viewHolder);
            } else {
                viewHolder = (ViewHodlerLocationInfo) v.getTag();
            }

            viewHolder.mLocationName.setText(mData.get(position).mName);
            viewHolder.mLocationBarcode.setText(mData.get(position).mBarcode);
            return v;
        }
    }

    public class ViewHodlerLocationInfo {
        public TextView mLocationName;
        public TextView mLocationBarcode;
        public int mPosition;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_printall, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_printall:
                Intent i = new Intent(mContext, ActivityPrint.class);
                i.putExtra(ActivitySearchLocation.LOCATION_INFOS, mData);
                mContext.startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("打印");
        menu.add(0, MENU_PRINT, 1, "打印此库位条码");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        ViewHodlerLocationInfo holder = (ViewHodlerLocationInfo) info.targetView.getTag();
        int trailId = holder.mPosition;
        RequestLocationQuery.LocationInfo locationInfo = mData.get(trailId);
        switch (item.getItemId()) {
            case MENU_PRINT:
                Intent i = new Intent(this, ActivityPrint.class);
                i.putExtra(ActivitySearchLocation.LOCATION_INFO, locationInfo);
                startActivity(i);
                break;
        }
        return true;
    }
}
