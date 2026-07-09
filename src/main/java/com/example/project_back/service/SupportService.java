package com.example.project_back.service;

import com.example.project_back.dto.request.admin.ReplySupportRequest;
import com.example.project_back.dto.request.spec.SupportRequestParam;
import com.example.project_back.dto.request.user.SupportRequest;
import com.example.project_back.dto.response.user.FAQResponse;
import com.example.project_back.dto.response.user.SupportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupportService {
    Page<FAQResponse> getFAQs(Pageable pageable);

    void createTicket(SupportRequest request);

    Page<SupportResponse> getMyTickets(Pageable pageable);
//ADMIN
Page<SupportResponse> getAllTickets(SupportRequestParam  param, Pageable pageable);

    SupportResponse getTicketById(Integer id);

    void replyTicket(Integer id, ReplySupportRequest request);

    void resolveTicket(Integer id);

    void deleteTicket(Integer id);
}
