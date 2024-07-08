package io.ylab.mapper;

import io.ylab.dto.BookingDto;
import io.ylab.dto.ChamberDto;
import io.ylab.model.Booking;
import io.ylab.model.Chamber;
import org.mapstruct.factory.Mappers;

public interface ChamberMapper {
    ChamberMapper INSTANCE = Mappers.getMapper(ChamberMapper.class);

    Chamber toEntity(ChamberDto dto);

    ChamberDto toDTO(Chamber entity);
}
