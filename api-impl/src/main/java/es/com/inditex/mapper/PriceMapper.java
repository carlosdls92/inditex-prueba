package es.com.inditex.mapper;

import es.com.inditex.entity.Price;
import es.com.inditex.model.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    @Mapping(target = "priceToApply", source = "priceList")
    @Mapping(target = "priceFinal", source = "price")
    @Mapping(target = "product", source = "product.name")
    @Mapping(target = "brand", source = "brand.name")
    PriceResponse pricetoPriceResponse(Price price);
}
