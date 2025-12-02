# 🧾 SmartShop Web API 規格書 (v1)

所有 API 的基礎 URL 為：`http://localhost:8080/api`。

---

## 🧾 統一回應格式 (ApiResponse)

所有 API 回傳皆採用統一的 `ApiResponse<T>` JSON 格式：

| 欄位 | 類型 | 說明 |
| :--- | :--- | :--- |
| `status` | `int` | HTTP 狀態碼 (例如：200, 400, 404, 500) |
| `message` | `String` | 執行結果的訊息，例如 "操作成功" 或錯誤描述 |
| `data` | `T` | 實際回傳的資料物件 (例如 `UserDTO` 或 `List<ProductDTO>`)，操作失敗時為 `null` |

**成功範例：**
```json
{
  "status": 200,
  "message": "操作成功",
  "data": { }
}
錯誤範例：JSON{
  "status": 404,
  "message": "找不到商品 ID：99",
  "data": null
}
📡 API 模組一覽 (API Modules)1. 🔑 會員與認證 (AuthController - /api/auth)MethodPath說明認證要求來源POST/register會員註冊 (傳入 RegisterRequestDTO)公開 (無需登入)UserController.javaPOST/login會員登入 (傳入 LoginRequestDTO)公開 (無需登入)UserController.javaGET/logout登出 (清除 Session)公開 (無需登入)UserController.javaGET/me查詢目前登入者資料需登入 (USER/ADMIN)UserController.javaGET/isLoggedIn判斷登入狀態公開 (無需登入)UserController.java2. 🛍️ 商品管理 (ProductController - /api/products)MethodPath說明認證要求GET"", /查詢所有商品 (可選用 keyword, category 篩選)公開 (無需登入)GET/filter整合：搜尋 + 分頁 + 排序 (包含 page, size, sortBy, direction, keyword, category 參數)公開 (無需登入)GET/page商品純分頁查詢 (包含 page, size, sortBy, direction 參數)公開 (無需登入)GET/categories取得所有商品分類列表公開 (無需登入)GET/{id}查詢指定商品 (單筆)公開 (無需登入)POST"", /新增商品 (傳入 ProductDTO)ADMINPUT/{id}修改指定商品 (傳入 ProductDTO)ADMINDELETE/{id}刪除商品ADMIN3. 🛒 購物車 (CartController - /api/cart)MethodPath說明認證要求來源GET"", /取得購物車內容 (回傳 List<CartItemDTO>)需登入 (USER/ADMIN)CartController.javaPOST/add加入購物車 (傳入 productId, qty)需登入 (USER/ADMIN)CartController.javaPUT/update更新購物車商品數量 (傳入 productId, qty)需登入 (USER/ADMIN)CartController.javaDELETE/remove/{productId}移除購物車中的某商品需登入 (USER/ADMIN)CartController.javaDELETE/clear清空購物車需登入 (USER/ADMIN)CartController.java4. 📦 訂單管理 (OrderController - /api/orders)MethodPath說明認證要求來源POST/checkout該用戶 (已登入) 進行結帳 (從購物車建立訂單)需登入 (USER/ADMIN)OrderController.javaGET"", /查詢該用戶 (已登入) 的所有訂單需登入 (USER/ADMIN)OrderController.javaGET/{orderId}查詢單一訂單 (只能查自己的訂單)需登入 (USER/ADMIN)OrderController.javaGET/admin/all查詢所有訂單ADMINREADME.md5. 💰 金流與付款 (PaymentController - /api/payments)MethodPath說明認證要求來源GET/ecpay/{orderId}產生綠界 (ECPay) 付款表單並導向付款頁需登入 (USER/ADMIN)PaymentController.javaPOST/ecpay/notify綠界 (ECPay) 背景通知 (NotifyURL)，用於更新訂單狀態公開 (由綠界呼叫)PaymentController.javaPOST/ecpay/return綠界 (ECPay) 前端回傳頁 (ClientBackURL)公開 (由綠界跳轉)PaymentController.javaGET/history查詢登入者所有付款紀錄需登入 (USER/ADMIN)PaymentController.javaGET/{paymentId}查詢單筆付款明細需登入 (USER/ADMIN)PaymentController.javaPOST/test/pay/{orderId}開發專用：模擬付款成功 (更新訂單狀態為 PAID)需登入 (USER/ADMIN)PaymentController.java6. 🧠 AI 智能顧問 (ChatController & AiLogController - /api/ai)MethodPath說明認證要求來源POST/chat與 AI 購物顧問對話 (傳入 ChatRequestDTO 訊息)需登入 (USER/ADMIN)ChatController.javaGET/logs查詢使用者 AI 使用紀錄列表 (回傳 List<AiLogDTO>)需登入 (USER/ADMIN)AiLogController.javaPOST/logs新增 AI 紀錄 (用於紀錄發問和回覆)需登入 (USER/ADMIN)AiLogController.java7. ❤️ 收藏清單 (FavoriteController - /api/favorites)(此模組在 WEB_API.txt 中為預留/規劃功能，尚未看到對應的 Java 實作檔案，但提供了規格)MethodPath說明認證要求來源GET"", /獲取用戶 (已登入) 的收藏清單需登入 (USER/ADMIN)WEB_API.txtGET/product/{productId}獲取該商品被哪些用戶關注管理者可用 (ADMIN)WEB_API.txtPOST/{productId}用戶 (已登入) 加入收藏商品需登入 (USER/ADMIN)WEB_API.txtDELETE/{productId}用戶 (已登入) 移除收藏商品需登入 (USER/ADMIN)WEB_API.txt
