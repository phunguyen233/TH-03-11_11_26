package com.example.quanlykhohang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter đưa danh sách sản phẩm (kết quả truy vấn) lên ListView.
 */
public class ProductAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);
        }

        TextView tvAvatar = convertView.findViewById(R.id.tvItemAvatar);
        TextView tvName = convertView.findViewById(R.id.tvItemName);
        TextView tvQuantity = convertView.findViewById(R.id.tvItemQuantity);
        TextView tvPrice = convertView.findViewById(R.id.tvItemPrice);
        TextView tvCategory = convertView.findViewById(R.id.tvItemCategory);
        TextView tvWarning = convertView.findViewById(R.id.tvItemWarning);

        Product p = products.get(position);
        tvAvatar.setText(p.getInitial());
        tvName.setText(p.getName());
        tvQuantity.setText("SL: " + p.getQuantity());
        tvPrice.setText(Product.formatMoney(p.getPrice()));
        tvCategory.setText(p.getCategory());

        // Sản phẩm sắp hết hàng: hiện nhãn cảnh báo và nhãn số lượng màu đỏ
        if (p.isLowStock()) {
            tvWarning.setVisibility(View.VISIBLE);
            tvQuantity.setTextColor(context.getColor(R.color.red));
            tvQuantity.setBackgroundResource(R.drawable.bg_chip_warning);
        } else {
            tvWarning.setVisibility(View.GONE);
            tvQuantity.setTextColor(context.getColor(R.color.primary));
            tvQuantity.setBackgroundResource(R.drawable.bg_chip);
        }
        return convertView;
    }
}
