# Nhật Ký Thay Đổi (Changelog)

Toàn bộ các thay đổi, tính năng mới, cải tiến giao diện và sửa lỗi của dự án **Ứng dụng Ghi Chú Thông Minh & Cộng Đồng Ghi Chú - Thảo Luận** sẽ được ghi nhận chi tiết tại tệp tin này.

Định dạng nhật ký tuân thủ theo tiêu chuẩn [Keep a Changelog](https://keepachangelog.com/vi/1.0.0/) và phiên bản dự án tuân theo [Semantic Versioning](https://semver.org/).

---

## [1.2.0] - 2026-09-25

### ✨ Added (Thêm mới)
- **Mở rộng kho dữ liệu mẫu lên 140 mẫu (Du Lịch & Ẩm Thực Đường Phố)**:
  - Thêm danh mục mới **"Ẩm thực đường phố"** với các món ăn biểu tượng Việt Nam: *Phở Gánh Hà Nội, Bánh Mì Sài Gòn, Bún Bò Huế, Cơm Tấm Sài Gòn, Bánh Xèo Miền Tây, Cà Phê Trứng Hà Nội,...*
  - Cơ cấu tổng kho mẫu gồm 48 Ghi chú, 46 Bình luận và 46 Phản hồi.
  - Thêm thẻ bộ lọc riêng biệt cho chủ đề **"Ẩm thực đường phố"** với tông màu cam đặc trưng (`#FFF3E0` / `#E65100`).

### 🎨 Fixed & Improved Layout (Cải tiến & Tối ưu Bố Cục Giao Diện)
- **Tối ưu hóa Bố cục Hộp thoại Mẫu Dữ Liệu (`SampleDialog.kt`)**:
  - Gom khối bộ lọc (*Chủ đề* & *Loại mẫu*) vào một khung `Surface` bo góc 16.dp hiện đại.
  - Tích hợp dải chip cuộn ngang (`horizontalScroll`) giúp các nút bộ lọc hiển thị vừa vặn, không bị co kéo hay tràn lề trên mọi kích thước màn hình.
- **Tối ưu hóa Bố cục Hộp thoại Nhập/Xuất Đa Định Dạng (`MultiFilesImportExportDialog.kt`)**:
  - Tối ưu dải tùy chọn 8 định dạng xuất tệp (`JSON`, `XML`, `XLSX`, `HTML`, `CSV`, `DOCX`, `PPTX`, `TXT`) và dải dán văn bản nạp dữ liệu bằng dải thẻ cuộn ngang (`horizontalScroll`).
  - Cải thiện khoảng cách lề chuẩn Material 3, bo góc 12.dp và đường viền phân biệt định dạng rõ ràng.
  - Thêm hỗ trợ giải mã phân tích cú pháp mã nguồn XML trực tiếp khi dán vào ô nhập liệu.

### 📦 Release Artifacts
- Cập nhật tệp APK bản dựng thử nghiệm tại đường dẫn `apk-download/apk-debug.apk`.

---

## [1.1.0] - 2026-09-24

### ✨ Added (Thêm mới)
- **Hệ thống Nhập/Xuất Dữ Liệu 8 Định Dạng Toàn Diện**:
  - **Excel (.xlsx)**: Tạo tệp Bảng tính Microsoft Excel OpenXML với 3 Worksheets riêng biệt (Ghi chú, Bình luận gốc, Phản hồi lồng nhau).
  - **XML (.xml)**: Xuất và nạp lại cấu trúc cây dữ liệu thẻ XML chuẩn hóa (`<notes_backup>`, `<note>`, `<comments>`, `<comment>`, `<reply>`).
  - Bổ sung trình nạp dữ liệu tự động kiểm tra định dạng và chế độ dán mã nguồn trực tiếp.
- **Tạo Quy Trình Tự Động Biển Dịch GitHub Actions (`.github/workflows/android-build.yml`)**:
  - Tự động biên dịch APK và khởi tạo GitHub Release đính kèm `app-debug.apk` mỗi khi tạo thẻ `v*` trên repository.

---

## [1.0.0] - 2026-09-23

### 🚀 Release Đầu Tiên (Initial Release)
- **Quản lý Ghi chú Nâng cao**:
  - Soạn thảo văn bản đầy đủ với tính năng Hoàn tác/Làm lại (Undo & Redo), bộ đếm từ và ký tự thời gian thực.
  - Đánh dấu sao yêu thích (⭐ Favorite) và lọc nhanh các ghi chú yêu thích.
- **Hệ thống Bình luận & Thảo luận Đa tầng**:
  - Hỗ trợ bình luận gốc và các phản hồi lồng nhau nhiều cấp.
  - Cho phép ghim (📌) và gắn sao (⭐) các thảo luận chất lượng.
- **Gamification & Điểm Danh (Streak)**:
  - Tích lũy Kim Cương hàng ngày qua chuỗi điểm danh và hoàn thành mục tiêu.
  - Cửa hàng đổi gói nâng cấp dung lượng lưu trữ từ 8 GB đến 1.024 GB với thanh tiến trình trực quan.
  - Bảng xếp hạng thành viên năng nổ.
- **Xuất tệp PDF Native (SDK gốc Android)**:
  - Xuất danh sách ghi chú yêu thích ra tệp PDF chất lượng cao, phân trang tự động và hỗ trợ tiếng Việt có dấu hoàn hảo.
