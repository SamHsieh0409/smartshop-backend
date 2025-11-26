package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.OrderDTO;

public interface OrderService {

    OrderDTO checkout(String username);

    List<OrderDTO> getUserOrders(String username);

    OrderDTO getOrderById(Long orderId, String username);  // 加入使用者檢查

}
