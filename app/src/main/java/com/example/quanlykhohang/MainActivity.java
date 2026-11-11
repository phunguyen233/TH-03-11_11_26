package com.example.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Màn hình 2: Danh sách sản phẩm + khối thống kê.
 */
public class MainActivity extends AppCompatActivity {

    /** Khóa dùng để truyền id sản phẩm qua Intent. */
    public static final String EXTRA_PRODUCT_ID = "product_id";

    private TextView tvTotalCount, tvTotalValue, tvLowStock, tvEmpty;
    private ListView lvProducts;

    private DatabaseHelper dbHelper;
    // ArrayList chỉ chứa kết quả truy vấn để hiển thị, dữ liệu chính nằm trong SQLite
    private final ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStock = findViewById(R.id.tvLowStock);
        tvEmpty = findViewById(R.id.tvEmpty);
        lvProducts = findViewById(R.id.lvProducts);
        Button btnAdd = findViewById(R.id.btnAdd);

        adapter = new ProductAdapter(this, products);
        lvProducts.setAdapter(adapter);
        lvProducts.setEmptyView(tvEmpty); // tự hiện thông báo khi danh sách rỗng

        // Nhấn vào một dòng -> mở chi tiết, chỉ truyền id
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra(EXTRA_PRODUCT_ID, products.get(position).getId());
                startActivity(intent);
            }
        });

        // Nút Thêm sản phẩm
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });
    }

    /** Mỗi lần màn hình hiện lại (sau khi thêm / xóa / đổi số lượng) thì tải lại dữ liệu. */
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Danh sách
        products.clear();
        products.addAll(dbHelper.getAllProducts());
        adapter.notifyDataSetChanged();

        // Thống kê
        tvTotalCount.setText(String.valueOf(products.size()));
        tvTotalValue.setText(Product.formatMoney(dbHelper.getTotalValue()));
        tvLowStock.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
