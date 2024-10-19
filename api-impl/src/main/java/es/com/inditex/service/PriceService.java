package es.com.inditex.service;

import es.com.inditex.model.PriceResponse;

import java.time.LocalDateTime;

public interface PriceService {
    PriceResponse findPrice(LocalDateTime applyDate, Long productId, Long brandId);
}
