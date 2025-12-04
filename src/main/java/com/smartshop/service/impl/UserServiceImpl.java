package com.smartshop.service.impl;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.LoginRequestDTO;
import com.smartshop.model.dto.RegisterRequestDTO;
import com.smartshop.model.dto.UserDTO;
import com.smartshop.model.entity.AiLog;
import com.smartshop.model.entity.Cart;
import com.smartshop.model.entity.User;
import com.smartshop.repository.AiLogRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiLogRepository aiLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    


    @Override
    public UserDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("使用者已存在");
        }

        User user = new User();
        modelMapper.map(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        return convertToUserDTO(userRepository.save(user));
    }


    @Override
    public UserDTO login(LoginRequestDTO request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("帳號不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        return convertToUserDTO(user);
    }


    @Override
    public UserDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));
        return convertToUserDTO(user);
    }


    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    
  //User → UserDTO
    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        
        Cart cart = user.getCart();
        int itemCount = 0;
        
        if (cart != null && cart.getCartItems() != null) {
        	itemCount = cart.getCartItems().stream()
        			.mapToInt(item -> item.getQty())
        			.sum();
        }
        
        dto.setCartItemCount(itemCount);

        return dto;
    }
}