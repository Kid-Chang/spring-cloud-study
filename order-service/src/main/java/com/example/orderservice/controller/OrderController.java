package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.messageQueue.KafkaProducer;
import com.example.orderservice.messageQueue.OrderProducer;
import com.example.orderservice.repository.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class OrderController {
    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    OrderProducer orderProducer;

    @GetMapping("health_check")
    public String status() {
        return "It's Working in Order Service on PORT " + env.getProperty("local.server.port");
    }

    @PostMapping("{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,  @RequestBody RequestOrder orderDetails){
        log.info("Before add orders data");
        OrderDto orderDto = new ModelMapper().map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        // jpa
        OrderDto createOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = new ModelMapper().map(createOrder, ResponseOrder.class);

        // kafka
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        // 카프카에 메시지 전송해서 주문 생성 이벤트 발생
        // -> catalog-service에서 해당 상품의 재고를 주문 수량에 맞춰 차감시킬 수 있도록.
        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);

//        ResponseOrder responseOrder = new ModelMapper().map(orderDto, ResponseOrder.class);

        log.info("After add orders data");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable("userId") String userId) throws Exception{
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

//        resilience4j 테스트를 위한 임시 코드
//        try{
//            Thread.sleep(1000);
//            throw new Exception("장애 발생");
//
//        } catch (InterruptedException ex){
//            log.warn(ex.getMessage());
//        }

        log.info("After retrieve orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
