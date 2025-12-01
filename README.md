SmartShop 的後端 RESTful API 服務，基於 **Spring Boot 3** 開發。提供使用者認證、書籍管理、訂單處理、金流邏輯以及整合 **Ollama** 的在地端 AI 服務。

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
  * 內建綠界 (ECPay) 金流串接邏輯 (產生表單、檢查碼 CheckMacValue 計算)。
  * 支援模擬付款 API (方便本機開發測試)。

## 🛠️ 技術棧 (Tech Stack)

* **語言**：Java 21
* **框架**：Spring Boot 3.5.7, Spring Data JPA, Spring Security
* **資料庫**：MySQL
* **工具**：Lombok, ModelMapper, Gson
* **AI 客戶端**：OkHttp (連線至本地 Ollama 服務)

## API 概覽
POST,/api/auth/login,會員登入,公開
POST,/api/auth/register,會員註冊,公開
GET,/api/products/filter,搜尋與篩選商品,公開
POST,/api/ai/chat,與 AI 購物顧問對話,需登入
POST,/api/orders/checkout,購物車結帳,需登入
POST,/api/payments/test/pay/{id},模擬付款成功 (開發用),需登入
GET,/api/orders/admin/all,查詢所有訂單,ADMIN
