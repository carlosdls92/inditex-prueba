package es.com.inditex.controller;

import es.com.inditex.model.PriceResponse;
import es.com.inditex.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController implements PriceControllerApi{

    private final PriceService priceService;

    @GetMapping("/find-price")
    @Override
    public PriceResponse findPrice(
            @RequestParam("apply-date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applyDate,
            @RequestParam("product-id") Long productId,
            @RequestParam("brand-id") Long brandId
    ) {
        return priceService.findPrice(applyDate, productId, brandId);
    }

}