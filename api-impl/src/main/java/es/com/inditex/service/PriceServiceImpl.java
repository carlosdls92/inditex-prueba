package es.com.inditex.service;

import es.com.inditex.config.exception.CustomBaseRuntimeException;
import es.com.inditex.entity.Price;
import es.com.inditex.mapper.PriceMapper;
import es.com.inditex.model.PriceResponse;
import es.com.inditex.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceMapper priceMapper;
    private final PriceRepository priceRepository;

    @Override
    public PriceResponse findPrice(LocalDateTime applyDate, Long productId, Long brandId) {
        return priceRepository.findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    brandId,
                    productId,
                    applyDate,
                    applyDate)
                .stream()
                .sorted(Comparator.comparing(Price::getPriority, Comparator.reverseOrder()))
                .map(priceMapper::pricetoPriceResponse)
                .peek(r -> r.setApplyDate(applyDate))
                .findFirst()
                .orElseThrow(()->new CustomBaseRuntimeException("No se encontraron Datos"));
    }
}

