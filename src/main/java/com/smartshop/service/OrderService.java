package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.OrderDTO;

public interface OrderService {

    // 取得使用者全部訂單
    List<OrderDTO> getOrders(String username);

    // 取得單一訂單
    OrderDTO getOrderById(Long orderId);

    // 從購物車建立訂單
    OrderDTO createOrder(String username);

}
