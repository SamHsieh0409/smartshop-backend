package com.smartshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.ai.OllamaClient;
import com.smartshop.model.dto.ChatRequestDTO;
import com.smartshop.model.dto.ChatResponseDTO;
import com.smartshop.model.dto.ProductDTO;
import com.smartshop.service.AiLogService;
import com.smartshop.service.ChatService;
import com.smartshop.service.ProductService;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private AiLogService aiLogService;

    @Override
    public ChatResponseDTO chat(String username, ChatRequestDTO request) {

        String userMessage = request.getMessage();

        // 1. 用關鍵字搜尋商品（最多取 3 筆）
        List<ProductDTO> candidates = productService.searchProducts(userMessage, null);
        List<ProductDTO> top3 = candidates.stream()
                .limit(3)
                .toList();

        // 2. 組系統提示
        String systemPrompt = """
            你是 SmartShop 的 AI 商品顧問，
            你的任務是根據使用者的需求，推薦最適合的商品，並用口語化的繁體中文回答。

            回答時：
            - 優先説明使用者需求
            - 若有提供商品清單，只能從清單中挑選
            - 回答不要太長，不要廢話
            """;

        // 3. 把商品資訊串成文字給 AI 參考
        StringBuilder productInfo = new StringBuilder();
        for (ProductDTO p : top3) {
            productInfo.append("商品ID: ").append(p.getId())
                    .append("，名稱: ").append(p.getName())
                    .append("，價格: ").append(p.getPrice())
                    .append("，分類: ").append(p.getCategory())
                    .append("\n");
        }

        String aiUserPrompt = """
            使用者的問題：
            %s

            可推薦的商品清單（最多 3 筆）：
            %s

            若清單為空，請告知目前找不到合適商品，可以建議使用者改用關鍵字搜尋。
            若有商品，請說明適合的原因。
            """.formatted(userMessage, productInfo.toString());

        // 4. 呼叫 Ollama
        String aiReply = ollamaClient.ask(systemPrompt, aiUserPrompt);

        // 5. 寫入 AI Log
        aiLogService.addAiLog(username, userMessage, aiReply);

        // 6. 組 ResponseDTO 回給前端
        ChatResponseDTO resp = new ChatResponseDTO();
        resp.setReply(aiReply);
        resp.setProducts(top3);

        return resp;
    }
}
