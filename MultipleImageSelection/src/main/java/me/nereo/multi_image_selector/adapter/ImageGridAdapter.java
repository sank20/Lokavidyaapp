package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;

/**
 * ImageAdapter
 *
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    public ImageGridAdapter(Context context, boolean showCamera){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
    }
    /**
     * Display selection indicator
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }

    /**
     * Select a picture, change the selection status
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * Set the default path selected by the picture
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * Set dataset
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * Reset each Column of Size
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if(mItemSize == columnWidth){
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera){
            return position==0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size()+1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if(showCamera){
            if(i == 0){
                return null;
            }
            return mImages.get(i-1);
        }else{
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        if(type == TYPE_CAMERA){
            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
            view.setTag(null);
        }else if(type == TYPE_NORMAL){
            ViewHolde holde;
            if(view == null){
                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                holde = new ViewHolde(view);
            }else{
                holde = (ViewHolde) view.getTag();
                if(holde == null){
                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
                    holde = new ViewHolde(view);
                }
            }
            if(holde != null) {
                holde.bindData(getItem(i));
            }
        }

        /** Fixed View Size */
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if(lp.height != mItemSize){
            view.setLayoutParams(mItemLayoutParams);
        }

        return view;
    }

    class ViewHolde {
        ImageView image;
        ImageView indicator;
        View mask;

        ViewHolde(View view){
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);
        }

        void bindData(final Image data){
            if(data == null) return;
            // Processing state radio and multiple choice
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedImages.contains(data)){
                    // Setting the selected state
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    // Not selected
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);

            if(mItemSize > 0) {
                // Display Pictures
                Picasso.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                                //.error(R.drawable.default_error)
                        .resize(mItemSize, mItemSize)
                        .centerCrop()
                        .into(image);
            }
        }
    }

}
