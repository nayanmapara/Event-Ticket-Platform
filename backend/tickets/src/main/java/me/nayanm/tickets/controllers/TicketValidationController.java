package me.nayanm.tickets.controllers;

import lombok.RequiredArgsConstructor;
import me.nayanm.tickets.domain.dtos.TicketValidationRequestDto;
import me.nayanm.tickets.domain.dtos.TicketValidationResponseDto;
import me.nayanm.tickets.domain.entities.TicketValidation;
import me.nayanm.tickets.domain.entities.TicketValidationMethod;
import me.nayanm.tickets.mappers.TicketValidationMapper;
import me.nayanm.tickets.services.TicketValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket-validations")
@RequiredArgsConstructor
public class TicketValidationController {

    private final TicketValidationService ticketValidationService;
    private final TicketValidationMapper ticketValidationMapper;

    @PostMapping
    public ResponseEntity<TicketValidationResponseDto> validateTicket(
            @RequestBody TicketValidationRequestDto ticketValidationRequestDto
    ){
        TicketValidationMethod method = ticketValidationRequestDto.getMethod();
        TicketValidation ticketValidation;

        if(TicketValidationMethod.MANUAL.equals(method)){
            ticketValidation = ticketValidationService.validateTicketManually(ticketValidationRequestDto.getId());
        } else {
            ticketValidation = ticketValidationService.validateTicketByQrCode(ticketValidationRequestDto.getId());
        }

        return ResponseEntity.ok(
                ticketValidationMapper.toTicketValidationResponseDto(ticketValidation)
        );
    }

}
