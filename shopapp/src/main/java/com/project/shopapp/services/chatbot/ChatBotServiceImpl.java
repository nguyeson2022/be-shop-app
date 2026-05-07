package com.project.shopapp.services.chatbot;

import com.project.shopapp.configurations.GeminiConfig;
import com.project.shopapp.dtos.ChatRequest;
import com.project.shopapp.dtos.ChatResponse;
import com.project.shopapp.responses.Product.ProductResponse;
import com.project.shopapp.services.ChatBotService;
import com.project.shopapp.services.Product.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
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

    private static final int MAX_MENTIONED_PRODUCTS = 6;

    private static final String DEFAULT_SYSTEM_INSTRUCTION = """
            Bạn là trợ lý AI của cửa hàng gia dụng Sonnguyen. Chủ cửa hàng tên là Sơn.

            QUAN TRỌNG: Bạn CHỈ được trả lời về các sản phẩm có trong cửa hàng gia dụng Sonnguyen.

            Nhiệm vụ của bạn:
            - Tư vấn sản phẩm gia dụng có trong cửa hàng
            - Giải đáp thắc mắc về giá cả, tính năng sản phẩm
            - Hỗ trợ khách hàng chọn sản phẩm phù hợp
            - Giới thiệu về cửa hàng gia dụng Sonnguyen

            Nếu khách hỏi về:
            - Sản phẩm KHÔNG có trong cửa hàng → Xin lỗi, cửa hàng chúng tôi không có sản phẩm này
            - Chủ đề KHÔNG liên quan → Tôi chỉ có thể tư vấn về sản phẩm gia dụng trong cửa hàng Sonnguyen

            Phong cách: Thân thiện, chuyên nghiệp.
            """;

    private String getProductContext() {
        try {
            String cached = (String) httpSession.getAttribute("productContext");
            if (cached != null)
                return cached;

            PageRequest pageRequest = PageRequest.of(0, 50);
            Page<ProductResponse> products = productService.getAllProducts("", null, pageRequest);

            StringBuilder sb = new StringBuilder();
            sb.append("\n=== DANH SÁCH SẢN PHẨM ===\n");

            for (ProductResponse p : products.getContent()) {
                sb.append(String.format(
                        "- %s | Giá: %,.0f | Kho: %d | Mô tả: %s\n",
                        p.getName(),
                        p.getPrice(),
                        p.getStock(),
                        p.getDescription() != null ? p.getDescription() : "Không có"));
            }

            String context = sb.toString();
            httpSession.setAttribute("productContext", context);
            return context;

        } catch (Exception e) {
            return "Không load được sản phẩm.";
        }
    }

    private List<ProductResponse> findMentionedProducts(String reply) {
        List<ProductResponse> mentioned = new ArrayList<>();
        if (reply == null || reply.isBlank()) {
            return mentioned;
        }
        try {
            PageRequest pageRequest = PageRequest.of(0, 100);
            Page<ProductResponse> products = productService.getAllProducts("", null, pageRequest);
            String lowerReply = reply.toLowerCase();

            for (ProductResponse p : products.getContent()) {
                if (p.getName() == null || p.getName().isBlank())
                    continue;
                if (lowerReply.contains(p.getName().toLowerCase())) {
                    mentioned.add(p);
                    if (mentioned.size() >= MAX_MENTIONED_PRODUCTS)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mentioned;
    }

    @Override
    public ChatResponse analyzeDescription(ChatRequest request) {

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                + geminiConfig.getGeminiApiKey();

        System.out.println("Calling Gemini API: " + url);

        // Conversation session
        List<String> conversation = (List<String>) httpSession.getAttribute("conversation");
        if (conversation == null) {
            conversation = new ArrayList<>();
            httpSession.setAttribute("conversation", conversation);
        }

        String productContext = getProductContext();
        String history = String.join("\n", conversation);

        String promptText = DEFAULT_SYSTEM_INSTRUCTION + "\n"
                + productContext + "\n"
                + history + "\n"
                + "Người dùng: " + request.getRequest();

        // ✅ JSON chuẩn
        JSONObject part = new JSONObject();
        part.put("text", promptText);

        JSONObject content = new JSONObject();
        content.put("parts", new JSONArray().put(part));

        JSONObject body = new JSONObject();
        body.put("contents", new JSONArray().put(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject json = new JSONObject(response.getBody());

            // ✅ Check an toàn
            if (!json.has("candidates")) {
                return new ChatResponse("AI không phản hồi.", LocalDateTime.now());
            }

            String reply = json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            conversation.add("User: " + request.getRequest());
            conversation.add("AI: " + reply);

            ChatResponse chatResponse = new ChatResponse(reply, LocalDateTime.now());
            chatResponse.setProducts(findMentionedProducts(reply));
            return chatResponse;

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            return new ChatResponse("Quá hạn mức Gemini (429). Đợi chút nhé.", LocalDateTime.now());

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("Gemini Error: " + e.getResponseBodyAsString());
            return new ChatResponse("Lỗi Gemini: " + e.getStatusCode(), LocalDateTime.now());

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Lỗi hệ thống: " + e.getMessage(), LocalDateTime.now());
        }
    }
}
