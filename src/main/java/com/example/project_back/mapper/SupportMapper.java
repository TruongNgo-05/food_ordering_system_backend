package com.example.project_back.mapper;

import com.example.project_back.constant.SupportStatus;
import com.example.project_back.dto.request.user.SupportRequest;
import com.example.project_back.dto.response.user.SupportResponse;
import com.example.project_back.entity.SupportTicket;
import com.example.project_back.entity.User;

import java.util.ArrayList;
import java.util.List;

public class SupportMapper {

    private SupportMapper() {
    }

    // Entity -> Response
    public static SupportResponse toResponse(SupportTicket ticket) {

        SupportResponse response = new SupportResponse();

        response.setId(ticket.getId());

        response.setUserId(ticket.getUser().getId());
        response.setFullName(ticket.getUser().getFullName());
        response.setEmail(ticket.getUser().getEmail());

    response.setSupportCode(ticket.getSupportCode());
        response.setSubject(ticket.getSubject());
        response.setMessage(ticket.getMessage());
        response.setReply(ticket.getReply());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());

        return response;
    }

    // Request -> Entity
    public static SupportTicket toEntity(SupportRequest request, User user) {

        SupportTicket ticket = new SupportTicket();

        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());
        ticket.setStatus(SupportStatus.PENDING);

        return ticket;
    }

    // List<Entity> -> List<Response>
    public static List<SupportResponse> toResponseList(List<SupportTicket> tickets) {

        List<SupportResponse> responses = new ArrayList<>();

        for (SupportTicket ticket : tickets) {
            responses.add(toResponse(ticket));
        }

        return responses;
    }
}