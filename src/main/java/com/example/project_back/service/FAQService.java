package com.example.project_back.service;

import com.example.project_back.dto.request.admin.FAQCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.FAQAdminResponse;


public interface FAQService {

    FAQAdminResponse createFAQ(FAQCreateAndUpdateRequest request);

    FAQAdminResponse updateFAQ(Integer id, FAQCreateAndUpdateRequest request);

    String deleteFAQ(Integer id);

}