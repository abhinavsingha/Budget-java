package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.CgUnit;
import com.sdd.entities.HrData;
import com.sdd.entities.Role;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.response.ApiResponse;
import com.sdd.response.HradataResponse;
import com.sdd.service.MangeRoleService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MangeRoleImpl implements MangeRoleService {

    @Autowired
    RoleRepository roleRepository;


    @Autowired
    CgUnitRepository cgUnitRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    HrDataRepository hrDataRepository;

    @Autowired
    private HeaderUtils headerUtils;

    @Override
    public ApiResponse<List<Role>> getAllRole() {
        String token = headerUtils.getTokeFromHeader();

        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(),"1");
        List<Role> getAllRole = new ArrayList<>();

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        } else {


            HrData hrData = hrDataRepository.findByPidAndIsActive(hrDataCheck.getPid(),"1");
            String cuurentRole = hrData.getRoleId().split(",")[0];
            CgUnit cgUnit = cgUnitRepository.findByUnit(hrData.getUnitId());


            if (cuurentRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
                getAllRole = roleRepository.findByPurposeCode("-1");

            } else if (cuurentRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
                if (cgUnit.getPurposeCode().equalsIgnoreCase("0")) {
                    getAllRole = roleRepository.findByPurposeCodeOrPurposeCode("0", "1");
//                    getAllRole = roleRepository.findByPurposeCode(cgUnit.getPurposeCode());
                } else if (cgUnit.getPurposeCode().equalsIgnoreCase("1")) {
                    getAllRole = roleRepository.findByPurposeCodeOrPurposeCode("0", "1");
//                    getAllRole = roleRepository.findByPurposeCode(cgUnit.getPurposeCode());
                }

            }


        }


        return ResponseUtils.createSuccessResponse(getAllRole, new TypeReference<List<Role>>() {
        });

    }

    @Override
    public ApiResponse<Role> getRoleById(String roleId) {
        String token = headerUtils.getTokeFromHeader();
        if (roleId == null || roleId.isEmpty()) {
            throw new SDDException(HttpStatus.BAD_REQUEST.value(), "ROLE ID CAN NOT BLANK");
        }
        Role getRole = roleRepository.findByRoleId(roleId);
        return ResponseUtils.createSuccessResponse(getRole, new TypeReference<Role>() {
        });
    }
}
