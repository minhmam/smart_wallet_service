package com.minhpt.smart_wallet_service.config;


import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = "spring", //Mapper trở thành Spring Bean
        unmappedSourcePolicy = ReportingPolicy.IGNORE, //Bỏ qua field chưa map
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE //field null không overwrite
)
public interface MapStructConfig {
}
