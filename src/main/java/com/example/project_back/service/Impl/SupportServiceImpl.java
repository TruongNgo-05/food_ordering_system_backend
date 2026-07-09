package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.SupportStatus;
import com.example.project_back.dto.request.admin.ReplySupportRequest;
import com.example.project_back.dto.request.spec.SupportRequestParam;
import com.example.project_back.dto.request.user.SupportRequest;
import com.example.project_back.dto.response.user.FAQResponse;
import com.example.project_back.dto.response.user.SupportResponse;
import com.example.project_back.entity.FAQ;
import com.example.project_back.entity.SupportTicket;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.SupportMapper;
import com.example.project_back.repository.FAQRepository;
import com.example.project_back.repository.SupportTicketRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.SupportService;
import com.example.project_back.specification.SupportSpecification;
import com.example.project_back.specification.VoucherSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final FAQRepository faqRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    @Override
    public Page<FAQResponse> getFAQs(Pageable pageable) {

        Page<FAQ> faqPage = faqRepository.findAllByOrderByIdAsc(pageable);

        return faqPage.map(faq -> {
            FAQResponse response = new FAQResponse();
            response.setId(faq.getId());
            response.setQuestion(faq.getQuestion());
            response.setAnswer(faq.getAnswer());
            return response;
        });
    }
    @Override
    public void createTicket(SupportRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        SupportTicket ticket = SupportMapper.toEntity(request, user);

        ticket = supportTicketRepository.save(ticket);

        ticket.setSupportCode("SP-" + String.format("%06d", ticket.getId()));

        supportTicketRepository.save(ticket);

    }

    @Override
    public Page<SupportResponse> getMyTickets(Pageable pageable) {

        String username = SecurityUtils.getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy người dùng"));

        Page<SupportTicket> tickets = supportTicketRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        return tickets.map(SupportMapper::toResponse);
    }

    @Override
    public Page<SupportResponse> getAllTickets(SupportRequestParam param , Pageable pageable) {
        String supportCode = param.getSupportCode();
        SupportStatus supportStatus = param.getStatus();
        LocalDateTime maxDate = param.getMaxDate();
        LocalDateTime minDate = param.getMinDate();
        Specification<SupportTicket> spec = Specification.unrestricted();
        if (supportCode != null && !supportCode.isBlank()) {
            spec = spec.and(SupportSpecification.hasSupportCode(supportCode));
        }

        if (supportStatus != null) {
            spec = spec.and(SupportSpecification.hasStatus(param.getStatus()));
        }

        if (maxDate != null && minDate != null) {
            spec = spec.and(SupportSpecification.hasCreateDate(param.getMinDate(), param.getMaxDate()));
        }
        Page<SupportTicket> tickets = supportTicketRepository.findAll(spec, pageable);


        return tickets.map(ticket -> SupportMapper.toResponse(ticket));
    }


    @Override
    public SupportResponse getTicketById(Integer id) {

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy ticket"));

        return SupportMapper.toResponse(ticket);
    }

    @Override
    public void replyTicket(Integer id, ReplySupportRequest request) {

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket"));

        ticket.setReply(request.getReply());
        ticket.setStatus(SupportStatus.REPLIED);

        supportTicketRepository.save(ticket);
    }

    @Override
    public void resolveTicket(Integer id) {

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket"));

        ticket.setStatus(SupportStatus.RESOLVED);

        supportTicketRepository.save(ticket);
    }

    @Override
    public void deleteTicket(Integer id) {

        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket"));

        supportTicketRepository.delete(ticket);
    }
}