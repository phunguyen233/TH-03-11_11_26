package com.example.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Lớp quản lý cơ sở dữ liệu SQLite warehouse.db (phiên bản 1).
 * Gồm 2 bảng: users (tài khoản đăng nhập) và products (sản phẩm).
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    // Bảng products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_PRICE = "price";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_CATEGORY = "category";
    public static final String COL_ACTIVE = "active";
    public static final String COL_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Chỉ chạy MỘT lần khi cơ sở dữ liệu chưa tồn tại: tạo bảng + chèn dữ liệu mẫu. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_PRICE + " INTEGER NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_CATEGORY + " TEXT NOT NULL, "
                + COL_ACTIVE + " INTEGER NOT NULL, "
                + COL_DESCRIPTION + " TEXT)");

        // Tài khoản mẫu
        insertUser(db, "admin", "123456");

        // 3 sản phẩm mẫu (active = 1)
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true,
                "Chuột không dây kết nối USB 2.4GHz"));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true,
                "Bàn phím cơ switch xanh, có đèn nền"));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true,
                "Sổ tay A5 bìa cứng, 200 trang"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // =====================================================================
    // BẢNG USERS
    // =====================================================================

    /** Trả về true nếu có dòng khớp username + password trong bảng users. */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                        + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password});
        boolean found = cursor.moveToFirst();
        cursor.close();
        return found;
    }

    /** Thêm tài khoản mới vào bảng users. Trả về id vừa thêm, -1 nếu lỗi (trùng tên). */
    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    // Bản dùng trong onCreate(): phải dùng đúng đối tượng db được truyền vào,
    // KHÔNG gọi getWritableDatabase() trong onCreate() (sẽ gây lỗi gọi đệ quy).
    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // =====================================================================
    // BẢNG PRODUCTS
    // =====================================================================

    /** Thêm sản phẩm, trả về id vừa thêm (-1 nếu lỗi). */
    public long insertProduct(Product p) {
        return insertProduct(getWritableDatabase(), p);
    }

    private long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        values.put(COL_PRICE, p.getPrice());
        values.put(COL_QUANTITY, p.getQuantity());
        values.put(COL_CATEGORY, p.getCategory());
        values.put(COL_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COL_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /** Lấy toàn bộ sản phẩm để hiển thị danh sách (danh sách rỗng nếu chưa có sản phẩm). */
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_ID + " ASC", null);
        while (cursor.moveToNext()) {
            list.add(readProduct(cursor));
        }
        cursor.close();
        return list;
    }

    /** Lấy một sản phẩm theo id; trả về null nếu không tìm thấy. */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        Product product = null;
        if (cursor.moveToFirst()) {
            product = readProduct(cursor);
        }
        cursor.close();
        return product;
    }

    /** Cập nhật số lượng (dùng cho Nhập thêm / Bán). Trả về số dòng bị ảnh hưởng. */
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /** Xóa sản phẩm theo id. Trả về số dòng đã xóa. */
    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /** Tổng giá trị kho: SUM(price * quantity). Trả về 0 nếu kho rỗng. */
    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_PRODUCTS,
                null);
        long total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {   // SUM trả về NULL khi bảng rỗng
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    /** Số sản phẩm sắp hết hàng: COUNT với quantity < 5. */
    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_QUANTITY + " < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_LIMIT)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /** Chuyển dòng hiện tại của Cursor thành đối tượng Product. */
    private Product readProduct(Cursor cursor) {
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ACTIVE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
    }
}
