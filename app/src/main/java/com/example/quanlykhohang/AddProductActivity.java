package com.example.quanlykhohang;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Màn hình 3: Thêm sản phẩm mới.
 */
public class AddProductActivity extends AppCompatActivity {

    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Spinner spnCategory;   // dữ liệu lấy từ @array/categories (android:entries)
    private Switch swActive;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Hiện mũi tên quay lại trên thanh tiêu đề
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        edtName = findViewById(R.id.edtName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtDescription = findViewById(R.id.edtDescription);
        spnCategory = findViewById(R.id.spnCategory);
        swActive = findViewById(R.id.swActive);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String quantityText = edtQuantity.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        // 1. Tên không được để trống
        if (name.isEmpty()) {
            edtName.setError(getString(R.string.err_name));
            edtName.requestFocus();
            return;
        }

        // 2. Giá bán: phải là số và lớn hơn 0
        long price;
        try {
            price = Long.parseLong(priceText);
        } catch (NumberFormatException e) {
            price = -1; // rỗng hoặc không phải số
        }
        if (price <= 0) {
            edtPrice.setError(getString(R.string.err_price));
            edtPrice.requestFocus();
            return;
        }

        // 3. Số lượng: phải là số nguyên không âm
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            quantity = -1;
        }
        if (quantity < 0) {
            edtQuantity.setError(getString(R.string.err_quantity));
            edtQuantity.requestFocus();
            return;
        }

        String category = spnCategory.getSelectedItem().toString();
        boolean active = swActive.isChecked();

        // 4. Lưu vào cơ sở dữ liệu
        Product product = new Product(name, price, quantity, category, active, description);
        long newId = dbHelper.insertProduct(product);

        if (newId != -1) {
            Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
            finish(); // về MainActivity, onResume() sẽ tải lại danh sách và thống kê
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** Nhấn mũi tên trên thanh tiêu đề -> đóng màn hình. */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
