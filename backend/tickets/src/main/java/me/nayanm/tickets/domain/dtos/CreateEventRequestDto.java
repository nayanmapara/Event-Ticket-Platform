package me.nayanm.tickets.domain.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.nayanm.tickets.domain.CreateTicketTypeRequest;
import me.nayanm.tickets.domain.entities.EventStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDateTime start;

    private LocalDateTime end;

    @NotBlank(message = "Venue information is required")
    private String venue;

    private LocalDateTime salesStart;

    private LocalDateTime salesEnd;

    @NotNull(message = "Event status is required")
    private EventStatusEnum status;

    @NotEmpty(message = "At least one ticket type is required")
    @Valid
    private List<CreateTicketTypeRequestDto> ticketTypes;

}
