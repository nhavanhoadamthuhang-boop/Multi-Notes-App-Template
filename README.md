# Ứng dụng Ghi Chú Thông Minh & Cộng Đồng Ghi Chú - Thảo Luận (Smart Notes & Community App)

## 📖 Tổng Quan Dự Án
**Ứng dụng Ghi Chú Thông Minh & Cộng Đồng** là một ứng dụng di động hiện đại được xây dựng hoàn toàn bằng **Kotlin** và **Jetpack Compose**, tuân thủ nghiêm ngặt các tiêu chuẩn **Material Design 3**. Ứng dụng kết hợp hoàn hảo giữa công cụ quản lý ghi chú cá nhân mạnh mẽ và hệ thống tương tác cộng đồng sinh động, tích hợp cơ chế phần thưởng kim cương, điểm danh chuỗi ngày liên tục (Streak), thanh tiến trình theo dõi dung lượng lưu trữ thực tế và bảng xếp hạng thành viên tích cực.

Đặc biệt, ứng dụng được thiết kế tối ưu hóa trải nghiệm người dùng với phông chữ **Inter** hiện đại, hệ thống hành động nhanh thông minh và kho lưu trữ 128 mẫu ghi chú, bình luận và phản hồi phong phú.

---

## 🌟 Các Tính Năng Nổi Bật

### 1. Quản Lý Ghi Chú Toàn Diện & Nâng Cao (Undo/Redo & Rich Fields)
- **Tạo & Chỉnh Sửa Ghi Chú**: Hỗ trợ tiêu đề, nội dung chi tiết, nhãn phân loại (Labels) tùy chỉnh sinh động và trạng thái ghim (Pinned) lên đầu danh sách.
- **Tính năng Hoàn tác / Làm lại (Undo & Redo)**: Tích hợp công cụ khôi phục thay đổi thông minh trong trình soạn thảo ghi chú, giúp dễ dàng đảo ngược hoặc áp dụng lại các chỉnh sửa văn bản gần nhất một cách an toàn.
- **Bộ đếm Từ & Ký tự Động**: Hiển thị thời gian thực (Real-time) số lượng từ và số lượng ký tự trực quan ngay dưới ô nhập mô tả trong trình soạn thảo, giúp người dùng dễ dàng theo dõi và kiểm soát dung lượng văn bản của mình.
- **Yêu thích bằng Ngôi sao Vàng (⭐ Favorite/Star Feature)**: Đánh dấu các ghi chú quan trọng nhất của bạn bằng ngôi sao vàng hoàng kim sang trọng. Đồng thời, hỗ trợ lọc nhanh toàn bộ các ghi chú đã gắn sao chỉ với một chạm từ thanh công cụ chính.

### 2. Hệ Thống Bình Luận & Thảo Luận Tương Tác Đa Tầng
- **Thảo Luận Đa Tầng**: Mỗi ghi chú đều hỗ trợ hệ thống bình luận sâu nhiều cấp (bình luận chính và các phản hồi - replies) tạo nên không gian trao đổi sống động.
- **Yêu thích & Ghim Thảo Luận**: Cho phép gắn sao yêu thích (⭐) hoặc ghim (📌) các bình luận/phản hồi đắt giá lên đầu danh sách để cộng đồng tiện theo dõi.
- **Giao Diện Trực Quan**: Huy hiệu phân loại vàng sữa **⭐ YÊU THÍCH** và **⭐ SAO** được thiết kế nổi bật, chuyên nghiệp.

### 3. Kho 128 Mẫu Ghi Chú, Bình Luận & Phản Hồi
- **Bộ dữ liệu mẫu đa dạng**: Gồm 42 ghi chú, 43 bình luận và 43 phản hồi mẫu (Tổng cộng 128 mẫu) được soạn thảo cực kỳ chi tiết, phong phú về nhiều chủ đề và địa danh nổi tiếng.
  - **Du lịch Việt Nam**: Khám phá danh lam thắng cảnh nổi tiếng như Sapa, Hà Giang, Vịnh Hạ Long, Hội An, Phú Quốc,... đặc biệt là thông tin chi tiết về **Khu du lịch sinh thái Suối Nặm Thoong (Cao Bằng)** hoang sơ thơ mộng và **Khu di tích lịch sử anh hùng Kim Đồng (Cao Bằng)** linh thiêng hào hùng.
  - **Du lịch nước Nga**: Chiêm ngưỡng kiến trúc kiệt tác như Điện Kremlin (Moscow), Cung điện Mùa đông (Saint Petersburg), Hồ Baikal (Siberia), săn cực quang kỳ vĩ tại Murmansk,...
- **Công cụ tìm kiếm & Lọc thông minh**: Tìm kiếm thời gian thực kết hợp bộ lọc kép theo chủ đề và loại mẫu dữ liệu (Ghi chú, Bình luận, Phản hồi).
- **Nhập nhanh một chạm (Quick Import)**: Nhập lập tức bất kỳ ghi chú mẫu nào vào kho dữ liệu chính thức của bạn, tự động tạo nhãn dán tương ứng và nhận phần thưởng **+20 Kim Cương thưởng** tức thì!

### 4. Gamification: Điểm Danh (Streak), Phần Thưởng Kim Cương & Quản Lý Dung Lượng
- **Chuỗi điểm danh liên tục (Streak)**: Khích lệ người dùng duy trì thói quen viết ghi chú hàng ngày để duy trì chuỗi Streak và nhận thưởng kim cương tăng dần mỗi ngày.
- **Cơ chế tích lũy Kim cương**: Nhận thưởng khi hoàn thành mục tiêu, tạo ghi chú mới hoặc đóng góp tương tác tích cực.
- **Cửa Hàng Kim Cương & Thanh Tiến Trình Dung Lượng**:
  - Đổi các gói nâng cấp giới hạn từ **Gói Mặc Định (8 GB)** đến **Gói Cấp 8 VIP (1.024 GB)** bằng kim cương tích lũy.
  - Tích hợp **Thanh tiến trình dung lượng lưu trữ (Storage Capacity Progress Bar)** hiển thị mức dung lượng đã dùng thực tế/ước tính so với hạn mức gói hiện tại một cách trực quan.
- **Bảng Xếp Hạng (Leaderboard)**: Vinh danh các thành viên hoạt động năng nổ nhất trên bảng vàng danh giá.

### 5. Xuất các Ghi Chú Yêu thích ra tệp PDF (Sử dụng SDK gốc Android)
- **Hệ thống Xuất PDF nâng cao**: Tích hợp công cụ xuất toàn bộ danh sách ghi chú yêu thích (được gắn sao ⭐) ra một tệp PDF chuyên nghiệp nhiều trang sử dụng SDK native `PdfDocument` tối giản và hiệu quả.
- **Tính toán thời gian tương đối**: Hiển thị chi tiết thời gian đã trôi qua kể từ lúc tạo cho Ghi chú, Bình luận và Phản hồi (ví dụ: `X năm Y tháng Z tuần W ngày H giờ M phút S giây trước`), giúp tăng tính trực quan cho dòng sự kiện (Timeline).
- **Hỗ trợ Unicode tiếng Việt hoàn hảo**: Do sử dụng cơ chế Canvas & Paint gốc của Android, tệp tin PDF hỗ trợ đầy đủ các ký tự tiếng Việt có dấu với độ nét cao, loại bỏ hoàn toàn việc phải lọc bỏ dấu hay lỗi ký tự của các thư viện bên thứ ba.
- **Phân trang tự động thông minh**: Tự động hóa việc phân chia trang (Page-breaking), vẽ đường biên phân cách giữa các ghi chú bằng nét đứt thanh mảnh tinh xảo.

### 6. Hệ Thống Nhập / Xuất Dữ Liệu Đa Định Dạng (Multi-Format Import/Export)
- **Hỗ trợ 6 định dạng mạnh mẽ**: Cho phép người dùng xuất toàn bộ hoặc đơn lẻ các ghi chú và các thảo luận phân cấp đi kèm ra các định dạng tệp tin thông dụng nhất:
  - **JSON**: Dạng dữ liệu tiêu chuẩn hoàn hảo để sao lưu và khôi phục nguyên vẹn cấu trúc thông tin.
  - **HTML**: Thiết kế trang web tĩnh trực quan với CSS tối giản hiện đại, giúp dễ dàng mở xem và đọc dữ liệu trên mọi trình duyệt của máy tính hay điện thoại.
  - **CSV**: Định dạng bảng tính tiêu chuẩn, phân chia rõ ràng các thuộc tính của ghi chú, bình luận và phản hồi giúp dễ dàng nạp vào Microsoft Excel hoặc Google Sheets.
  - **Word (DOCX)**: Tạo tệp tài liệu Open XML (.docx) chuyên nghiệp, mở trực tiếp bằng MS Word hoặc Google Docs mà không cần tích hợp các thư viện bên thứ ba nặng nề.
  - **PowerPoint (PPTX)**: Tạo bài thuyết trình PowerPoint (.pptx) chuẩn Open XML, mỗi ghi chú được chuyển thành một slide với bố cục tiêu đề và nội dung phân bố hài hòa, dễ dàng mở trực tiếp bằng PowerPoint, Google Slides hay WPS Office.
  - **TXT (Văn bản thuần túy)**: Tệp văn bản siêu nhẹ, dễ lưu trữ và xem nhanh trên mọi nền tảng.
- **Trình nạp dữ liệu thông minh (Smart Importer)**:
  - **Tự động nhận diện định dạng**: Khi người dùng tải lên một tệp tin từ thiết bị, ứng dụng tự động kiểm tra phần mở rộng (`.json`, `.html`, `.csv`, `.docx`, `.pptx`, `.txt`) và áp dụng bộ giải mã phù hợp tương ứng.
  - **Dán văn bản trực tiếp (Paste Raw Content)**: Cho phép dán trực tiếp mã nguồn dữ liệu (JSON, HTML, CSV, TXT) vào ô nhập liệu để nạp nhanh mà không cần tạo tệp.
  - **Báo cáo kiểm tra trước khi nạp**: Phân tích cú pháp văn bản thời gian thực để báo cáo số lượng ghi chú, bình luận phát hiện được trước khi tiến hành cập nhật vào cơ sở dữ liệu.
- **Cơ chế ghi đè an toàn**: Hỗ trợ hai chế độ nhập linh hoạt: "Nhập thêm (Giữ nguyên dữ liệu cũ)" hoặc "Ghi đè hoàn toàn (Thay thế toàn bộ kho dữ liệu)" giúp người dùng làm chủ dữ liệu của mình.

### 7. Thiết Kế Đẹp Mắt & Bố Cục Thích Ứng (Adaptive Layout)
- **Kiến trúc thích ứng 4 chuẩn màn hình**: Tự động nhận diện không gian để chuyển đổi hiển thị tối ưu từ Điện thoại (Single-pane), Máy tính bảng (Master-Detail chia đôi màn hình), Laptop (Tối ưu biên lề) đến Desktop (3 cột song song chuyên nghiệp).
- **Phông chữ Inter tinh tế**: Toàn bộ hệ thống được hiển thị bằng phông chữ Inter hiện đại, bo tròn các góc thẻ tinh tế, kết hợp chuyển động Material Ripple mềm mại mang lại cảm giác sang trọng.

---

## 🛠️ Kiến Trúc & Công Nghệ Kỹ Thuật

- **Ngôn ngữ lập trình**: 100% Kotlin hiện đại, an toàn kiểu dữ liệu.
- **Giao diện người dùng**: Jetpack Compose với Material Design 3, phông chữ Inter thanh lịch, hỗ trợ Dark/Light Theme tự động và Dynamic Color (Android 12+).
- **Kiến trúc phần mềm**: MVVM (Model-View-ViewModel) kết hợp Unidirectional Data Flow (UDF).
- **Quản lý trạng thái & Bất đồng bộ**: Kotlin Coroutines & `StateFlow` / `SharedFlow`.
- **Cơ sở dữ liệu cục bộ**: Room Database với KSP (Kotlin Symbol Processing).
- **Hệ thống hướng dẫn**: Hướng dẫn 8 bước trực quan sinh động dành cho người dùng mới (`UserGuideDialog`).

---

## 🚀 Hướng Dẫn Sử Dụng & Trải Nghiệm
1. **Khởi động ứng dụng**: Thưởng thức ngay giao diện trực quan với phông chữ Inter sắc nét.
2. **Khám phá 128 mẫu ghi chú, bình luận và phản hồi**: Mở Drawer bên trái, chọn **"128 mẫu ghi chú, bình luận và phản hồi"** (`SampleDialog`) để dạo chơi qua các mẫu dữ liệu phong phú, thử tìm kiếm **"Suối Nặm Thoong"** hoặc **"Kim Đồng"** rồi nhấn **"Nhập nhanh"** để nhận quà kim cương.
3. **Sử dụng Undo/Redo**: Khi viết hay chỉnh sửa ghi chú, hãy trải nghiệm 2 nút Hoàn tác/Làm lại ở góc trên trình soạn thảo để dễ dàng quản lý nội dung.
4. **Đánh dấu Yêu thích**: Nhấn nút Ngôi sao (⭐) trên bất kỳ ghi chú hay bình luận nào để gắn sao vàng nổi bật và quản lý dễ dàng.
5. **Theo dõi Dung lượng & Đổi quà**: Mở **"Cửa hàng Kim Cương"** để xem thanh tiến trình dung lượng sử dụng và nâng cấp cấp độ tài khoản bằng Kim Cương tích lũy.
6. **Nhập/Xuất đa dạng**: Mở Menu ứng dụng, chọn **"Nhập/Xuất Dữ Liệu"** để xuất bản ghi chú ra PPTX, DOCX, HTML, CSV, JSON hoặc văn bản thuần túy.
