package com.pine.base.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.pine.base.R;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by tanghongfeng on 2018/10/30
 */

public class ProvinceSelectDialog extends Dialog {
    /**
     * 所有省
     */
    protected static ArrayList<String> mProvinceList;
    /**
     * key - 省 value - 市
     */
    protected static Map<String, ArrayList<String>> mCityListMap = new HashMap<>();
    /**
     * key - 市 values - 区
     */
    protected static Map<String, ArrayList<String>> mDistrictListMap = new HashMap<>();
    /**
     * key - 区 values - 邮编
     */
    protected static Map<String, String> mZipCodeMap = new HashMap<>();
    private ProvinceSelectDialog.Builder mBuilder;

    public ProvinceSelectDialog(@NonNull Context context) {
        super(context);
        if (mProvinceList == null || mProvinceList.size() < 1) {
            parseData(context);
        }
    }

    public ProvinceSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        if (mProvinceList == null || mProvinceList.size() < 1) {
            parseData(context);
        }
    }

    private void parseData(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("province_data.xml");
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(inputStream, new DefaultHandler() {
                private ArrayList<String> cityList, districtList;

                @Override
                public void startDocument() throws SAXException {
                    // 当读到第一个开始标签的时候，会触发这个方法
                    mProvinceList = new ArrayList<>();
                }

                @Override
                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {
                    // 当遇到开始标记的时候，调用这个方法
                    if (qName.equals("province")) {
                        mProvinceList.add(attributes.getValue(0));
                        cityList = new ArrayList<>();
                    } else if (qName.equals("city")) {
                        cityList.add(attributes.getValue(0));
                        districtList = new ArrayList<>();
                    } else if (qName.equals("district")) {
                        districtList.add(attributes.getValue(0));
                        mZipCodeMap.put(attributes.getValue(0), attributes.getValue(1));
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName)
                        throws SAXException {
                    // 遇到结束标记的时候，会调用这个方法
                    if (qName.equals("district")) {

                    } else if (qName.equals("city")) {
                        mDistrictListMap.put(cityList.get(cityList.size() - 1), districtList);
                    } else if (qName.equals("province")) {
                        mCityListMap.put(mProvinceList.get(mProvinceList.size() - 1), cityList);
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length)
                        throws SAXException {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ProvinceSelectDialog.Builder getBuilder() {
        return mBuilder;
    }

    public interface IDialogDateSelected {
        void onSelected(String provinceName, String cityName, String districtName, String zipCode);
    }

    public static class Builder {
        private TextView cancelBtn, confirmBtn;
        private WheelPicker wheelProvince, wheelCity, wheelDistrict;
        private int curProvinceIndex, curCityIndex, curDistrictIndex;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public ProvinceSelectDialog create(IDialogDateSelected dialogSelect) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ProvinceSelectDialog dialog = new ProvinceSelectDialog(context, R.style.BaseSelectDialogStyle);
            View layout = inflater.inflate(R.layout.base_dialog_locate_select, null);
            cancelBtn = layout.findViewById(R.id.cancel_tv);
            confirmBtn = layout.findViewById(R.id.confirm_tv);
            wheelProvince = layout.findViewById(R.id.wheel_one);
            wheelCity = layout.findViewById(R.id.wheel_two);
            wheelDistrict = layout.findViewById(R.id.wheel_three);
            initViewOnCreate(dialog, dialogSelect);
            dialog.setContentView(layout);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager m = ((Activity) context).getWindowManager();
            Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
            p.width = d.getWidth(); //宽度设置为屏幕
            dialog.getWindow().setAttributes(p); //设置生效
            dialog.mBuilder = this;
            return dialog;
        }

        private void initViewOnCreate(final ProvinceSelectDialog dialog, final IDialogDateSelected dialogSelect) {
            wheelProvince.setData(mProvinceList);
            wheelProvince.setSelectedItemPosition(curProvinceIndex);
            wheelCity.setData(mCityListMap.get(mProvinceList.get(curProvinceIndex)));
            wheelCity.setSelectedItemPosition(curCityIndex);
            wheelDistrict.setData(mDistrictListMap.get(mCityListMap.get(mProvinceList.get(curProvinceIndex)).get(curCityIndex)));
            wheelDistrict.setSelectedItemPosition(curDistrictIndex);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (dialogSelect != null) {
                        String provinceName = mProvinceList.get(curProvinceIndex);
                        String cityName = mCityListMap.get(provinceName).get(curCityIndex);
                        String districtName = mDistrictListMap.get(cityName).get(curDistrictIndex);
                        dialogSelect.onSelected(provinceName, cityName, districtName,
                                mZipCodeMap.get(districtName));
                    }
                }
            });
            wheelProvince.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    curProvinceIndex = i;
                    updateCity();
                }
            });
            wheelCity.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    curCityIndex = i;
                    updateDistrict();
                }
            });
            wheelDistrict.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                    curDistrictIndex = i;
                }
            });
        }

        private void updateCity() {
            wheelCity.setData(mCityListMap.get(mProvinceList.get(curProvinceIndex)));
            curCityIndex = 0;
            wheelCity.setSelectedItemPosition(curCityIndex);
            updateDistrict();
        }

        private void updateDistrict() {
            wheelDistrict.setData(mDistrictListMap.get(mCityListMap.get(mProvinceList.get(curProvinceIndex)).get(curCityIndex)));
            curDistrictIndex = 0;
            wheelDistrict.setSelectedItemPosition(curDistrictIndex);
        }
    }
}