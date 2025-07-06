package me.nayanm.tickets.mappers;

import me.nayanm.tickets.domain.CreateEventRequest;
import me.nayanm.tickets.domain.CreateTicketTypeRequest;
import me.nayanm.tickets.domain.dtos.*;
import me.nayanm.tickets.domain.entities.Event;
import me.nayanm.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventResponseDto toListEventResponseDto(Event event);

}
