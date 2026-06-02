package com.example.project_back.mapper;

import com.example.project_back.dto.response.user.TableDetailResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.entity.TableDetail;
import org.springframework.beans.BeanUtils;

public class TableMapper {
    public static TableResponse toTableResponse(TableDetail table) {
        TableResponse tableResponse = new TableResponse();
        BeanUtils.copyProperties(table, tableResponse);
        tableResponse.setQrCode(table.getQrCode());
        return tableResponse;
    }

    public static TableDetailResponse  toTableDetailResponse(TableDetail tableDetail) {
        TableDetailResponse tableDetailResponse = new TableDetailResponse();
        tableDetailResponse.setTableDetail(toTableResponse(tableDetail));
        tableDetailResponse.setQrCode(tableDetail.getQrCode());
        return tableDetailResponse;
    }



}
