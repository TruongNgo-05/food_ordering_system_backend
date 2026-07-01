package com.example.project_back.mapper;

import com.example.project_back.dto.response.admin.TableAdminResponse;
import com.example.project_back.dto.response.user.TableBookResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.entity.TableDetail;
import org.springframework.beans.BeanUtils;

public class TableMapper {

//    demo
    public static TableResponse toTableResponseTest(TableDetail table) {
        TableResponse tableResponse = new TableResponse();
        BeanUtils.copyProperties(table, tableResponse);
        tableResponse.setStatus(table.getStatus().toString());
        return tableResponse;
    }

//    user
    public static TableBookResponse tableBookResponse(TableDetail table) {
        TableBookResponse tableBookResponse = new TableBookResponse();
        BeanUtils.copyProperties(table, tableBookResponse);
        tableBookResponse.setTableId(table.getId());
        return tableBookResponse;
    }



//admin
public static TableAdminResponse toTableAdminResponse(TableDetail table) {
   TableAdminResponse tableAdminResponse = new TableAdminResponse();
    BeanUtils.copyProperties(table, tableAdminResponse);
    return tableAdminResponse;
}


}
