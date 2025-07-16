package me.nayanm.tickets.mappers;

import me.nayanm.tickets.domain.dtos.ListTicketResponseDto;
import me.nayanm.tickets.domain.dtos.ListTicketTicketTypeResponseDto;
import me.nayanm.tickets.domain.entities.Ticket;
import me.nayanm.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    ListTicketTicketTypeResponseDto toListTicketTicketTypeResponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

}
