package com.project.shopapp.services.chatbot;

import com.project.shopapp.configurations.GeminiConfig;
import com.project.shopapp.dtos.ChatRequest;
import com.project.shopapp.dtos.ChatResponse;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.Product.ProductResponse;
import com.project.shopapp.services.ChatBotService;
import com.project.shopapp.services.Product.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final HttpSession httpSession;
    private final ProductService productService;


    // ✅ Mô tả hệ thống (system instruction)
    private static final String DEFAULT_SYSTEM_INSTRUCTION = """
            Bạn là trợ lý AI của cửa hàng gia dụng Minh Tiến. Chủ cửa hàng tên là Tiến.
            
            QUAN TRỌNG: Bạn CHỈ được trả lời về các sản phẩm có trong cửa hàng gia dụng Minh Tiến.
            
            Nhiệm vụ của bạn:
            - Tư vấn sản phẩm gia dụng có trong cửa hàng
            - Giải đáp thắc mắc về giá cả, tính năng sản phẩm
            - Hỗ trợ khách hàng chọn sản phẩm phù hợp
            - Giới thiệu về cửa hàng gia dụng Minh Tiến
            
            Nếu khách hỏi về:
            - Sản phẩm KHÔNG có trong cửa hàng → Xin lỗi, cửa hàng chúng tôi không có sản phẩm này
            - Chủ đề KHÔNG liên quan → Tôi chỉ có thể tư vấn về sản phẩm gia dụng trong cửa hàng Minh Tiến
            
            Phong cách giao tiếp: Thân thiện, chuyên nghiệp, nhiệt tình hỗ trợ khách hàng.
            """;

    /**
     * Lấy danh sách sản phẩm từ database và format thành context cho AI
     */
    private String getProductContext() {
        try {
            // Lấy các sản phẩm từ session cache trước
            @SuppressWarnings("unchecked")
            String cachedContext = (String) httpSession.getAttribute("productContext");
            if (cachedContext != null) {
                return cachedContext;
            }

            // Nếu chưa có trong cache, query từ database
            PageRequest pageRequest = PageRequest.of(0, 50); // Lấy 50 sản phẩm đầu tiên
            Page<ProductResponse> productsPage = productService.getAllProducts("", null, pageRequest);
            
            StringBuilder contextBuilder = new StringBuilder();
            contextBuilder.append("\n=== DANH SÁCH SẢN PHẨM CỬA HÀNG GIA DỤNG MINH TIẾN ===\n");
            
            for (ProductResponse product : productsPage.getContent()) {
                contextBuilder.append(String.format(
                    "- ID: %d | Tên: %s | Giá: %,.0f VNĐ | Kho: %d | Mô tả: %s | Danh mục: %s\n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getDescription() != null ? product.getDescription() : "Không có mô tả",
                    product.getCategoryId()
                ));
            }
            
            contextBuilder.append("=== HẾT DANH SÁCH SẢN PHẨM ===\n");
            
            String context = contextBuilder.toString();
            // Cache context trong session trong 10 phút
            httpSession.setAttribute("productContext", context);
            
            return context;
        } catch (Exception e) {
            return "\n=== KHÔNG THỂ TẢI DANH SÁCH SẢN PHẨM ===\nVui lòng thử lại sau.\n";
        }
    }

    @Override
    public ChatResponse analyzeDescription(ChatRequest request) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
                + geminiConfig.getGeminiApiKey();

        // Lấy hội thoại từ session (mỗi người dùng sẽ có session riêng)
        @SuppressWarnings("unchecked")
        List<String> conversation = (List<String>) httpSession.getAttribute("conversation");
        if (conversation == null) {
            conversation = new ArrayList<>();
            httpSession.setAttribute("conversation", conversation);
        }

        // Lấy thông tin sản phẩm từ database
        String productContext = getProductContext();
        
        // Gộp ngữ cảnh
        String conversationContext = conversation.stream().collect(Collectors.joining("\n"));
        String promptText = DEFAULT_SYSTEM_INSTRUCTION + "\n" + 
                           productContext + "\n" + 
                           conversationContext + "\n" + 
                           "Người dùng: " + request.getRequest();

        JSONObject prompt = new JSONObject();
        prompt.put("contents", new JSONObject[]{
                new JSONObject().put("parts", new JSONObject[]{
                        new JSONObject().put("text", promptText)
                })
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(prompt.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        JSONObject responseJson = new JSONObject(response.getBody());
        String content = responseJson
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        // Lưu lại hội thoại trong session người dùng
        conversation.add("Người dùng: " + request.getRequest());
        conversation.add("Trợ lý: " + content);

        return new ChatResponse(content, LocalDateTime.now());
    }
}
