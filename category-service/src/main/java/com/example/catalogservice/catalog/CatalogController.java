package com.example.catalogservice.catalog;

import com.example.catalogservice.repository.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog-service")
@AllArgsConstructor
public class CatalogController {
    Environment env;
    CatalogService catalogService;
    @GetMapping("health_check")
    public String status() {
        return "It's Working in Catalog Service on PORT " + env.getProperty("local.server.port");
    }

    @GetMapping("catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUsers() {

        Iterable<CatalogEntity> userList = catalogService.getAllCatalogs();
        List<ResponseCatalog> result =  new ArrayList<>();

        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}
