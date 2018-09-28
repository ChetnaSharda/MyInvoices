package com.andrinotech.myinvoices.ui.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.R;

import java.util.ArrayList;
import java.util.List;

public class InvoiceListAdapter extends RecyclerView.Adapter<InvoiceListAdapter.ViewHolder> {

    List<Invoices> itemList;
    Context context;
    private LayoutInflater mInflater;
    static OnItemClickListener mItemClickListener;

    public InvoiceListAdapter(List<Invoices> innerEntities, Context context) {
        this.itemList = innerEntities;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

    }

    public void setItemList(ArrayList<Invoices> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int pos);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_invoice, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Invoices invoices = itemList.get(position);
        holder.date.setText(invoices.getDate());
        holder.title.setText(invoices.getTitle());
        holder.shop.setText(invoices.getShopName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, shop;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            shop = itemView.findViewById(R.id.shop);
        }
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
