# Quản lý kho hàng – Hướng dẫn chạy

Ngôn ngữ: Java + XML. Lưu trữ: SQLite qua SQLiteOpenHelper (warehouse.db, phiên bản 1).
Package: `com.example.quanlykhohang`. Tài khoản mẫu: **admin / 123456**.

## Cách 1 – Mở thẳng project

1. Giải nén, mở Android Studio → **File > Open** → chọn thư mục `QuanLyKhoHang`.
2. Chờ Gradle Sync xong (lần đầu cần mạng để tải Gradle 8.7 và thư viện).
   Nếu Android Studio đề nghị nâng cấp Android Gradle Plugin thì cứ đồng ý.
3. Chọn máy ảo hoặc điện thoại thật rồi nhấn **Run**.

## Cách 2 – Chép vào project mới (dùng khi Cách 1 báo lỗi Gradle)

1. **New Project > Empty Views Activity**, Language = **Java**,
   Package name = `com.example.quanlykhohang`, Minimum SDK = API 24.
2. Chép đè 7 file trong `app/src/main/java/com/example/quanlykhohang/` vào thư mục tương ứng.
3. Chép đè các thư mục `res/layout`, `res/drawable`, `res/values` (colors, strings, themes).
   Xóa `res/values-night/themes.xml` và `activity_main.xml` cũ nếu Android Studio báo trùng.
4. Chép đè `AndroidManifest.xml`.
5. **Build > Rebuild Project**, rồi **Run**.

## Cấu trúc mã nguồn

| File | Vai trò |
|---|---|
| `Product.java` | Lớp sản phẩm: tên, giá, số lượng, loại, trạng thái, mô tả |
| `DatabaseHelper.java` | Tạo bảng users/products, chèn dữ liệu mẫu, toàn bộ truy vấn |
| `LoginActivity.java` | Màn hình 1 – đăng nhập, gọi `checkLogin()` |
| `MainActivity.java` | Màn hình 2 – thống kê + danh sách, tải lại trong `onResume()` |
| `ProductAdapter.java` | Đưa danh sách sản phẩm lên ListView |
| `AddProductActivity.java` | Màn hình 3 – kiểm tra dữ liệu và `insertProduct()` |
| `ProductDetailActivity.java` | Màn hình 4 – nhận id, nhập thêm / bán / xóa |

## Kịch bản tự kiểm tra trước khi nộp

1. Đăng nhập bỏ trống → báo lỗi; nhập sai → "Sai tên đăng nhập hoặc mật khẩu"; admin/123456 → vào danh sách; nhấn Back → thoát app, không quay lại màn hình đăng nhập.
2. Danh sách ban đầu: 3 sản phẩm, giá trị kho 5.350.000 đ, 1 sản phẩm sắp hết hàng (Bàn phím cơ, SL màu đỏ).
3. Thêm sản phẩm: bỏ trống tên / giá = 0 / bỏ trống số lượng → báo lỗi; nhập đúng → về danh sách, thống kê thay đổi.
4. Chi tiết: Nhập thêm 1, Bán 1 → số lượng và giá trị tồn kho đổi ngay; bán đến 0 → Toast "Hết hàng".
5. Xóa sản phẩm → về danh sách, thống kê cập nhật.
6. Tắt hẳn app (vuốt khỏi Recents) rồi mở lại → dữ liệu vẫn còn.

Lưu ý: dữ liệu mẫu chỉ được chèn khi tạo cơ sở dữ liệu lần đầu. Muốn trở về dữ liệu ban đầu thì gỡ app hoặc vào Settings > Apps > Clear storage.
