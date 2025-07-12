package me.nayanm.tickets.mappers;

import me.nayanm.tickets.domain.CreateEventRequest;
import me.nayanm.tickets.domain.CreateTicketTypeRequest;
import me.nayanm.tickets.domain.UpdateEventRequest;
import me.nayanm.tickets.domain.UpdateTicketTypeRequest;
import me.nayanm.tickets.domain.dtos.CreateEventRequestDto;
import me.nayanm.tickets.domain.dtos.CreateEventResponseDto;
import me.nayanm.tickets.domain.dtos.CreateTicketTypeRequestDto;
import me.nayanm.tickets.domain.dtos.GetEventDetailsResponseDto;
import me.nayanm.tickets.domain.dtos.GetEventDetailsTicketTypesResponseDto;
import me.nayanm.tickets.domain.dtos.GetPublishedEventDetailsResponseDto;
import me.nayanm.tickets.domain.dtos.GetPublishedEventDetailsTicketTypesResponseDto;
import me.nayanm.tickets.domain.dtos.ListEventResponseDto;
import me.nayanm.tickets.domain.dtos.ListEventTicketTypeResponseDto;
import me.nayanm.tickets.domain.dtos.ListPublishedEventResponseDto;
import me.nayanm.tickets.domain.dtos.UpdateEventRequestDto;
import me.nayanm.tickets.domain.dtos.UpdateEventResponseDto;
import me.nayanm.tickets.domain.dtos.UpdateTicketTypeRequestDto;
import me.nayanm.tickets.domain.dtos.UpdateTicketTypeResponseDto;
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

    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(TicketType ticketType);

    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);

    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    GetPublishedEventDetailsTicketTypesResponseDto toGetPublishedEventDetailsTicketTypesResponseDto(TicketType ticketType);

    GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
}
