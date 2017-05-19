package com.lyyjy.yfyb.bionicfish.HelpDocument;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyyjy.yfyb.bionicfish.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 */

public class HelpItemFragment extends Fragment {

    private static final String TAG = HelpItemFragment.class.getSimpleName();

    private static final String ARG_HTML_TEXT = "html_text";

    private TextView mTextView;
    private String mHtmlText;

    public static HelpItemFragment newInstance(String htmlText) {
        Bundle args = new Bundle();
        args.putString(ARG_HTML_TEXT, htmlText);

        HelpItemFragment fragment = new HelpItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHtmlText = getArguments().getString(ARG_HTML_TEXT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_item, container, false);

        mTextView = (TextView) view.findViewById(R.id.help_document_text_view);

        if (Build.VERSION.SDK_INT<24){
            mTextView.setText(Html.fromHtml(mHtmlText,mImageGetter,null));
        }else{
            mTextView.setText(Html.fromHtml(mHtmlText,Html.FROM_HTML_MODE_LEGACY,mImageGetter,null));
        }

        return view;
    }

    private Html.ImageGetter mImageGetter=new Html.ImageGetter() {

        private final Map<String,Integer> RESOURCE_MAP=new HashMap<String,Integer>(){
            {
                put("bluetooth_enable", R.drawable.bluetooth_enable);
                put("search",R.drawable.search);
                put("disconnected",R.drawable.disconnected);
                put("connected",R.drawable.connected);
                put("manual",R.drawable.manual);
                put("sensor",R.drawable.sensor);
                put("light_color",R.drawable.light_color);
                put("fish_specification", R.drawable.fish_specification);
                put("fish_quick_tour", R.drawable.fish_quick_tour);
                put("battery", R.drawable.battery);
            }
        };

        @Override
        public Drawable getDrawable(String s) {
            Integer id=RESOURCE_MAP.get(s);
            if (id==null){
                return null;
            }
            Drawable drawable;

            if (Build.VERSION.SDK_INT<22){
                drawable=getResources().getDrawable(id);
            }else {
                drawable = getResources().getDrawable(id, null);
            }

            if (s.equals("fish_quick_tour") || s.equals("fish_specification")){
                int width=getResources().getDisplayMetrics().widthPixels;
//                int height=getResources().getDisplayMetrics().heightPixels;
                int fishWidth= width/2;
                int fishHeight= (int) (drawable.getIntrinsicHeight()*1.0f/drawable.getIntrinsicWidth()*fishWidth);

                int marginLeft=(width-fishWidth)/2;
                drawable.setBounds(marginLeft,0,marginLeft+fishWidth,fishHeight);
            }else{
                int size= (int) getResources().getDimension(R.dimen.help_document_text_size);
                int width= (int) (drawable.getIntrinsicWidth()*(1.25*size)/drawable.getIntrinsicHeight());
                drawable.setBounds(0,-size/4,width,size);
            }
            return drawable;
        }
    };
}
