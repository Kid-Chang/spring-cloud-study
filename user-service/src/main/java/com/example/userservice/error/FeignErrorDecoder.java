package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@AllArgsConstructor
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    Environment env;
    @Override
    public Exception decode(String methodKey, Response response) {
        log.info("FeignErrorDecoder methodKey: {}", methodKey);
        log.info("FeignErrorDecoder status: {}", response.status());
        switch (response.status()){
            case 400:
                break;
            case 404:
                // 원래는 500번대 에러가 발생했지만, 이 에러를 404로 변경
                if(methodKey.contains("getOrders")){
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                        env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}
