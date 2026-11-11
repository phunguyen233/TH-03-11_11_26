package com.example.quanlykhohang;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp biểu diễn một sản phẩm trong kho (tương ứng một dòng của bảng products).
 */
public class Product {

    /** Số lượng dưới ngưỡng này thì coi là "Sắp hết hàng". */
    public static final int LOW_STOCK_LIMIT = 5;

    private int id;
    private String name;
    private long price;        // giá bán (VNĐ)
    private int quantity;      // số lượng tồn
    private String category;   // loại sản phẩm
    private boolean active;    // true: đang kinh doanh
    private String description;

    public Product() {
    }

    /** Dùng khi tạo sản phẩm mới (chưa có id). */
    public Product(String name, long price, int quantity, String category,
                   boolean active, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    /** Dùng khi đọc sản phẩm từ cơ sở dữ liệu (đã có id). */
    public Product(int id, String name, long price, int quantity, String category,
                   boolean active, String description) {
        this(name, price, quantity, category, active, description);
        this.id = id;
    }

    // ----- Các phương thức nghiệp vụ -----

    /** Sản phẩm sắp hết hàng khi số lượng < 5. */
    public boolean isLowStock() {
        return quantity < LOW_STOCK_LIMIT;
    }

    /** Giá trị tồn kho = giá bán × số lượng. */
    public long getStockValue() {
        return price * quantity;
    }

    /** Chữ cái đầu của tên (in hoa), dùng cho ô ảnh đại diện. */
    public String getInitial() {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.trim().substring(0, 1).toUpperCase(new Locale("vi", "VN"));
    }

    /** Định dạng tiền có dấu phân cách hàng nghìn, ví dụ 450000 -> "450.000 đ". */
    public static String formatMoney(long amount) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format(amount) + " đ";
    }

    // ----- Getter / Setter -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
