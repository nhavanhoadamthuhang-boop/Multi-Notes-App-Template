# Nhật Ký Thay Đổi (Changelog)

Toàn bộ các thay đổi, tính năng mới, cải tiến giao diện và sửa lỗi của dự án **Ứng dụng Ghi Chú Đa Năng Thông Minh & Cộng Đồng Ghi Chú - Thảo Luận** sẽ được ghi nhận chi tiết tại tệp tin này.

Định dạng nhật ký tuân thủ theo tiêu chuẩn [Keep a Changelog](https://keepachangelog.com/vi/1.0.0/) và phiên bản dự án tuân theo [Semantic Versioning](https://semver.org/).

---

## [1.2.5] - 2026-09-25

### 🎨 Fixed Check-In Banner Layout & UI Proportions (Sửa Lỗi Bố Cục Thẻ Điểm Danh)
- **Tóm tắt & Khắc phục bố cục banner điểm danh (`DiamondTrackerComponents.kt`)**:
  - Sửa lỗi thẻ banner điểm danh bị kéo giãn chiều cao bất hợp lý, làm dẹt dải nút "Điểm danh ngay" thành vạch màu vàng dọc ở mép phải.
  - Áp dụng `wrapContentHeight()` và bộ căn chỉnh `Modifier.weight(1f)` cho phần văn bản, đảm bảo nút bấm *"Điểm danh ngay"* giữ nguyên tỷ lệ bo góc 8.dp chuẩn, không bị biến dạng hay giãn dòng.
  - Tối ưu khoảng cách căn lề (padding 12.dp horizontal / 8.dp vertical) và bổ sung icon ngôi sao `✪` nổi bật trên nền badge tròn màu vàng kim.

---

## [1.2.4] - 2026-09-25

### 🎙️ Voice Note Recorder Feature Documentation (Cập Nhật Hướng Dẫn Ghi Âm Ghi Chú)
- **Tóm tắt & Hoàn thiện tài liệu Ghi âm ghi chú (`VoiceRecordDialog.kt`)**:
  - Giao diện ghi âm hiện đại dạng Modal Bottom Sheet với nút Micro trung tâm, đồng hồ đếm thời gian thực (`00:03`), hướng dẫn chạm micro để ghi âm.
  - Khung xem và chỉnh sửa bản dịch giọng nói thời gian thực ("Bản dịch giọng nói - Có thể chỉnh sửa") với định dạng tiêu đề theo mốc thời gian (ví dụ: `Ghi chú giọng nói lúc 15:11`).
  - Lựa chọn nhanh mẫu bản dịch kiểm thử (Quick testing presets) gồm: *"Họp phòng ban sáng thứ hai"* và *"Mua sữa, táo và bánh mì"*.
- **Cập nhật tài liệu dự án (`README.md` & `CHANGELOG.md`)**:
  - Bổ sung mục tính năng Ghi Âm Ghi Chú & Dịch Giọng Nói Trực Tiếp vào hệ thống tính năng nổi bật trong `README.md`.

---

## [1.2.3] - 2026-09-25

### 🇻🇳 Integrated Vietnamese Leaders Biographies (Tích Hợp Tiểu Sử Cố TBT Nguyễn Phú Trọng & TBT Tô Lâm)
- **Bổ sung tư liệu lịch sử về Cố TBT Nguyễn Phú Trọng & TBT Tô Lâm**:
  - **Cố Tổng Bí thư Nguyễn Phú Trọng**: Tóm tắt hành trình lãnh đạo kiên trung, tấm gương đạo đức cách mạng liêm chính, công cuộc chỉnh đốn Đảng "không có vùng cấm" và trường phái *"Ngoại giao cây tre Việt Nam"*.
  - **Tổng Bí thư Tô Lâm**: Tóm tắt bước đột phá chiến lược Đề án 06 về chuyển đổi số quốc gia, tinh gọn bộ máy và tầm nhìn *"Kỷ nguyên vươn mình của dân tộc"*.
- **Cập nhật tổng số mẫu tài nguyên lên 162 mẫu**:
  - Tự động hiển thị chính xác tổng số lượng mẫu và kết quả lọc thời gian thực trong giao diện `SampleDialog`.

---

## [1.2.2] - 2026-09-25

### ✨ Expanded Sample Data to 160 Items (Nâng Kho Mẫu Lên 160 Mẫu)
- **Mở rộng kho dữ liệu từ 144 mẫu lên 160 mẫu phong phú**:
  - **Ẩm thực đường phố Việt Nam**: Tích hợp các món ăn đặc sản miền Trung gồm *Mì Quảng Quảng Nam* và *Cao Lầu Hội An*.
  - **Chủ đề mới "Tiểu sử lãnh đạo Việt Nam"**: Thêm danh mục tư liệu lịch sử về các vị lãnh đạo tiền bối kiệt xuất (*Chủ tịch Hồ Chí Minh, Đại tướng Võ Nguyên Giáp, Cố Tổng Bí thư Nguyễn Văn Linh, Cố Thủ tướng Phạm Văn Đồng, Cố Tổng Bí thư Lê Duẩn*).
  - Thêm thẻ bộ lọc riêng biệt cho danh mục *"Tiểu sử lãnh đạo Việt Nam"* với tông màu đỏ trang trọng (`#FFEBEE` / `#C62828`).
  - Cơ cấu kho mẫu chuẩn gồm 55 Ghi chú mẫu, 53 Bình luận mẫu và 52 Phản hồi mẫu.

---

## [1.2.1] - 2026-09-25

### 🏷️ Category Renaming & Data Refinement (Đổi Tên Danh Mục & Chuẩn Hóa Mẫu Dữ Liệu)
- **Đổi tên danh mục "Ẩm thực đường phố" thành "Ẩm thực đường phố Việt Nam"**:
  - Cập nhật đồng bộ và nhất quán tên danh mục, nhãn bộ lọc `FilterChip`, logic lọc mẫu `matchesCategory` cũng như thẻ màu nhận diện trên các giao diện `SampleNoteCard`, `SampleCommentCard` và `SampleReplyCard` trong `SampleDialog.kt`.
- **Mở rộng kho dữ liệu mẫu lên 144 mẫu toàn diện**:
  - Cấu trúc lại kho mẫu gồm 48 Ghi chú, 48 Bình luận và 48 Phản hồi mẫu trải dài qua 3 chủ đề lớn: *Du lịch Việt Nam*, *Du lịch nước Nga* và *Ẩm thực đường phố Việt Nam*.

### 🛠️ Build & Environment Maintenance (Khắc Phục Biên Dịch & Môi Trường)
- **Tạo tệp cấu hình môi trường**:
  - Bổ sung `/.env` và `/.env.example` tương thích với cấu hình Secrets Gradle Plugin.
- **Sửa lỗi cú pháp & đồng bộ mã nguồn**:
  - Khắc phục lỗi đóng ngoặc kép chuỗi ký tự trong `SampleDialog.kt` và cập nhật hàm khởi tạo mẫu `generate144Samples()`.

### 📦 Release Artifacts (Tệp Bản Dựng)
- Biên dịch và cập nhật tệp APK thử nghiệm mới nhất (`33.8 MB`) tại đường dẫn `apk-download/apk-debug.apk`.

---

## [1.2.0] - 2026-09-25

### ✨ Added (Thêm mới)
- **Mở rộng kho dữ liệu mẫu lên 144 mẫu (Du Lịch & Ẩm Thực Đường Phố)**:
  - Thêm danh mục mới **"Ẩm thực đường phố"** với các món ăn biểu tượng Việt Nam: *Phở Gánh Hà Nội, Bánh Mì Sài Gòn, Bún Bò Huế, Cơm Tấm Sài Gòn, Bánh Xèo Miền Tây, Cà Phê Trứng Hà Nội,...*
  - Cơ cấu tổng kho mẫu gồm 48 Ghi chú, 48 Bình luận và 48 Phản hồi.
  - Thêm thẻ bộ lọc riêng biệt cho chủ đề **"Ẩm thực đường phố"** với tông màu cam đặc trưng (`#FFF3E0` / `#E65100`).

### 🎨 Fixed & Improved Layout (Cải tiến & Tối ưu Bố Cục Giao Diện)
- **Tối Ưu Hóa Thẻ Số Dư Cửa Hàng (`DiamondTrackerComponents.kt`)**:
  - Chuyển đổi nhãn hiển thị số dư từ `"kim cương"` sang ký hiệu `"💎"` kết hợp ràng buộc `weight(1f, fill = false)` và `maxLines = 1`, loại bỏ hoàn toàn hiện tượng ngắt dòng thô của nút *"Lịch sử"* và tiêu đề gói trên thẻ số dư.
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
