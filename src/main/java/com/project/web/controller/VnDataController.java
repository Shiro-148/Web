package com.project.web.Controller;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vn")
public class VnDataController {

    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/provinces")
    public ResponseEntity<List<Map<String, Object>>> getProvinces() {
        try (InputStream in = new ClassPathResource("static/js/vn-wards.json").getInputStream()) {
            List<Map<String, Object>> all = mapper.readValue(in, new TypeReference<List<Map<String, Object>>>() {
            });
            // return provinces only (without full wards) to keep payload small
            List<Map<String, Object>> provinces = all.stream().map(p -> {
                return Map.of(
                        "province_code", p.get("province_code"),
                        "name", p.get("name"));
            }).toList();
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/provinces/{provinceCode}/wards")
    public ResponseEntity<List<Map<String, Object>>> getWards(@PathVariable String provinceCode) {
        try (InputStream in = new ClassPathResource("static/js/vn-wards.json").getInputStream()) {
            List<Map<String, Object>> all = mapper.readValue(in, new TypeReference<List<Map<String, Object>>>() {
            });
            Optional<Map<String, Object>> found = all.stream()
                    .filter(p -> provinceCode.equals(String.valueOf(p.get("province_code")))
                            || provinceCode.equals(String.valueOf(p.get("code"))))
                    .findFirst();
            if (found.isPresent()) {
                Object wards = found.get().get("wards");
                if (wards instanceof List) {
                    return ResponseEntity.ok((List<Map<String, Object>>) wards);
                }
            }
            return ResponseEntity.ok(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
