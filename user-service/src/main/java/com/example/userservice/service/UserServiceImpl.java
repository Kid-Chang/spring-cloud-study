package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.error.FeignErrorDecoder;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    Environment env;
//    RestTemplate restTemplate;
    OrderServiceClient orderServiceClient;

    // Bean에 등록했기때문에 여기서 호출할 필요가 없다.
    // FeignErrorDecoder feignErrorDecoder;

    CircuitBreakerFactory circuitBreakerFactory;

    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPwd()));
        userRepository.save(userEntity);

        UserDto returnUserDto = modelMapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity =  userRepository.findByUserId(userId);

        if(userEntity == null) {
            throw new RuntimeException("User not found");
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        List<ResponseOrder> ordersList = new ArrayList<>();
        /* Using as rest template */
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                                            new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });
//        // ParameterizedTypeReference<List<ResponseOrder>>에서 ParameterizedTypeReference는 특정 타입 받겠다고 선언, List<ResponseOrder>는 orders api에서 받을 타입
//        ordersList = orderListResponse.getBody();

        /* Using as FeignClient */
//        try {
//            ordersList = orderServiceClient.getOrders(userId);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        /* Using as FeignClient with ErrorDecoder */
//        ordersList = orderServiceClient.getOrders(userId);

        /* Using as FeignClient with CircuitBreaker */
        log.info("Before call orders microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        // CircuitBreaker가 run 했을때 getOrders가 정상작동하면 해당 값을 담고 오류가 발생하면 빈 리스트를 반환한다.
        ordersList = circuitBreaker.run(() ->
            orderServiceClient.getOrders(userId), throwable -> new ArrayList<>()
        );
        log.info("After call orders microservice");
        userDto.setOrders(ordersList);

        return userDto;
    }

    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);
        if(userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true,
            true, true, true, new ArrayList<>());
    }

    public UserDto getUserDetailsByEmail(String userName) {
        UserEntity userEntity = userRepository.findByEmail(userName);
        if(userEntity == null) {
            throw new UsernameNotFoundException(userName);
        }
        return new ModelMapper().map(userEntity, UserDto.class);
    }
}
