package com.example.nikonbe.api.client.promotion;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/promotions")
@RequiredArgsConstructor
@Tag(name = "Client - Promotion", description = "Các API khuyến mãi dành cho khách hàng")
public class PromotionController {}
