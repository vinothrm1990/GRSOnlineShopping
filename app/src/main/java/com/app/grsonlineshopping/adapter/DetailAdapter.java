package com.app.grsonlineshopping.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.grsonlineshopping.R;
import com.app.grsonlineshopping.helper.Constants;
import com.app.grsonlineshopping.helper.ImageCache;
import java.util.ArrayList;

public class DetailAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> detailList;
    ImageLoader imageLoader;

    public DetailAdapter(Context context, ArrayList<String> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    @Override
    public int getCount() {
        return detailList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.pager_layout, container, false);

        NetworkImageView imageView = view.findViewById(R.id.detail_iv_slide);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Constants.IMAGE_URL + detailList.get(position), ImageLoader.getImageListener(imageView, R.drawable.ic_image, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(Constants.IMAGE_URL + detailList.get(position), imageLoader);
        imageView.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        container.addView(view);
        return  view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
