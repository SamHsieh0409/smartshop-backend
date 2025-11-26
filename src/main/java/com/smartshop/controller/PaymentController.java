package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.PaymentDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.PaymentService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 前端呼叫，產生綠界表單並導向付款頁
    @GetMapping("/ecpay/{orderId}")
    public ResponseEntity<String> ecpayPay(
            @PathVariable Long orderId,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        String html = paymentService.generateEcpayForm(username, orderId);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    // 綠界背景通知
    @PostMapping("/ecpay/notify")
    public String ecpayNotify(
            @RequestParam("MerchantTradeNo") String merchantTradeNo,
            @RequestParam("RtnCode") int rtnCode,
            @RequestParam("TradeAmt") Integer amount) {

        paymentService.processEcpayCallback(merchantTradeNo, rtnCode, amount);
        // 綠界規定：成功要回傳 "1|OK"
        return "1|OK";
    }

    // 前端回傳頁
    @PostMapping("/ecpay/return")
    public ApiResponse<String> ecpayReturn() {
        return new ApiResponse<>(200, "付款流程完成，請回網站查看訂單狀態", null);
    }
    
    // 查詢登入者所有付款紀錄
    @GetMapping("/history")
    public ApiResponse<List<PaymentDTO>> getPaymentHistory(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        return new ApiResponse<>(200, "查詢成功", paymentService.getPaymentHistory(username));
    }


    // 查詢單筆付款明細
    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentDTO> getPayment(@PathVariable Long paymentId) {
        return new ApiResponse<>(200, "查詢成功", paymentService.getPaymentById(paymentId));
    }    
}
