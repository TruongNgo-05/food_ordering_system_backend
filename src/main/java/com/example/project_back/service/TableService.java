package com.example.project_back.service;

import com.example.project_back.dto.request.admin.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.BookTableRequest;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableDetailResponse;
import com.example.project_back.dto.response.user.TableResponse;

import java.util.List;

public interface TableService {
    List<TableResponse> getListTables();

    TableResponse createTable(CreateAndUpdateTableRequest create);

    TableResponse updateTable(CreateAndUpdateTableRequest update, Integer id);

    TableDetailResponse tableDetail(Integer id);

    String deleteTable(Integer id);

    MenuTableResponse getMenuByTable(String tableNumber);

    TableResponse dinnerSet(BookTableRequest bookTableRequest);
}
