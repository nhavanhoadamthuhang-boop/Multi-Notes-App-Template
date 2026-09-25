package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.NoteEntity
import com.example.data.local.CommentEntity

sealed class SampleItem {
    data class Note(
        val id: Int,
        val title: String,
        val content: String,
        val category: String,
        val tags: String
    ) : SampleItem()

    data class Comment(
        val id: Int,
        val author: String,
        val content: String,
        val noteTitle: String,
        val category: String
    ) : SampleItem()

    data class Reply(
        val id: Int,
        val author: String,
        val content: String,
        val parentCommentContent: String,
        val category: String
    ) : SampleItem()
}

object SamplesProvider {
    fun generate160Samples(): List<SampleItem> {
        val list = mutableListOf<SampleItem>()

        // 1. Generate 55 Notes (22 Du lịch VN, 20 Du lịch Nga, 8 Ẩm thực đường phố, 5 Tiểu sử lãnh đạo)
        val vnNotes = listOf(
            Triple("Hành trình khám phá Hà Giang mộng mơ", "Khám phá dốc Thẩm Mã, cột cờ Lũng Cú và dòng sông Nho Quế xanh biếc thơ mộng. Thời điểm lý tưởng nhất là mùa hoa tam giác mạch từ tháng 10 đến tháng 12.", "Hà Giang, Tây Bắc"),
            Triple("Review chi tiết Sapa 3 ngày 2 đêm", "Chinh phục đỉnh Fansipan - nóc nhà Đông Dương bằng cáp treo, check-in bản Cát Cát của người H’Mông, thưởng thức đồ nướng Sapa thơm phức trong sương mờ.", "Sapa, Lào Cai"),
            Triple("Kinh nghiệm du thuyền 5 sao Vịnh Hạ Long", "Hành trình len lỏi qua hàng ngàn hòn đảo đá vôi kỳ vĩ, tham quan hang Sửng Sốt, chèo thuyền kayak tại hang Luồn và ngắm hoàng hôn rực rỡ từ boong tàu.", "Hạ Long, Quảng Ninh"),
            Triple("Ninh Bình - Tràng An non nước hữu tình", "Chèo thuyền dọc dòng sông Sào Khê qua các hang động kỳ bí, leo 500 bậc đá lên đỉnh hang Múa ngắm trọn vẹn thung lũng lúa vàng Tam Cốc bát ngát.", "Tràng An, Ninh Bình"),
            Triple("Phong Nha Kẻ Bàng - Kỳ quan trong lòng đất", "Khám phá động Thiên Đường tráng lệ với hệ thống thạch nhũ lung linh, trải nghiệm đu dây zipline tại sông Chày hang Tối đầy phấn khích.", "Phong Nha - Kẻ Bàng, Quảng Bình"),
            Triple("Cố đô ở Thừa Thiên Huế - Vẻ đẹp trầm mặc cổ kính", "Tham quan Đại Nội hoàng tráng, viếng lăng Khải Định, lăng Tự Đức cổ kính, lắng nghe ca Huế trên sông Hương và thưởng thức bún bò Huế chuẩn vị.", "Thừa Thiên Huế"),
            Triple("Đà Nẵng - Thành phố của những cây cầu", "Check-in Cầu Vàng nổi tiếng trên Bà Nà Hills, tắm biển Mỹ Khê cát trắng mịn, ngắm Cầu Rồng phun lửa phun nước lung linh vào cuối tuần.", "Đà Nẵng"),
            Triple("Hội An - Hoài niệm phố cổ đèn lồng", "Dạo bước qua Chùa Cầu cổ kính, đi thuyền thả hoa đăng lung linh trên dòng sông Hoài thơ mộng, thưởng thức món cao lầu và nước mót mát lành.", "Hội An, Quảng Nam"),
            Triple("Quy Nhơn - Kỳ Co Eo Gió hoang sơ", "Đắm mình trong làn nước xanh ngọc bích tại bãi tắm Kỳ Co, ngắm vách đá dựng đứng kỳ vĩ tại Eo Gió - nơi đón hoàng hôn đẹp nhất Việt Nam.", "Quy Nhơn, Bình Định"),
            Triple("Nha Trang - Thiên đường biển gọi", "Tham quan VinWonders trên đảo Hòn Tre náo nhiệt, lặn ngắm san hô tại hòn Mun, thưởng thức hải sản tươi ngon ngay tại cảng biển.", "Nha Trang, Khánh Hòa"),
            Triple("Đà Lạt - Thành phố ngàn hoa thơ mộng", "Thưởng ngoạn hồ Tuyền Lâm bảng lảng sương sớm, check-in thung lũng Tình Yêu, nhâm nhi cà phê nóng giữa rừng thông bạt ngàn đón gió lạnh.", "Đà Lạt, Lâm Đồng"),
            Triple("Mũi Né - Đồi cát bay lộng gió", "Trải nghiệm lái xe địa hình trên đồi cát trắng mịn màng, dạo bước Suối Tiên với vách đất sét đỏ rực rỡ rực rỡ dưới nắng vàng.", "Mũi Né, Bình Thuận"),
            Triple("Sài Gòn - Nhịp sống năng động 24h", "Ghé thăm Nhà thờ Đức Bà, Bưu điện Thành phố cổ kính, uống cà phê bệt vỉa hè và ngắm toàn cảnh Sài Gòn lấp lánh từ tòa nhà Bitexco.", "Sài Gòn, Thành phố Hồ Chí Minh"),
            Triple("Cần Thơ - Chợ nổi Cái Răng sông nước", "Thức dậy từ sớm đón bình minh trên chợ nổi Cái Răng, thưởng thức hủ tiếu lắc độc đáo ngay trên ghe thuyền sóng nước Miền Tây.", "Cần Thơ"),
            Triple("Phú Quốc - Đảo Ngọc hoàng hôn rực rỡ", "Tắm biển bãi Sao cát trắng như kem, tham quan Grand World không ngủ, ngắm hoàng hôn buông xuống đẹp đến nghẹt thở tại Sunset Sanato.", "Phú Quốc, Kiên Giang"),
            Triple("Côn Đảo - Uy nghiêm và hoang sơ", "Thắp hương mộ chị Võ Thị Sáu linh thiêng lúc nửa đêm, hòa mình vào bãi Đầm Trầu hoang sơ yên bình và tham quan di tích nhà tù lịch sử.", "Côn Đảo, Bà Rịa Vũng Tàu"),
            Triple("Cao Bằng - Thác Bản Giốc hùng vĩ biên thùy", "Thác nước tự nhiên lớn nhất Đông Nam Á đổ nước trắng xóa bạt ngàn, tham quan suối Lê Nin trong vắt như gương và hang Pác Bó lịch sử.", "Cao Bằng"),
            Triple("Mai Châu - Bản Lác thung lũng thanh bình", "Đạp xe giữa những cánh đồng lúa xanh mướt mải, thưởng thức xôi nếp nương thơm dẻo và hòa mình vào điệu múa sạp rộn ràng của người Thái.", "Mai Châu, Hòa Bình"),
            Triple("Tây Ninh - Chinh phục đỉnh núi Bà Đen", "Hành trình đi cáp treo hiện đại lên nóc nhà Đông Nam Bộ, chiêm bái tượng Phật Bà Tây Bổ Đà Sơn uy nghiêm ẩn hiện trong mây mờ.", "Tây Ninh"),
            Triple("Khu du lịch sinh thái Suối Nặm Thoong - Cao Bằng", "Cách trung tâm thành phố Cao Bằng khoảng 25km, nổi bật với dòng suối trong vắt mát rượi chảy lượn quanh vách núi rừng kỳ vĩ. Điểm dã ngoại dã ngoại cắm trại lý tưởng thuộc Công viên địa chất toàn cầu UNESCO Non nước Cao Bằng, kết hợp nhà sàn mộc mạc thưởng thức lợn quay mác mật, cá suối nướng thơm ngon.", "Suối Nặm Thoong, Cao Bằng"),
            Triple("Khu di tích lịch sử anh hùng Kim Đồng - Cao Bằng", "Nằm tại làng Nà Mạ, xã Trường Hà, huyện Hà Quảng, là 'địa chỉ đỏ' thiêng liêng tưởng nhớ người đội trưởng đầu tiên của Đội Thanh Niên Tiền Phong Hồ Chí Minh. Khuôn viên rộng 12ha trang nghiêm dưới chân núi Tèo Lài với tượng đài Kim Đồng tay nâng chim bồ câu, đền thờ, nhà trưng bày lịch sử và hang Nộc Én.", "Di tích Kim Đồng, Cao Bằng"),
            Triple("Phú Yên - Hoa vàng trên cỏ xanh", "Check-in gành Đá Đĩa độc nhất vô nhị với các khối đá lục giác xếp chồng, ngắm bình minh sớm nhất tại hải đăng Đại Lãnh cực Đông.", "Phú Yên")
        )

        val ruNotes = listOf(
            Triple("Quảng trường Đỏ Moscow - Trái tim nước Nga", "Tận mắt chiêm ngưỡng điện Kremlin uy nghiêm, nhà thờ Saint Basil với những mái vòm củ hành rực rỡ như cổ tích và bảo tàng lịch sử quốc gia.", "Moscow"),
            Triple("Saint Petersburg - Thành phố của những cung điện", "Khám phá bảo tàng Hermitage tráng lệ, Cung điện Mùa đông nguy nga của Sa hoàng và dạo thuyền trên sông Neva thơ mộng ngắm cầu mở.", "Saint Petersburg"),
            Triple("Hồ Baikal Siberia - Viên ngọc xanh vĩnh cửu", "Hồ nước ngọt sâu nhất thế giới với làn nước trong suốt như pha lê. Vào mùa đông, hồ đóng băng tạo thành những đường rạn nứt tuyệt mỹ.", "Baikal, Siberia"),
            Triple("Kazan - Sự giao thoa văn hóa Á-Âu", "Khám phá thủ phủ của nước cộng hòa Tatarstan với thánh đường Hồi giáo Kul Sharif lộng lẫy nằm ngay cạnh nhà thờ Chính thống giáo cổ kính.", "Kazan"),
            Triple("Vladivostok - Thành phố cảng Viễn Đông", "Điểm cuối của tuyến đường sắt xuyên Siberia huyền thoại, đi cáp treo ngắm vịnh Sừng Vàng và thưởng thức cua hoàng đế siêu tươi ngon.", "Vladivostok"),
            Triple("Sochi - Thủ đô mùa hè bên bờ Biển Đen", "Thành phố nghỉ dưỡng xinh đẹp với những bãi biển ấm áp tuyệt vời, rặng palm xanh mướt và khu trượt tuyết đẳng cấp Olympic Krasnaya Polyana.", "Sochi"),
            Triple("Vành đai Vàng nước Nga - Golden Ring", "Hành trình qua các thị trấn cổ kính như Suzdal, Vladimir đầy nhà thờ mái vòm vàng rực rỡ, cảm nhận nhịp sống Nga xưa yên bình.", "Golden Ring"),
            Triple("Đảo Kizhi - Tuyệt tác kiến trúc gỗ không đinh", "Nằm giữa hồ Onega, hòn đảo nổi tiếng với hai nhà thờ bằng gỗ thông độc đáo được dựng hoàn toàn thủ công không cần dùng tới một chiếc đinh nào.", "Kizhi, Karelia"),
            Triple("Dãy núi Caucasus - Hùng vĩ biên thùy", "Nơi có đỉnh Elbrus cao nhất châu Âu quanh năm tuyết phủ trắng xóa, là thiên đường cho những tín đồ leo núi và đam mê mạo hiểm.", "Caucasus"),
            Triple("Kamchatka - Thung lũng mạch nước phun kỳ bí", "Vùng đất của lửa và băng với hàng trăm núi lửa đang hoạt động, suối nước nóng bốc hơi nghi ngút giữa núi tuyết và gấu nâu săn cá hồi.", "Kamchatka"),
            Triple("Yekaterinburg - Ranh giới Á Âu huyền thoại", "Check-in tại đài tưởng niệm cột mốc phân chia hai châu lục Á - Âu, tham quan nhà thờ Đổ Máu linh thiêng nơi gia đình Sa hoàng cuối cùng tạ thế.", "Yekaterinburg"),
            Triple("Nizhny Novgorod - Pháo đài cổ bên sông Volga", "Ghé thăm điện Kremlin Nizhny Novgorod sừng sững trên đồi cao, dạo bước phố đi bộ Pokrovskaya ngắm các tòa nhà kiến trúc gỗ cổ tuyệt đẹp.", "Nizhny Novgorod"),
            Triple("Murmansk - Săn Bắc Cực Quang huyền diệu", "Nằm sâu trong vòng Bắc Cực, đây là nơi ngắm những dải lụa ánh sáng xanh cực quang huyền ảo nhảy múa trên bầu trời đêm đông lạnh giá.", "Murmansk"),
            Triple("Cung điện Peterhof - Versailles của nước Nga", "Quần thể cung điện và đài phun nước hoành tráng bậc nhất thế giới hướng ra vịnh Phần Lan, biểu tượng cho quyền lực Sa hoàng Peter Đại đế.", "Peterhof"),
            Triple("Đồi Chim Sẻ - Toàn cảnh thủ đô Moscow", "Điểm ngắm cảnh cao nhất thủ đô ngắm trọn vẹn sân vận động Luzhniki, trường đại học Lomonosov (MSU) vĩ đại và nhịp sống nhộn nhịp bên sông.", "Moscow"),
            Triple("Nhà thờ Saint Basil - Kiệt tác kiến trúc Nga", "Công trình biểu tượng quốc gia với 9 tháp mái vòm màu sắc sặc sỡ, được Sa hoàng Ivan bạo chúa xây dựng để kỷ niệm chiến thắng Kazan năm 1552.", "Moscow"),
            Triple("Cung điện Catherine - Căn phòng hổ phách huyền thoại", "Tọa lạc tại Tsarskoye Selo, cung điện sở hữu phòng hổ phách quý giá lộng lẫy được chế tác cực kỳ tinh xảo từ hàng tấn đá hổ phách tự nhiên.", "Pushkin"),
            Triple("Bảo tàng Hermitage - Kho tàng nghệ thuật nhân loại", "Một trong những bảo tàng lớn và cổ nhất thế giới với hơn 3 triệu tác phẩm nghệ thuật vô giá từ thời tiền sử đến hiện đại.", "Saint Petersburg"),
            Triple("Siberia - Vùng đất băng giá huyền thoại", "Trải nghiệm cái lạnh kỷ lục tại Oymyakon - ngôi làng lạnh nhất thế giới có người sinh sống, đi xe chó kéo vượt qua những cánh rừng taiga phủ tuyết.", "Siberia"),
            Triple("Dãy núi Altay - Thiên đường sinh thái hoang sơ", "Dòng sông Katun xanh màu ngọc bích chảy len lỏi giữa rừng thông, không khí trong lành nguyên sơ thích hợp cho các chuyến đi chữa lành.", "Altay")
        )

        val streetFoodNotes = listOf(
            Triple("Phở Gánh Hà Nội - Tinh hoa ẩm thực phố cổ", "Tô phở nóng hổi thơm nức mùi hành hoa, bánh phở mềm dẻo cùng nước dùng ngọt thanh từ xương ống ninh kỹ. Trải nghiệm ngồi ghế nhựa vỉa hè đón bình minh Hà Nội thật sự khó quên.", "Hà Nội"),
            Triple("Bánh Mì Sài Gòn - Vua ẩm thực đường phố thế giới", "Vỏ bánh mì giòn rụm kẹp pate gan thơm béo, chả lụa, thịt xá xíu, dưa góp chua ngọt cùng chút ớt cay nồng. Món ăn nhanh đậm đà đầy đủ dưỡng chất.", "Sài Gòn"),
            Triple("Bún Bò ở Thừa Thiên Huế - Hương vị đậm đà xứ Cố đô", "Sợi bún toàn mềm dẻo trong nước dùng sóng sánh màu dầu điều thơm nức sả ớt, ăn kèm huyết luộc, giò heo, chả cua và đĩa rau sống bắp chuối thái mỏng.", "Thừa Thiên Huế"),
            Triple("Cơm Tấm Sài Gòn - Sườn bì chả mỡ hành", "Hạt cơm tấm thơm dẻo ăn kèm miếng sườn nướng mỡ hành đậm đà, bì chả giòn sần sật, trứng ốp la lòng đào và chén nước mắm kẹo ớt hiểm.", "Sài Gòn"),
            Triple("Bánh Xèo Miền Tây - Giòn rụm ngập tràn nhân tôm thịt", "Bánh xèo vàng ươm thơm lừng nước cốt dừa, nhân tôm đất, thịt ba chỉ và giá đỗ. Cuộn cùng bánh tráng, rau rừng chấm nước mắm tỏi ớt chua ngọt.", "Miền Tây"),
            Triple("Cà Phê Trứng Hà Nội - Sóng sánh vị béo ngậy ngọt ngào", "Lớp kem trứng đánh bông mịn như mây béo ngậy đắng nhẹ quyện cùng cà phê phin đậm đà thơm nức, thức uống biểu tượng của thủ đô.", "Hà Nội"),
            Triple("Mì Quảng Quảng Nam - Món ăn hồn túy xứ Quảng", "Sợi mì Quảng vàng tươi đậm đà nước dùng ninh từ tôm, thịt heo, gà hoặc ếch. Ăn kèm bánh tráng nướng giòn rụm, đậu phụng rang thơm phức và đĩa rau sống đắng, bắp chuối, rau húng tươi ngon.", "Quảng Nam"),
            Triple("Cao Lầu Hội An - Đậm đà phong vị phố cổ", "Sợi cao lầu vàng óng dẻo dai chế biến với nước giếng Cả Bá cổ kính, ăn kèm thịt xá xíu đậm đà, tóp mỡ giòn tan và rau đắng Trà Quế mát lành.", "Hội An, Quảng Nam")
        )

        val leaderNotes = listOf(
            Triple("Chủ tịch Hồ Chí Minh - Vị lãnh tụ kính yêu của dân tộc", "Sinh năm 1890 tại Kim Liên, Nam Đàn, Nghệ An. Bác Hồ là người anh hùng giải phóng dân tộc, danh nhân văn hóa thế giới, đã ra đi tìm đường cứu nước năm 1911 và đọc Bản Tuyên ngôn Độc lập khai sinh nước Việt Nam Dân chủ Cộng hòa ngày 2/9/1945.", "Nghệ An, Hà Nội"),
            Triple("Đại tướng Võ Nguyên Giáp - Người anh cả của QĐND Việt Nam", "Vị tướng tài ba thiên tài quân sự, chỉ huy trực tiếp chiến dịch Điện Biên Phủ 1954 lừng lẫy năm châu, chấn động địa cầu và chiến dịch Hồ Chí Minh lịch sử năm 1975 giải phóng hoàn toàn miền Nam, thống nhất đất nước.", "Quảng Bình, Hà Nội"),
            Triple("Cố Tổng Bí thư Nguyễn Phú Trọng - Ngọn cờ chỉnh đốn Đảng & Ngoại giao cây tre", "Tổng Bí thư ĐCSVN (2011-2024). Người lãnh đạo kiên trung, tấm gương sáng về đạo đức cách mạng giản dị, liêm chính. Tác giả của công cuộc đấu tranh phòng chống tham nhũng 'không có vùng cấm' và trường phái 'Ngoại giao cây tre Việt Nam' nâng cao vị thế đất nước trên trường quốc tế.", "Hà Nội"),
            Triple("Tổng Bí thư Tô Lâm - Đột phá Chuyển đổi số & Kỷ nguyên vươn mình", "Tổng Bí thư ĐCSVN từ 8/2024, Chủ tịch nước CHXHCNVN (từ 5/2024). Người chỉ đạo quyết liệt Đề án 06 về chuyển đổi số quốc gia, đẩy mạnh tinh gọn bộ máy nhà nước, đưa đất nước vững bước tiến vào kỷ nguyên mới phát triển nhanh, hiện đại và thịnh vượng.", "Hưng Yên, Hà Nội"),
            Triple("Cố Tổng Bí thư Nguyễn Văn Linh - Người mở đường Đổi Mới", "Nhà lãnh đạo kiên trung, sáng tạo, gắn liền với công cuộc Đổi Mới đất nước từ Đại hội VI (1986) với những bài viết nổi tiếng 'Những việc cần làm ngay' mang lại luồng sinh khí mới cho nền kinh tế xã hội.", "Hưng Yên, TPHCM"),
            Triple("Cố Thủ tướng Phạm Văn Đồng - Nhà ngoại giao xuất sắc", "Người học trò xuất sắc của Chủ tịch Hồ Chí Minh, Thủ tướng tại vị lâu nhất Việt Nam (1955-1987). Ông là nhà văn hóa, nhà ngoại giao kiệt xuất, Trưởng đoàn đàm phán Hiệp định Giơ-ne-vơ 1954.", "Quảng Ngãi, Hà Nội"),
            Triple("Cố Tổng Bí thư Lê Duẩn - Lãnh đạo kiên cường cách mạng", "Nhà lý luận xuất sắc, chiến sĩ cách mạng kiên trung có đóng góp to lớn vào đường lối chỉ đạo cuộc kháng chiến chống Mỹ cứu nước, giải phóng miền Nam thống nhất Tổ quốc.", "Quảng Trị, Hà Nội")
        )

        // Add 55 Notes
        var noteIdCounter = 1
        vnNotes.forEach { (title, desc, place) ->
            list.add(
                SampleItem.Note(
                    id = noteIdCounter++,
                    title = title,
                    content = desc,
                    category = "Du lịch Việt Nam",
                    tags = "#vietnam, #travel, #${place.lowercase().replace(" ", "").replace(",", "")}"
                )
            )
        }
        ruNotes.forEach { (title, desc, place) ->
            list.add(
                SampleItem.Note(
                    id = noteIdCounter++,
                    title = title,
                    content = desc,
                    category = "Du lịch nước Nga",
                    tags = "#russia, #travel, #${place.lowercase().replace(" ", "").replace(",", "")}"
                )
            )
        }
        streetFoodNotes.forEach { (title, desc, place) ->
            list.add(
                SampleItem.Note(
                    id = noteIdCounter++,
                    title = title,
                    content = desc,
                    category = "Ẩm thực đường phố Việt Nam",
                    tags = "#amthuc, #miquang, #${place.lowercase().replace(" ", "").replace(",", "")}"
                )
            )
        }
        leaderNotes.forEach { (title, desc, place) ->
            list.add(
                SampleItem.Note(
                    id = noteIdCounter++,
                    title = title,
                    content = desc,
                    category = "Tiểu sử lãnh đạo Việt Nam",
                    tags = "#lichsu, #lanhdao, #${place.lowercase().replace(" ", "").replace(",", "")}"
                )
            )
        }

        // 2. Generate 53 Comments (22 VN, 20 Russia, 6 Ẩm thực, 5 Lãnh đạo)
        val commentTemplates = listOf(
            "Cảnh đẹp quá! Mình nhất định phải đến đây một lần trong đời.",
            "Bài viết rất chi tiết, cảm ơn bạn đã chia sẻ kinh nghiệm quý giá này nhé!",
            "Chi phí trọn gói cho chuyến đi này khoảng bao nhiêu tiền vậy chủ thớt ơi?",
            "Có cần phải xin visa trước lâu không bạn? Thủ tục có phức tạp lắm không?",
            "Mình vừa đi tuần trước xong, đồ ăn ở đây siêu ngon mà người dân lại cực kỳ mến khách.",
            "Mùa nào đi là đẹp nhất hả bạn? Mình đang lên kế hoạch cho cả gia đình.",
            "Ảnh chụp góc nào cũng đẹp như tranh vẽ vậy, bạn dùng điện thoại hay máy ảnh thế?",
            "Nhìn yên bình quá, rất thích hợp để đi nghỉ dưỡng và chữa lành tâm hồn.",
            "Bạn có gợi ý homestay hay khách sạn nào giá cả hợp lý gần trung tâm không?",
            "Một trải nghiệm tuyệt vời! Cảm ơn bài review rất có tâm của bạn.",
            "Đọc bài viết xong chỉ muốn xách ba lô lên và đi ngay lập tức thôi!",
            "Kiến trúc ở đây thật sự quá vĩ đại và cổ kính, nhìn rất ấn tượng.",
            "Chi phí hải sản hay ăn uống ở đây có bị đắt đỏ hay chặt chém gì không bạn?",
            "Có chỗ nào chơi phù hợp cho trẻ em nhỏ đi cùng không ạ?",
            "Tuyệt vời quá! Cảnh sắc thiên nhiên nước mình không thua kém gì nước ngoài cả.",
            "Địa điểm này đi tự túc dễ không bạn hay bắt buộc phải đi theo tour?",
            "Nơi này chụp ảnh sống ảo thì đúng là đỉnh của chóp luôn rồi!",
            "Cảm giác được hòa mình vào không gian tuyết trắng / biển xanh thật là sướng.",
            "Thời tiết lúc bạn đi có lạnh lắm không, cần chuẩn bị trang phục thế nào?",
            "Suối Nặm Thoong này có an toàn cho trẻ em bơi lội và vui chơi không bạn nhỉ?",
            "Cảm động quá, đây thực sự là địa chỉ đỏ giáo dục truyền thống cách mạng cực kỳ ý nghĩa cho thế hệ măng non.",
            "Biển Phú Yên xanh ngắt trong veo, bãi cỏ rộng thênh thang tha hồ chạy nhảy nhé!",
            "Có được cắm trại dựng lều dã ngoại ở gần khu vực di tích anh Kim Đồng không bạn?",
            "Món ăn đường phố này ngon đỉnh cao, ngập tràn hương vị béo ngậy đậm đà khó cưỡng!",
            "Giá thành cực kỳ bình dân sinh viên, chủ quán lại siêu vui tính nhiệt tình nữa!",
            "Nước dùng đậm vị thanh ngọt tự nhiên, ăn một lần là nghiện luôn cả tuần!",
            "Mì Quảng Quảng Nam ăn kèm bánh tráng nướng giòn rụm và đậu phụng rang thơm nức thật sự là mỹ vị xứ Quảng!",
            "Du lịch cáp treo lên đỉnh núi Bà Đen ở tỉnh Tây Ninh, Việt Nam thật tuyệt vời!",
            "Thật tự hào và biết ơn những cống hiến hy sinh vĩ đại của các vị lãnh đạo tiền bối đối với nền độc lập tự do của Tổ quốc!",
            "Bài viết tổng hợp tiểu sử rất trang trọng và xúc động, giúp thế hệ trẻ hiểu rõ hơn về lịch sử hào hùng của dân tộc.",
            "Tự hào về lịch sử vẻ vang của Việt Nam dưới sự chỉ đạo kiệt xuất của các bậc lãnh tụ kính yêu!"
        )

        val authors = listOf(
            "Minh Anh", "Thanh Hằng", "Khánh Nam", "Quỳnh Chi", "Hoàng Long",
            "Thu Trang", "Duy Mạnh", "Hương Giang", "Tuấn Kiệt", "Bảo Ngọc",
            "Đức Huy", "Hồng Nhung", "Viết Tiến", "Ngọc Diệp", "Quốc Anh",
            "Phương Linh", "Văn Hải", "Thanh Thảo", "Minh Đức", "Mai Phương"
        )

        var commentIdCounter = 1
        for (i in 0 until 22) {
            val vnNoteTitle = vnNotes[i % vnNotes.size].first
            list.add(
                SampleItem.Comment(
                    id = commentIdCounter++,
                    author = authors[i % authors.size],
                    content = "Dành cho chuyến đi [${vnNoteTitle}]: ${commentTemplates[i]}",
                    noteTitle = vnNoteTitle,
                    category = "Du lịch Việt Nam"
                )
            )
        }
        for (i in 0 until 20) {
            val ruNoteTitle = ruNotes[i].first
            list.add(
                SampleItem.Comment(
                    id = commentIdCounter++,
                    author = authors[(i + 5) % authors.size],
                    content = "Dành cho chuyến đi [${ruNoteTitle}]: ${commentTemplates[i]}",
                    noteTitle = ruNoteTitle,
                    category = "Du lịch nước Nga"
                )
            )
        }
        for (i in 0 until 6) {
            val foodTitle = streetFoodNotes[i % streetFoodNotes.size].first
            list.add(
                SampleItem.Comment(
                    id = commentIdCounter++,
                    author = authors[(i + 10) % authors.size],
                    content = "Trải nghiệm ẩm thực [${foodTitle}]: ${commentTemplates[23 + (i % 4)]}",
                    noteTitle = foodTitle,
                    category = "Ẩm thực đường phố Việt Nam"
                )
            )
        }
        for (i in 0 until 5) {
            val leaderTitle = leaderNotes[i % leaderNotes.size].first
            list.add(
                SampleItem.Comment(
                    id = commentIdCounter++,
                    author = authors[(i + 15) % authors.size],
                    content = "Tư liệu lịch sử [${leaderTitle}]: ${commentTemplates[28 + (i % 3)]}",
                    noteTitle = leaderTitle,
                    category = "Tiểu sử lãnh đạo Việt Nam"
                )
            )
        }

        // 3. Generate 52 Replies (22 VN, 20 Russia, 5 Ẩm thực, 5 Lãnh đạo)
        val replyTemplates = listOf(
            "Đúng vậy bạn ơi, đi một lần là nhớ mãi luôn đó!",
            "Chi phí tự túc hết tầm 3-5 triệu thôi nè, khá là tiết kiệm.",
            "E-visa hiện tại làm online cực kỳ nhanh gọn, chỉ mất khoảng 3 ngày thôi bạn.",
            "Bạn nên chuẩn bị quần áo ấm nha, mùa đông nhiệt độ xuống rất thấp đó.",
            "Đồ ăn ở đây rất hợp khẩu vị, giá cả cực kỳ bình dân không lo chặt chém nha.",
            "Cảm ơn bạn! Mình chụp hoàn toàn bằng điện thoại đời thường thôi nè.",
            "Tháng 9 - tháng 10 mùa thu vàng là thời điểm lãng mạn nhất để đi nhé.",
            "Tự túc hoàn toàn dễ dàng bạn nhé, đường sá đi lại giờ rất thuận tiện.",
            "Gần trung tâm có rất nhiều homestay xinh xắn giá chỉ từ 300k/đêm thôi.",
            "Nhất định phải thử món đặc sản địa phương ở đây nha, ngon quên sầu!",
            "Chuẩn luôn ạ, không gian ở đây rộng rãi, mát mẻ, các bé tha hồ chạy nhảy.",
            "Đồng ý với bạn, kiến trúc chạm khắc tinh xảo vô cùng, nhìn trực tiếp ngỡ ngàng luôn.",
            "Cảm ơn bạn đã quan tâm! Chúc bạn có chuyến đi thật vui vẻ và an toàn nha.",
            "Xách ba lô lên đi thôi bạn ơi, tuổi trẻ phải đi và trải nghiệm chứ!",
            "Bạn có thể đặt trước dịch vụ trên mạng để có giá ưu đãi hơn nha.",
            "Cảnh sắc tự nhiên hùng vĩ vô cùng, đi rồi mới thấy nước mình đẹp thế nào.",
            "Cảm giác được hít thở bầu không khí trong lành ở đây thật sự rất sảng khoái.",
            "Cứ chuẩn bị tâm lý thoải mái và một chiếc điện thoại đầy pin để chụp ảnh nhé!",
            "Đúng rồi bạn, người dân cực kỳ thân thiện, nhiệt tình chỉ đường lắm luôn.",
            "Vào mùa cạn nước suối Nặm Thoong nông và chảy rất êm ả, trong vắt nên trẻ em bơi lội và tắm mát dã ngoại cực kỳ an toàn nha!",
            "Dạ đúng ạ, được dâng hương tưởng nhớ người anh hùng nhỏ tuổi trước tượng đài trang nghiêm lộng gió thật sự vô cùng xúc động!",
            "Phú Yên cực kỳ hoang sơ và bình dị luôn đó bạn ơi!",
            "Thêm chút quẩy giòn và chút ớt chưng cay nồng nữa là xuất sắc không còn gì bằng nha!",
            "Chuẩn vị gia truyền lâu năm luôn, lần nào ra đây mình cũng phải ăn 2 tô mới đã!",
            "Chuẩn luôn bạn ơi, Mì Quảng ăn cùng bánh tráng nướng giòn và đĩa rau sống Trà Quế thì chuẩn vị xứ Quảng nhất!",
            "Đúng vậy ạ, truyền thống lịch sử hào hùng của dân tộc luôn là niềm tự hào lớn lao cho các thế hệ mai sau!",
            "Tự hào truyền thống cách mạng Việt Nam, đời đời ghi nhớ công ơn các anh hùng liệt sĩ và lãnh đạo tiền bối!"
        )

        var replyIdCounter = 1
        for (i in 0 until 22) {
            val parentAuthor = authors[i % authors.size]
            list.add(
                SampleItem.Reply(
                    id = replyIdCounter++,
                    author = authors[(i + 3) % authors.size],
                    content = "Trả lời @$parentAuthor: ${replyTemplates[i]}",
                    parentCommentContent = commentTemplates[i],
                    category = "Du lịch Việt Nam"
                )
            )
        }
        for (i in 0 until 20) {
            val parentAuthor = authors[(i + 5) % authors.size]
            list.add(
                SampleItem.Reply(
                    id = replyIdCounter++,
                    author = authors[(i + 8) % authors.size],
                    content = "Trả lời @$parentAuthor: ${replyTemplates[i]}",
                    parentCommentContent = commentTemplates[i],
                    category = "Du lịch nước Nga"
                )
            )
        }
        for (i in 0 until 5) {
            val parentAuthor = authors[(i + 10) % authors.size]
            list.add(
                SampleItem.Reply(
                    id = replyIdCounter++,
                    author = authors[(i + 12) % authors.size],
                    content = "Trả lời @$parentAuthor: ${replyTemplates[22 + (i % 3)]}",
                    parentCommentContent = commentTemplates[23 + (i % 3)],
                    category = "Ẩm thực đường phố Việt Nam"
                )
            )
        }
        for (i in 0 until 5) {
            val parentAuthor = authors[(i + 15) % authors.size]
            list.add(
                SampleItem.Reply(
                    id = replyIdCounter++,
                    author = authors[(i + 18) % authors.size],
                    content = "Trả lời @$parentAuthor: ${replyTemplates[25 + (i % 2)]}",
                    parentCommentContent = commentTemplates[28 + (i % 2)],
                    category = "Tiểu sử lãnh đạo Việt Nam"
                )
            )
        }

        return list
    }
}

@Composable
fun SampleDialog(
    onImportNote: (title: String, content: String, category: String, tags: String) -> Unit,
    onDismiss: () -> Unit
) {
    val allSamples = remember { SamplesProvider.generate160Samples() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Tất cả") }
    var selectedTypeFilter by remember { mutableStateOf("Tất cả") }

    val filteredSamples = remember(searchQuery, selectedCategoryFilter, selectedTypeFilter) {
        allSamples.filter { item ->
            // Category filter
            val matchesCategory = when (selectedCategoryFilter) {
                "Du lịch Việt Nam" -> {
                    when (item) {
                        is SampleItem.Note -> item.category == "Du lịch Việt Nam"
                        is SampleItem.Comment -> item.category == "Du lịch Việt Nam"
                        is SampleItem.Reply -> item.category == "Du lịch Việt Nam"
                    }
                }
                "Du lịch nước Nga" -> {
                    when (item) {
                        is SampleItem.Note -> item.category == "Du lịch nước Nga"
                        is SampleItem.Comment -> item.category == "Du lịch nước Nga"
                        is SampleItem.Reply -> item.category == "Du lịch nước Nga"
                    }
                }
                "Ẩm thực đường phố Việt Nam" -> {
                    when (item) {
                        is SampleItem.Note -> item.category == "Ẩm thực đường phố Việt Nam"
                        is SampleItem.Comment -> item.category == "Ẩm thực đường phố Việt Nam"
                        is SampleItem.Reply -> item.category == "Ẩm thực đường phố Việt Nam"
                    }
                }
                "Tiểu sử lãnh đạo Việt Nam" -> {
                    when (item) {
                        is SampleItem.Note -> item.category == "Tiểu sử lãnh đạo Việt Nam"
                        is SampleItem.Comment -> item.category == "Tiểu sử lãnh đạo Việt Nam"
                        is SampleItem.Reply -> item.category == "Tiểu sử lãnh đạo Việt Nam"
                    }
                }
                else -> true
            }

            // Type filter
            val matchesType = when (selectedTypeFilter) {
                "Ghi chú" -> item is SampleItem.Note
                "Bình luận" -> item is SampleItem.Comment
                "Phản hồi" -> item is SampleItem.Reply
                else -> true
            }

            // Search query
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.lowercase()
                when (item) {
                    is SampleItem.Note -> item.title.lowercase().contains(q) || item.content.lowercase().contains(q) || item.tags.lowercase().contains(q)
                    is SampleItem.Comment -> item.author.lowercase().contains(q) || item.content.lowercase().contains(q) || item.noteTitle.lowercase().contains(q)
                    is SampleItem.Reply -> item.author.lowercase().contains(q) || item.content.lowercase().contains(q) || item.parentCommentContent.lowercase().contains(q)
                }
            }

            matchesCategory && matchesType && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("samples_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "${allSamples.size} mẫu ghi chú, bình luận và phản hồi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tổng số mẫu lọc được: ${filteredSamples.size} / ${allSamples.size}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_samples")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm tiêu đề, tác giả, nội dung mẫu...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Xóa")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("travel_samples_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Unified Scrollable Filter Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Category Filter Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chủ đề:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(62.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "Tất cả",
                                    "Du lịch Việt Nam",
                                    "Du lịch nước Nga",
                                    "Ẩm thực đường phố Việt Nam",
                                    "Tiểu sử lãnh đạo Việt Nam"
                                ).forEach { cat ->
                                    FilterChip(
                                        selected = selectedCategoryFilter == cat,
                                        onClick = { selectedCategoryFilter = cat },
                                        label = { Text(cat, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        // Type Filter Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Loại:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(62.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Tất cả", "Ghi chú", "Bình luận", "Phản hồi").forEach { type ->
                                    FilterChip(
                                        selected = selectedTypeFilter == type,
                                        onClick = { selectedTypeFilter = type },
                                        label = { Text(type, fontSize = 12.sp) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Samples List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredSamples.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không tìm thấy mẫu nào khớp với bộ lọc hiện tại.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(filteredSamples) { sample ->
                        when (sample) {
                            is SampleItem.Note -> {
                                SampleNoteCard(
                                    note = sample,
                                    onImport = {
                                        onImportNote(sample.title, sample.content, sample.category, sample.tags)
                                    }
                                )
                            }
                            is SampleItem.Comment -> {
                                SampleCommentCard(comment = sample)
                            }
                            is SampleItem.Reply -> {
                                SampleReplyCard(reply = sample)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                // Bottom Quick Action
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Mẹo: Đối với các mẫu Ghi chú, bạn có thể nhấn nút Nhập nhanh để lưu ngay ghi chú đó vào danh sách của bạn và nhận +20 Kim Cương!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SampleNoteCard(
    note: SampleItem.Note,
    onImport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (note.category) {
                            "Du lịch Việt Nam" -> Color(0xFFE8F5E9)
                            "Du lịch nước Nga" -> Color(0xFFE3F2FD)
                            "Ẩm thực đường phố Việt Nam" -> Color(0xFFFFF3E0)
                            "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFE8F5E9)
                        }
                    ) {
                        Text(
                            text = note.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (note.category) {
                                "Du lịch Việt Nam" -> Color(0xFF2E7D32)
                                "Du lịch nước Nga" -> Color(0xFF1565C0)
                                "Ẩm thực đường phố Việt Nam" -> Color(0xFFE65100)
                                "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFC62828)
                                else -> Color(0xFF2E7D32)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "GHI CHÚ MẪU",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Button(
                    onClick = onImport,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(32.dp).testTag("btn_import_sample_${note.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nhập nhanh", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.tags,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SampleCommentCard(
    comment: SampleItem.Comment
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (comment.category) {
                        "Du lịch Việt Nam" -> Color(0xFFE8F5E9)
                        "Du lịch nước Nga" -> Color(0xFFE3F2FD)
                        "Ẩm thực đường phố Việt Nam" -> Color(0xFFFFF3E0)
                        "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = comment.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (comment.category) {
                            "Du lịch Việt Nam" -> Color(0xFF2E7D32)
                            "Du lịch nước Nga" -> Color(0xFF1565C0)
                            "Ẩm thực đường phố Việt Nam" -> Color(0xFFE65100)
                            "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFC62828)
                            else -> Color(0xFF2E7D32)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "BÌNH LUẬN MẪU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = comment.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SampleReplyCard(
    reply: SampleItem.Reply
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (reply.category) {
                        "Du lịch Việt Nam" -> Color(0xFFE8F5E9)
                        "Du lịch nước Nga" -> Color(0xFFE3F2FD)
                        "Ẩm thực đường phố Việt Nam" -> Color(0xFFFFF3E0)
                        "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Text(
                        text = reply.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (reply.category) {
                            "Du lịch Việt Nam" -> Color(0xFF2E7D32)
                            "Du lịch nước Nga" -> Color(0xFF1565C0)
                            "Ẩm thực đường phố Việt Nam" -> Color(0xFFE65100)
                            "Tiểu sử lãnh đạo Việt Nam" -> Color(0xFFC62828)
                            else -> Color(0xFF2E7D32)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "PHẢN HỒI MẪU",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = reply.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reply.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ) {
                Text(
                    text = "Trả lời cho bình luận gốc: \"${reply.parentCommentContent}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
