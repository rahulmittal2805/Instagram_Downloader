package com.example.instagramdownloader.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.example.instagramdownloader.Activity.PlayVideoActivity;
import com.example.instagramdownloader.R;

import com.example.instagramdownloader.model.ResponseModel;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends PagerAdapter {

     Context mContext;
    ResponseModel _data;
    LayoutInflater layoutInflater;

    //RelativeLayout toptoolbar;

    public  ImageAdapter(){

    }

    public ImageAdapter(Activity activity,ResponseModel _data) {
        this.mContext = activity;
        this._data = _data;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
       return  _data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = layoutInflater.inflate(R.layout.activity_image_show, container, false);
        ImageView photoView = itemView.findViewById(R.id.photoView);
       // VideoView videoView = itemView.findViewById(R.id.videoView);
        ImageView PlayButtonImage=itemView.findViewById(R.id.PlayButtonImage);

       if( _data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(position).getNode().get__typename().equals("GraphImage")){
           Picasso.get().load(_data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(position).getNode().getDisplay_url()).into(photoView);
//           videoView.setVisibility(View.GONE);
       }else if(_data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(position).getNode().get__typename().equals( "GraphVideo")){

           Picasso.get().load(_data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(position).getNode().getDisplay_url()).into(photoView);
           PlayButtonImage.setVisibility(View.VISIBLE);
           final String Video_url = _data.getGraphql().getShortcode_media().getEdge_sidecar_to_children().getEdges().get(position).getNode().getVideo_url();
           PlayButtonImage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i=new Intent(mContext, PlayVideoActivity.class);
                   i.putExtra("Video URL",Video_url);
                   mContext.startActivity(i);
               }
           });

           //Creating MediaController
         //  MediaController mediaController= new MediaController(mContext);

//           videoView.setVisibility(View.VISIBLE);
//           mediaController.setAnchorView(videoView);
//           String url = _data.getEdges().get(position).getNode().getVideo_url();
//           videoView.setMediaController(mediaController);
//           videoView.setVideoURI(Uri.parse(url));
//           videoView.requestFocus();
//           videoView.start();
       }






        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
