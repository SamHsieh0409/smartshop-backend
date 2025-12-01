package com.smartshop.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.PaymentDTO;
import com.smartshop.model.entity.Order;
import com.smartshop.model.entity.Payment;
import com.smartshop.model.entity.User;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.PaymentService;
import com.smartshop.util.ECPayUtils;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 從 application.properties 讀取綠界設定
    @Value("${ecpay.merchantId}")
    private String merchantId;

    @Value("${ecpay.hashKey}")
    private String hashKey;

    @Value("${ecpay.hashIV}")
    private String hashIV;

    @Value("${ecpay.payUrl}")
    private String payUrl;

    @Value("${ecpay.clientBackUrl}")
    private String clientBackURL;


    // 1. 產生綠界付款表單(自動跳轉)
    @Override
    public String generateEcpayForm(String username, Long orderId) {

        User user = getUser(username);
        Order order = getOrder(orderId);

        // 建立唯一交易編號
        String tradeNo = "SS" + System.currentTimeMillis();

        // 新增付款紀錄
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setMethod("ECPAY");
        payment.setAmount(order.getTotalAmount());
        payment.setTransactionId(tradeNo);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 綠界要求的時間格式
        String tradeDate = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        // 建立送給綠界的欄位
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", tradeNo);
        params.put("MerchantTradeDate", tradeDate);
        params.put("PaymentType", "aio");
        params.put("TotalAmount", order.getTotalAmount().intValue() + "");
        params.put("TradeDesc", "SmartShop 訂單付款");
        params.put("ItemName", "訂單編號:" + order.getId());

        // 綠界通知（Server）
        params.put("ReturnURL", "http://localhost:8080/api/payments/ecpay/notify");

        // 前端跳回頁面
        params.put("ClientBackURL", clientBackURL + "?orderId=" + orderId);
        // 支付方式（信用卡）
        params.put("ChoosePayment", "Credit");

        // 產生檢查碼
        String checkMacValue = ECPayUtils.generateCheckMacValue(params, hashKey, hashIV);
        params.put("CheckMacValue", checkMacValue);

        // 自動提交的 HTML
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body onload='document.forms[0].submit()'>");
        sb.append("<form method='post' action='").append(payUrl).append("'>");

        for (String key : params.keySet()) {
            sb.append("<input type='hidden' name='")
                    .append(key)
                    .append("' value='")
                    .append(params.get(key))
                    .append("'/>");
        }

        sb.append("</form></body></html>");

        return sb.toString();
    }


    // 2. 處理綠界背景通知（NotifyURL）
    @Override
    public void processEcpayCallback(String merchantTradeNo, int rtnCode, Integer amount) {

        // 只有 rtnCode = 1 代表付款成功
        if (rtnCode != 1) return;

        // 找付款紀錄
        Payment payment = paymentRepository.findByTransactionId(merchantTradeNo)
                .orElseThrow(() -> new RuntimeException("找不到付款紀錄"));

        // 金額驗證（避免被改金額）
        if (payment.getAmount().intValue() != amount.intValue()) {
            throw new RuntimeException("金額不一致，付款拒絕更新");
        }

        // 更新付款成功
        payment.setStatus("SUCCESS");
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // 更新訂單狀態
        Order order = payment.getOrder();
        order.setStatus("PAID");
        orderRepository.save(order);
    }


    // 3. 查詢所有付款紀錄（依使用者）
    @Override
    public List<PaymentDTO> getPaymentHistory(String username) {

        User user = getUser(username);

        return paymentRepository.findByUserId(user.getId())
                .stream()
                .map(p -> modelMapper.map(p, PaymentDTO.class))
                .toList();
    }


    // 4. 查詢單筆付款紀錄
    @Override
    public PaymentDTO getPaymentById(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("付款紀錄不存在"));

        return modelMapper.map(payment, PaymentDTO.class);
    }


    // 內部工具方法
    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
    }
}
