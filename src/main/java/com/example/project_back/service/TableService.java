package com.example.project_back.service;

import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.request.user.table.CreateAndUpdateTableRequest;
import com.example.project_back.dto.request.user.table.DinnerSetTableRequest;
import com.example.project_back.dto.response.user.MenuTableResponse;
import com.example.project_back.dto.response.user.TableDetailResponse;
import com.example.project_back.dto.response.user.TableResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TableService {
    List<TableResponse> getListTables();

    TableResponse createTable(CreateAndUpdateTableRequest create);

    TableResponse updateTable(CreateAndUpdateTableRequest update, Integer id);

    TableDetailResponse tableDetail(Integer id);

    String deleteTable(Integer id);

    MenuTableResponse getMenuByTable(String tableNumber);

    TableResponse dinnerSet(DinnerSetTableRequest dinnerSetTableRequest);
}
