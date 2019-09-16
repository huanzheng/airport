package com.invent.airport.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.invent.airport.R;
import com.invent.airport.requests.RequestLocationQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zpSDK.zpSDK.zpSDK;

public class ActivityPrint extends AirportBaseActivity {
    /**
     * Called when the activity is first created.
     */
    public static final String KEY_PRINT_AST_NAME = "print_ast_name";
    public static final String KEY_PRINT_AST_BARCODE = "print_ast_barcode";
    public static BluetoothAdapter myBluetoothAdapter;
    public String SelectedBDAddress;
    private Button mPrint;
    private Button mStop;
    StatusBox statusBox;
    private String mAssetName;
    private String mAssetBarcode;
    private RequestLocationQuery.LocationInfo mLocationInfo;
    private ArrayList<RequestLocationQuery.LocationInfo> mLocationInfos =
            new ArrayList<RequestLocationQuery.LocationInfo>();
    private Handler mMainHandler;
    private static final int DELAY = 2000;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAssetName = getIntent().getStringExtra(KEY_PRINT_AST_NAME);
        mAssetBarcode = getIntent().getStringExtra(KEY_PRINT_AST_BARCODE);
        mLocationInfo = getIntent().getParcelableExtra(ActivitySearchLocation.LOCATION_INFO);
        mLocationInfos = getIntent().getParcelableArrayListExtra(ActivitySearchLocation.LOCATION_INFOS);
        setContentView(R.layout.activity_print);
        if (!ListBluetoothDevice()) finish();
        mPrint = (Button) findViewById(R.id.activity_print_print);
        mStop = (Button) findViewById(R.id.activity_print_stop);

        if (mLocationInfos != null && !mLocationInfos.isEmpty()) {
            mStop.setVisibility(View.VISIBLE);
            mStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mStopPrint = true;
                }
            });
        }

        statusBox = new StatusBox(this, mPrint);
        mMainHandler = new Handler(Looper.getMainLooper());

        mPrint.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if (mAssetName != null && mAssetBarcode != null) {
                    printAssetBarcode(SelectedBDAddress, mAssetName, mAssetBarcode);
                } else if (mLocationInfo != null) {
                    printAssetBarcode(SelectedBDAddress, mLocationInfo.mName, mLocationInfo.mBarcode);
                } else if (mLocationInfos != null && !mLocationInfos.isEmpty()) {
                    startPrintAllLocations();
                }
            }
        });

    }

    @SuppressWarnings("deprecation")
    public boolean ListBluetoothDevice() {
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ListView listView = (ListView) findViewById(R.id.listView1);
        MyAdapter m_adapter = new MyAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[]{"DeviceName", "BDAddress"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        listView.setAdapter(m_adapter);

        if ((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
            Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() <= 0) return false;
        for (BluetoothDevice device : pairedDevices) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("DeviceName", device.getName());
            map.put("BDAddress", device.getAddress());
            list.add(map);
        }
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedBDAddress = list.get(position).get("BDAddress");
                if (((ListView) parent).getTag() != null) {
                    ((View) ((ListView) parent).getTag()).setBackgroundDrawable(null);
                }
                ((ListView) parent).setTag(view);
                view.setBackgroundColor(Color.BLUE);
            }
        });
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean OpenPrinter(String BDAddress) {
        if (BDAddress == "" || BDAddress == null) {
            Toast.makeText(this, "没有选择打印机", Toast.LENGTH_SHORT).show();
            return false;
        }
        BluetoothDevice myDevice;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "读取蓝牙设备错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        myDevice = myBluetoothAdapter.getRemoteDevice(BDAddress);
        if (myDevice == null) {
            Toast.makeText(this, "读取蓝牙设备错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (zpSDK.zp_open(myBluetoothAdapter, myDevice) == false) {
            Toast.makeText(this, zpSDK.ErrorMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void printAssetBarcode(String BDAddress, String name, String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return;
        }
        if (mLocationInfos != null && !mLocationInfos.isEmpty()) {
            statusBox.Show("正在打印..." + (mCurrentPrintPosition + 1) + "/" + mLocationInfos.size());
        } else {
            statusBox.Show("正在打印...");
        }
        if (!OpenPrinter(BDAddress)) {
            statusBox.Close();
            return;
        }
        if (!zpSDK.zp_page_create(82, 42)) {
            Toast.makeText(this, "创建打印页面失败", Toast.LENGTH_SHORT).show();
            statusBox.Close();
            return;
        }
        zpSDK.TextPosWinStyle = true;
        //zpSDK.zp_draw_rect(0.1, 0.1, 75, 30, 2);
        if (name != null && !name.isEmpty())
            zpSDK.zp_draw_text_ex(5, 4, name, "宋体", 4, 0, false, false, false);
        zpSDK.zp_draw_barcode(5, 10, barcode, zpSDK.BARCODE_TYPE.BARCODE_CODE128, 10, 2, 0);

        zpSDK.zp_page_print(false);
        zpSDK.zp_printer_status_detect();
        zpSDK.zp_goto_mark_label(30);
        if (zpSDK.zp_printer_status_get(8000) != 0) {
            Toast.makeText(this, zpSDK.ErrorMessage, Toast.LENGTH_SHORT).show();
        }
        zpSDK.zp_page_free();
        zpSDK.zp_close();
        statusBox.Close();
    }

    private class MyAdapter extends SimpleAdapter {

        public MyAdapter(Context context, List<? extends Map<String, ?>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            v.setBackgroundResource(R.drawable.selector_bg_list_item);
            TextView t1 = (TextView) v.findViewById(android.R.id.text1);
            TextView t2 = (TextView) v.findViewById(android.R.id.text2);
            t1.setTextColor(Color.BLACK);
            t2.setTextColor(Color.BLACK);
            return v;
        }
    }

    private ContinuousPrintRunnable mContinuousPrintRunnable = new ContinuousPrintRunnable();
    private int mCurrentPrintPosition = 0;
    private boolean mStopPrint = false;

    private class ContinuousPrintRunnable implements Runnable {
        @Override
        public void run() {
            RequestLocationQuery.LocationInfo locationInfo = mLocationInfos.get(mCurrentPrintPosition);
            printAssetBarcode(SelectedBDAddress, locationInfo.mName, locationInfo.mBarcode);

            mCurrentPrintPosition++;
            if (mCurrentPrintPosition < mLocationInfos.size() && !mStopPrint) {
                mMainHandler.postDelayed(mContinuousPrintRunnable, DELAY);
            }
        }
    }

    void startPrintAllLocations() {
        mCurrentPrintPosition = 0;
        mStopPrint = false;
        mMainHandler.post(mContinuousPrintRunnable);
    }
}

