package io.ylab.mapper;

import io.ylab.dto.BookingDto;
import io.ylab.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking toEntity(BookingDto dto);

    BookingDto toDTO(Booking entity);
}
