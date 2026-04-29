package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.admin.VoucherCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.VoucherAdminResponse;
import com.example.project_back.entity.Voucher;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.VoucherMapper;
import com.example.project_back.repository.VoucherRepository;
import com.example.project_back.service.VoucherService;
import com.example.project_back.specification.VoucherSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {
        private final VoucherRepository voucherRepository;

    @Override
    public Page<VoucherAdminResponse> getVouchers(VoucherRequestParam param, Pageable pageable){
        String code =  param.getCode();
        LocalDateTime startDate = param.getStartDate();
        LocalDateTime endDate = param.getEndDate();

        Specification<Voucher> spec =Specification.unrestricted();

        if(code!=null && !code.isEmpty()){
            spec=spec.and(VoucherSpecification.hasVoucherCode(code));
        }
        if(startDate!=null &&  endDate!=null){
            spec=spec.and(VoucherSpecification.hasDateVoucher(startDate,endDate));
        }
        return voucherRepository.findAll(spec,pageable).map(VoucherMapper::toVoucherAdminResponse);
    }

    @Override
    public VoucherAdminResponse getVoucherById(Integer id){
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        return VoucherMapper.toVoucherAdminResponse(voucher.get());
    }

    @Transactional
    @Override
    public VoucherAdminResponse createVoucher(VoucherCreateAndUpdateRequest create){
        if(voucherRepository.existsByCode(create.getVoucherCode())){
            throw new ApplicationException("Code already exists");
        }
        Voucher voucher = VoucherMapper.toVoucherAdminCreateResponse(create);
        Voucher saved = voucherRepository.save(voucher);
        return VoucherMapper.toVoucherAdminResponse(saved);
    }

    @Transactional
    @Override
    public VoucherAdminResponse updateVoucher(Integer id, VoucherCreateAndUpdateRequest update){
        Optional<Voucher> vouchers = voucherRepository.findById(id);
        if(vouchers.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        if(voucherRepository.existsByCodeAndIdNot(update.getVoucherCode(), id)){
            throw new ApplicationException("Code already exists");
        }
        Voucher voucher  = vouchers.get();
        VoucherMapper.toVoucherAdminUpdateResponse(update,voucher);
        return VoucherMapper.toVoucherAdminResponse(voucherRepository.save(voucher));
    }

    @Transactional
    @Override
    public String deleteVoucher(Integer id){
        Optional<Voucher> voucher = voucherRepository.findById(id);
        if(voucher.isEmpty()){
            throw new ApplicationException("k tim thay");
        }
        voucherRepository.deleteById(id);
        return "success";
    };
}
