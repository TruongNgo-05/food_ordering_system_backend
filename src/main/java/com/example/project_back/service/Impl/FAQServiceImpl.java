package com.example.project_back.service.Impl;


import com.example.project_back.dto.request.admin.FAQCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.FAQAdminResponse;
import com.example.project_back.entity.FAQ;
import com.example.project_back.repository.FAQRepository;
import com.example.project_back.service.FAQService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;

    @Override
    public FAQAdminResponse createFAQ(
            FAQCreateAndUpdateRequest request
    ) {
        FAQ faq = new FAQ();

        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        FAQ saved = faqRepository.save(faq);
        return convertToResponse(saved);

    }

    @Override
    public FAQAdminResponse updateFAQ(
            Integer id,
            FAQCreateAndUpdateRequest request
    ) {


        FAQ faq = faqRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("FAQ not found")
                );


        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());



        FAQ updated = faqRepository.save(faq);


        return convertToResponse(updated);

    }





    @Override
    public String deleteFAQ(Integer id) {


        FAQ faq = faqRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("FAQ not found")
                );


        faqRepository.delete(faq);


        return "Delete FAQ successfully";

    }





    private FAQAdminResponse convertToResponse(FAQ faq){

        return FAQAdminResponse.builder()

                .id(faq.getId())

                .question(faq.getQuestion())

                .answer(faq.getAnswer())

                .createdAt(faq.getCreatedAt())

                .updatedAt(faq.getUpdatedAt())

                .build();

    }


}