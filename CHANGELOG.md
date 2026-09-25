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
- **Tối Ưu Hóa Bố Cục Bảng Xếp Hạng (`LeaderboardDialog.kt`)**:
  - Giới hạn `maxLines = 1` và bổ sung ràng buộc co giãn trọng số (`weight(1f, fill = false)`) cho thẻ thứ hạng người dùng (`myUserRank`) và các hàng danh sách thành viên (`LeaderboardUserRow`), ngăn chặn hoàn toàn hiện tượng ngắt dòng thô (như tách số lượng `2.400` và `cmt` thành 2 dòng riêng biệt).
- **Tối Ưu Hóa Nút Kích Hoạt Gói Mua (`StorePackageCard`)**:
  - Giới hạn kích thước chữ `11.sp` và `maxLines = 1` cho các nút kích hoạt / mua gói VIP trong Cửa Hàng, loại bỏ hoàn toàn hiện tượng ngắt dòng hoặc tràn lề ký tự trên nền đỏ.
- **Tối Ưu Hóa Bố Cục Thẻ Điểm Danh (`DailyCheckInCard`)**:
  - Chuyển đổi cấu trúc thẻ Điểm danh Mỗi Ngày từ hàng ngang sang dạng cột đứng linh hoạt (`Column`), giúp biểu tượng, tiêu đề, nút bấm và nội dung chi tiết hiển thị gọn gàng, không bị co kéo hay ép dọc ký tự trên màn hình hẹp.
- **Tối ưu hóa Nút Chọn Tệp Tin (`MultiFilesImportExportDialog.kt`)**:
  - Cải tiến và rút gọn nhãn nút chọn tệp tin nhập dữ liệu (`Chọn tệp tin dữ liệu (JSON, HTML, CSV, TXT)`) với kích thước chữ `12.sp` và `maxLines = 1`, loại bỏ hoàn toàn hiện tượng cụt chữ/tràn lề khi hiển thị trên các dòng thiết bị di động.
- **Tối Ưu Hóa Hiển Thị Top App Bar & Huy Hiệu**:
  - Tối ưu hóa `DiamondTopBarBadge` trên thanh tiêu đề bằng cách sử dụng ký hiệu `💎` thay cho chữ "kim cương", loại bỏ hoàn toàn hiện tượng chồng lấn giao diện với huy hiệu chuỗi điểm danh (`StreakBadge`).
  - Rút gọn và chuẩn hóa nhãn mô tả gói dịch vụ (`badgeLabel`) thành các thông số ngắn gọn, súc tích (ví dụ: *"VIP Cấp 8 (32 năm • 1.024 GB)"*) giúp tránh hiện tượng tràn lề văn bản trong hộp thoại.
- **Thiết Kế Lại Bố Cục Tổng Quan Phức Tạp (`DiamondGoalDialog`)**:
  - Xây dựng lại bảng điều khiển tổng quan (Dashboard Overview) với thẻ trung tâm hiển thị tổng số Kim Cương, tiến độ % mục tiêu và thanh tiến trình `LinearProgressIndicator` phát sáng.
  - Tích hợp thẻ chi tiết gói VIP hiện tại kèm theo hạn mức trực quan (*Bình luận/ngày, Tốc độ cmt/phút, Giới hạn ghi chú, Thùng rác*).
  - Tích hợp thẻ quy đổi dữ liệu đóng góp (*20 Kim Cương/Mục*) và nút chuyển nhanh đến cửa hàng.
- **Thiết Kế Lại Bố Cục Nạp Kim Cương Nhanh Phức Tạp (`DiamondStoreDialog`)**:
  - Thay thế các hàng nút bấm đơn điệu bằng **Bảng lưới bề mặt cao cấp (Rich Surface Grid 2x4)** cho các gói nạp nhanh từ `+8k` đến `+1.024k` kim cương.
  - Mỗi thẻ nạp có biểu tượng Kim Cương vàng, nhãn mệnh giá đậm nét và phụ chú cấp độ rõ ràng (*Khởi động, Phổ biến, Chuyên nghiệp, VIP Pro, Tối thượng*).
- **Thiết Kế Lại Bố Cục Bảng Xếp Hạng (`LeaderboardDialog.kt`)**:
  - Tích hợp **Khối Bục Bảng Vàng (Top 3 Podium View)** dành cho Top 1 (Vàng - Trung tâm), Top 2 (Bạc - Trái) và Top 3 (Đồng - Phải) với biểu tượng cúp vinh danh, tên thành viên và số lượng Kim Cương trực quan.
  - Tối ưu hóa Thẻ thứ hạng cá nhân (`#Rank Badge`) rực rỡ kèm nút "Tăng hạng" mở nhanh Cửa hàng.
  - Sắp xếp danh sách thứ hạng thành viên (#4 trở đi) với đường viền bo góc mượt mà và thông số tương tác đầy đủ (*Ghi chú, Cmt, Streak*).
- **Thiết Kế Lại Bố Cục Cửa Hàng Gói Mua (`DiamondTrackerComponents.kt` - `StorePackageCard`)**:
  - Chuyển đổi khối thông số gói mua thành **Bảng Lưới 2 Cột (2-column Feature Grid)** hiển thị rõ ràng từng hạn mức (*Bình luận/ngày, Ghi chú/ngày, Tốc độ/phút, Thùng rác retention, Dung lượng GB*).
  - Thêm thanh tiến trình dung lượng lưu trữ (`LinearProgressIndicator`) sắc nét.
  - Tối ưu hóa Nút Kích Hoạt Gói Mua full-width với nhãn trạng thái *"Kích hoạt gói"* hoặc *"Cần thêm X 💎"*.
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
