package me.nayanm.tickets.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.nayanm.tickets.domain.entities.TicketValidationMethod;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequestDto {

    private UUID id;
    private TicketValidationMethod method;

}
