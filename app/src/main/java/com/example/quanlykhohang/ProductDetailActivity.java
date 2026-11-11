package com.example.quanlykhohang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình 4: Chi tiết sản phẩm. Nhận id qua Intent rồi truy vấn lại cơ sở dữ liệu.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvAvatar, tvName, tvCategory, tvStatus, tvPrice, tvQuantity,
            tvWarning, tvStockValue, tvDescription;

    private DatabaseHelper dbHelper;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        tvAvatar = findViewById(R.id.tvAvatar);
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvStatus = findViewById(R.id.tvStatus);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvWarning = findViewById(R.id.tvWarning);
        tvStockValue = findViewById(R.id.tvStockValue);
        tvDescription = findViewById(R.id.tvDescription);
        Button btnImport = findViewById(R.id.btnImport);
        Button btnSell = findViewById(R.id.btnSell);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Nhận id từ Intent
        productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);
        if (!loadProduct()) {
            return;
        }

        // (5) Nhập thêm 1
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.updateQuantity(productId, product.getQuantity() + 1);
                loadProduct();
            }
        });

        // (6) Bán 1
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product.getQuantity() == 0) {
                    Toast.makeText(ProductDetailActivity.this,
                            R.string.msg_out_of_stock, Toast.LENGTH_SHORT).show();
                    return; // không giảm nữa
                }
                dbHelper.updateQuantity(productId, product.getQuantity() - 1);
                loadProduct();
            }
        });

        // (7) Quay lại
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // (8) Xóa sản phẩm (hỏi lại trước khi xóa)
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }

    /**
     * Truy vấn lại sản phẩm theo id và hiển thị lên giao diện.
     * Trả về false (và đóng màn hình) nếu không tìm thấy sản phẩm.
     */
    private boolean loadProduct() {
        product = dbHelper.getProductById(productId);
        if (product == null) {
            Toast.makeText(this, R.string.msg_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        tvAvatar.setText(product.getInitial());
        tvName.setText(product.getName());
        tvCategory.setText(product.getCategory());
        tvStatus.setText(product.isActive()
                ? R.string.status_active : R.string.status_inactive);
        tvPrice.setText(Product.formatMoney(product.getPrice()));
        tvQuantity.setText(String.valueOf(product.getQuantity()));
        tvStockValue.setText(Product.formatMoney(product.getStockValue()));

        // Cảnh báo nếu số lượng dưới 5
        tvWarning.setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);

        String description = product.getDescription();
        if (description == null || description.trim().isEmpty()) {
            tvDescription.setText(R.string.no_description);
        } else {
            tvDescription.setText(description);
        }
        return true;
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteProduct(productId);
                        Toast.makeText(ProductDetailActivity.this,
                                R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                        finish(); // về danh sách, onResume() cập nhật lại thống kê
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
