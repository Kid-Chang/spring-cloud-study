package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service") // 로드밸런싱을 위해 order-service로 변경
public interface OrderServiceClient {

    @GetMapping("/{userId}/orders")
    List<ResponseOrder>  getOrders(@PathVariable String userId);

}
