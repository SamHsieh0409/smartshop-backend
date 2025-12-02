package com.smartshop.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

		// --- 第一階段：意圖分析 (Intent Recognition) ---

		String allCategories = "文學小說, 心理成長, 商業理財, 電腦程式, 漫畫輕小說";
		String intentSystemPrompt = """
	            你是一家線上書店的搜尋條件分析師。
	            
	            已知書店目前的書籍分類只有： %s
	            
	            規則：
	            1. CATEGORY: 請盡量將需求歸類到上述 5 個分類中。
	               - 找小說/故事 -> 文學小說
	               - 找勵志/心理/人際 -> 心理成長
	               - 找賺錢/股票/投資 -> 商業理財
	               - 找程式/Coding/軟體 -> 電腦程式
	               - 找動漫/輕小說 -> 漫畫輕小說
	               - 若完全不相關，填 null。
	            
	            2. KEYWORD (重要！): 
	               - 只提取「書名、作者、特定主題」的關鍵字（如：哈利波特、原子、Java）。
	               - 禁止提取泛用詞（如：書、書籍、商品、推薦、好看的、有趣的、便宜的、貴的）。
	               - 如果使用者只說了分類或形容詞，KEYWORD 請填 null。
	            
	            3. SORT: 
	               - 便宜/特價/預算低 -> price_asc
	               - 貴/典藏/高級 -> price_desc
	               - 最新/新書 -> newest
	               - 其他 -> null
	            
	            輸出格式要求：
	            KEYWORD: [關鍵字]
	            CATEGORY: [分類]
	            SORT: [排序]
	            """.formatted(allCategories);

		String intentUserPrompt = "使用者輸入：" + userMessage;

		// 呼叫 AI 取得分析結果
		String analysisResult = ollamaClient.ask(intentSystemPrompt, intentUserPrompt);
		System.out.println("DEBUG: AI 分析結果 -> \n" + analysisResult);

		// 解析 AI 回傳的文字
		String keyword = extractValue(analysisResult, "KEYWORD");
		String category = extractValue(analysisResult, "CATEGORY");
		String sortRaw = extractValue(analysisResult, "SORT");

		// 轉換排序參數給 ProductService 使用
		String sortBy = "id";
		String direction = "asc";

		if ("price_asc".equals(sortRaw)) {
			sortBy = "price";
			direction = "asc";
		} else if ("price_desc".equals(sortRaw)) {
			sortBy = "price";
			direction = "desc";
		} else if ("newest".equals(sortRaw)) {
			sortBy = "createdAt";
			direction = "desc";
		}

		// --- 第二階段：精準搜尋資料庫 ---
		// 使用 filterProducts 功能
		// 為了避免 keyword 是 "null" 字串導致搜尋失敗，做個轉換
		String searchKeyword = (keyword == null || keyword.equalsIgnoreCase("null") || keyword.isBlank()) ? null
				: keyword;
		String searchCategory = (category == null || category.equalsIgnoreCase("null") || category.isBlank()) ? null
				: category;

		Page<ProductDTO> productPage = productService.filterProducts(0, 3, sortBy, direction, searchKeyword,
				searchCategory);
		List<ProductDTO> top3 = productPage.getContent();

		// --- 第三階段：生成最終回覆 ---
		String chatSystemPrompt = """
				你是 SmartShop 書店的 AI 閱讀顧問。
				請根據搜尋到的書籍清單回答使用者的問題。

				規則：
				1. 語氣要充滿書卷氣、親切。
				2. 推薦書籍時，可以稍微帶入該書的適合族群（例如：這本適合初學者）。
				3. 如果找不到書，請委婉建議換個關鍵字（例如：試試看『理財』或『小說』）。
				4. 回答盡量簡短。
				""";

		StringBuilder productInfo = new StringBuilder();
		if (top3.isEmpty()) {
			productInfo.append("(目前沒有符合條件的商品)");
		} else {
			for (ProductDTO p : top3) {
				productInfo
						.append(String.format("- 商品：%s | 價格：%s | 分類：%s\n", p.getName(), p.getPrice(), p.getCategory()));
			}
		}

		String chatUserPrompt = String.format("""
				使用者問題：%s

				AI 分析的搜尋條件：關鍵字=%s, 排序=%s

				目前資料庫搜尋結果：
				%s
				""", userMessage, searchKeyword, sortRaw, productInfo.toString());

		// 呼叫 AI 生成最終對話
		String aiReply = ollamaClient.ask(chatSystemPrompt, chatUserPrompt);

		// 紀錄 Log
		aiLogService.addAiLog(username, userMessage, aiReply);

		ChatResponseDTO resp = new ChatResponseDTO();
		resp.setReply(aiReply);
		resp.setProducts(top3);

		return resp;
	}

	// 小工具：用正規表示式抓取 KEYWORD: xxx 這種格式的值
	private String extractValue(String text, String key) {
		try {
			Pattern pattern = Pattern.compile(key + "\\s*[:：]\\s*(.+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				String val = matcher.group(1).trim();
				if ("null".equalsIgnoreCase(val))
					return null;
				return val;
			}
		} catch (Exception e) {
			// 解析失敗就算了
		}
		return null;
	}
}