package me.nayanm.tickets.mappers;

import me.nayanm.tickets.domain.CreateEventRequest;
import me.nayanm.tickets.domain.CreateTicketTypeRequest;
import me.nayanm.tickets.domain.dtos.CreateEventRequestDto;
import me.nayanm.tickets.domain.dtos.CreateEventResponseDto;
import me.nayanm.tickets.domain.dtos.CreateTicketTypeRequestDto;
import me.nayanm.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

}
