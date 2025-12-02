
# 🧾 SmartShop 介紹

所有 API 的基礎 URL 為：`http://localhost:8080/api`

## ✨ 功能特色 (Features)

* **📡 RESTful API**：完整的商品、購物車、訂單、會員認證 API。
* **🔒 安全性**：
  * Spring Security 配置。
  * 支援密碼加密 (BCrypt) 與 Session 管理。
  * CORS 全域配置 (支援前端開發環境與 Ngrok 測試)。
* **🧠 AI 整合 (Ollama)**：
  * 整合 **Ollama** 本地模型 (`qwen2:4b` 或 `qwen3:4b`)。
  * **兩階段意圖分析**：AI 自動分析使用者語句中的「關鍵字」、「分類」與「排序需求」，再進行精準資料庫搜尋。
* **💾 資料庫**：使用 MySQL 與 Spring Data JPA 進行資料持久化。
* **💰 金流邏輯**：
  * 內建綠界 (ECPay) 金流串接邏輯 (產生表單、檢查碼 CheckMacValue 計算)。(努力中)
  * 支援模擬付款 API (方便本機開發測試)。

## 🛠️ 技術棧 (Tech Stack)

* **語言**：Java 21
* **框架**：Spring Boot 3.5.7, Spring Data JPA, Spring Security
* **資料庫**：MySQL
* **工具**：Lombok, ModelMapper, Gson
* **AI 客戶端**：OkHttp (連線至本地 Ollama 服務)

## API 模組一覽 (Modules)

1. 🔑 會員與認證 (AuthController) — 路徑前綴：/api/auth

| Method | Path         | 說明                                 | 認證要求            | 來源 |
|:------:|:------------:|:-------------------------------------|:-------------------:|:----:|
| POST   | /register    | 會員註冊 (傳入 RegisterRequestDTO)    | 公開 (無需登入)     | UserController.java |
| POST   | /login       | 會員登入 (傳入 LoginRequestDTO)       | 公開 (無需登入)     | UserController.java |
| GET    | /logout      | 登出 (清除 Session)                   | 公開 (無需登入)     | UserController.java |
| GET    | /me          | 查詢目前登入者資料                     | 需登入 (USER/ADMIN) | UserController.java |
| GET    | /isLoggedIn  | 判斷登入狀態                           | 公開 (無需登入)     | UserController.java |

---

2. 🛍️ 商品管理 (ProductController) — 路徑前綴：/api/products

| Method | Path            | 說明                                                                 | 認證要求            | 來源 |
|:------:|:---------------:|:---------------------------------------------------------------------|:-------------------:|:----:|
| GET    | / or ""         | 查詢所有商品 (可選用 keyword, category 篩選)                          | 公開 (無需登入)     | ProductController.java |
| GET    | /filter         | 整合：搜尋 + 分頁 + 排序 (包含 page, size, sortBy, direction, keyword, category 參數) | 公開 (無需登入)     | ProductController.java |
| GET    | /page           | 商品純分頁查詢 (包含 page, size, sortBy, direction 參數)              | 公開 (無需登入)     | ProductController.java |
| GET    | /categories     | 取得所有商品分類列表                                                  | 公開 (無需登入)     | ProductController.java |
| GET    | /{id}           | 查詢指定商品 (單筆)                                                   | 公開 (無需登入)     | ProductController.java |
| POST   | / or ""         | 新增商品 (傳入 ProductDTO)                                            | ADMIN               | ProductController.java |
| PUT    | /{id}           | 修改指定商品 (傳入 ProductDTO)                                        | ADMIN               | ProductController.java |
| DELETE | /{id}           | 刪除商品                                                              | ADMIN               | ProductController.java |

---

3. 🛒 購物車 (CartController) — 路徑前綴：/api/cart

| Method | Path                    | 說明                                    | 認證要求            | 來源 |
|:------:|:-----------------------:|:----------------------------------------|:-------------------:|:----:|
| GET    | / or ""                 | 取得購物車內容 (回傳 List<CartItemDTO>)   | 需登入 (USER/ADMIN) | CartController.java |
| POST   | /add                    | 加入購物車 (傳入 productId, qty)          | 需登入 (USER/ADMIN) | CartController.java |
| PUT    | /update                 | 更新購物車商品數量 (傳入 productId, qty)  | 需登入 (USER/ADMIN) | CartController.java |
| DELETE | /remove/{productId}     | 移除購物車中的某商品                      | 需登入 (USER/ADMIN) | CartController.java |
| DELETE | /clear                  | 清空購物車                              | 需登入 (USER/ADMIN) | CartController.java |

---

4. 📦 訂單管理 (OrderController) — 路徑前綴：/api/orders

| Method | Path            | 說明                                                                 | 認證要求            | 來源 |
|:------:|:---------------:|:---------------------------------------------------------------------|:-------------------:|:----:|
| POST   | /checkout       | 該用戶 (已登入) 進行結帳 (從購物車建立訂單)                          | 需登入 (USER/ADMIN) | OrderController.java |
| GET    | / or ""         | 查詢該用戶 (已登入) 的所有訂單                                        | 需登入 (USER/ADMIN) | OrderController.java |
| GET    | /{orderId}      | 查詢單一訂單 (只能查自己的訂單)                                       | 需登入 (USER/ADMIN) | OrderController.java |
| GET    | /admin/all      | 查詢所有訂單                                                          | ADMIN               | OrderController.java |

---

5. 💰 金流與付款 (PaymentController) — 路徑前綴：/api/payments (前端努力中)

| Method | Path                      | 說明                                                                 | 認證要求            | 來源 |
|:------:|:-------------------------:|:---------------------------------------------------------------------|:-------------------:|:----:|
| GET    | /ecpay/{orderId}          | 產生綠界 (ECPay) 付款表單並導向付款頁                                | 需登入 (USER/ADMIN) | PaymentController.java |
| POST   | /ecpay/notify             | 綠界 (ECPay) 背景通知 (NotifyURL)，用於更新訂單狀態                  | 公開 (由綠界呼叫)   | PaymentController.java |
| POST   | /ecpay/return             | 綠界 (ECPay) 前端回傳頁 (ClientBackURL)                               | 公開 (由綠界跳轉)   | PaymentController.java |
| GET    | /history                  | 查詢登入者所有付款紀錄                                               | 需登入 (USER/ADMIN) | PaymentController.java |
| GET    | /{paymentId}              | 查詢單筆付款明細                                                     | 需登入 (USER/ADMIN) | PaymentController.java |
| POST   | /test/pay/{orderId}       | 開發專用：模擬付款成功 (更新訂單狀態為 PAID)                          | 需登入 (USER/ADMIN) | PaymentController.java |

---

6. 🧠 AI 智能顧問 (ChatController & AiLogController) — 路徑前綴：/api/ai

| Method | Path   | 說明                                                | 認證要求            | 來源 |
|:------:|:------:|:----------------------------------------------------|:-------------------:|:----:|
| POST   | /chat  | 與 AI 購物顧問對話 (傳入 ChatRequestDTO 訊息)        | 需登入 (USER/ADMIN) | ChatController.java |
| GET    | /logs  | 查詢使用者 AI 使用紀錄列表 (回傳 List<AiLogDTO>)     | 需登入 (USER/ADMIN) | AiLogController.java |
| POST   | /logs  | 新增 AI 紀錄 (用於紀錄發問和回覆)                    | 需登入 (USER/ADMIN) | AiLogController.java |

---

## 統一回應格式 (ApiResponse)

所有 API 回傳皆採用統一的 `ApiResponse<T>` JSON 格式：

| 欄位     | 類型    | 說明                                                                 |
|---------:|--------:|----------------------------------------------------------------------|
| status   | int     | HTTP 狀態碼 (例如：200, 400, 404, 500)                                 |
| message  | String  | 執行結果的訊息，例如 "操作成功" 或錯誤描述                             |
| data     | T       | 實際回傳的資料物件 (例如 `UserDTO` 或 `List<ProductDTO>`)，操作失敗時為 `null` |

範例 — 成功：
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {}
}
```

範例 — 錯誤：
```json
{
  "status": 404,
  "message": "找不到商品 ID：99",
  "data": null
}
```

---


## 錯誤與例外處理建議

- 依情況回傳合適的 HTTP 狀態碼（400、401、404、500 等）。
- 在 `ApiResponse.message` 中提供使用者友善的錯誤描述，並在伺服器日誌中記錄詳細錯誤堆疊。
- 對於驗證失敗使用 401，資料不存在使用 404，參數錯誤使用 400。

---
