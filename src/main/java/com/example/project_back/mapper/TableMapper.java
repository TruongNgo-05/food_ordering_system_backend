package com.example.project_back.mapper;

import com.example.project_back.dto.response.admin.TableAdminResponse;
import com.example.project_back.dto.response.user.TableBookResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.entity.TableDetail;
import org.springframework.beans.BeanUtils;

public class TableMapper {

//    demo
    public static TableResponse toTableResponse(TableDetail table) {
        TableResponse tableResponse = new TableResponse();
        BeanUtils.copyProperties(table, tableResponse);
        tableResponse.setStatus(table.getStatus().toString());
        return tableResponse;
    }

//    public static TableBookResponse toTableBookResponse(TableDetail table) {
//        TableBookResponse tableBookResponse = new TableBookResponse();
//        tableBookResponse.setTableDetail(toTableResponse(table));
//        tableBookResponse.setStatus(table.getStatus().toString());
//        return tableBookResponse;
//    }

//admin
public static TableAdminResponse toTableAdminResponse(TableDetail table) {
   TableAdminResponse tableAdminResponse = new TableAdminResponse();
    BeanUtils.copyProperties(table, tableAdminResponse);
    tableAdminResponse.setStatus(table.getStatus().toString());
    return tableAdminResponse;
}

}
