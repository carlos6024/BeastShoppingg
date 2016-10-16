package com.carlosapps.beastshopping.views.ItemListViews;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.Item;
import com.carlosapps.beastshopping.infrastructure.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_shopping_item_itemName)
    public TextView itemName;

    @BindView(R.id.list_shopping_item_itemView)
    public ImageView itemView;

    @BindView(R.id.list_shopping_item_boughtBy)
    TextView boughtBy;


    public ListItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(Item item, Context context,String currentUserEmail){
        itemView.setTag(item);
        itemName.setText(item.getItemName());

        if (item.isBought()){
            itemName.setPaintFlags(itemName.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            itemView.setImageResource(R.mipmap.ic_check);
            boughtBy.setVisibility(View.VISIBLE);
            if (currentUserEmail.equals(item.getBoughtBy())){
                boughtBy.setText(context.getString(R.string.bought_by,"You"));
            } else{
                boughtBy.setText(context.getString(R.string.bought_by, Utils.decodeEmail(item.getBoughtBy())));
            }
        } else {
            itemName.setPaintFlags(itemName.getPaintFlags()& (~Paint.STRIKE_THRU_TEXT_FLAG));
            itemView.setImageResource(R.mipmap.ic_trash);
            boughtBy.setVisibility(View.GONE);
        }

    }
}
