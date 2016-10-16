package com.carlosapps.beastshopping.views.ShareListView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_user_userName)
    public TextView userName;

    @BindView(R.id.list_user_itemView)
    public ImageView userItemView;

    public ShareListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        itemView.setTag(user);
        userName.setText(user.getName());
    }
}
