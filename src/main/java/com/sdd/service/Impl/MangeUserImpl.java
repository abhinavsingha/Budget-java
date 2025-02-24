package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.*;
import com.sdd.entities.repository.*;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.response.*;
import com.sdd.service.MangeUserService;
import com.sdd.utils.ConverterUtils;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MangeUserImpl implements MangeUserService {

    @Autowired    
    RoleRepository roleRepository;
    @Autowired
    HrcodeRankRepository hrcodeRankRepository;
    @Autowired    
    CgUnitRepository cgUnitRepository;
    @Autowired
    Hr_DataRepository hr_DataRepository;
    @Autowired    
    HrDataRepository hrDataRepository;

    @Autowired
    CgStationRepository cgStationRepository;

    @Autowired    
    private JwtUtils jwtUtils;

    @Autowired    
    private HeaderUtils headerUtils;

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> addUser(HrData hrData) {
//        HrData hrData1=hrData;
        boolean flag=false;
        if(hrData.getRoleId().equalsIgnoreCase(HelperUtils.UNITADMIN)) {
            flag = true;
        }
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLE ID CAN NOT BE BLANK");
        }


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        } else {


            int newRoleId = Integer.parseInt(hrData.getRoleId());

            String[] testArray = hrDataCheck.getRoleId().split(",");
            int max = 0;
            for (int i = 0; i < testArray.length; i++) {
                if (Integer.parseInt(testArray[i].trim()) > max) {
                    max = Integer.parseInt(testArray[i]);
                }
            }
            if (newRoleId >= max) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE THIS TASK");
            }

        }
        DefaultResponse defaultResponse = new DefaultResponse();

        if (hrData.getUnitId() == null || hrData.getUnitId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT ID CAN NOT BE BLANK");
        }
        if (hrData.getUnit() == null || hrData.getUnit().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "UNIT NAME CAN NOT BE BLANK");
        }

        if (hrData.getPid() == null || hrData.getPid().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PID CAN NOT BE BLANK");
        }

        if (hrData.getFullName() == null || hrData.getFullName().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FULL NAME CAN NOT BE BLANK");
        }

        if (hrData.getPno() == null || hrData.getPno().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PNO CAN NOT BE BLANK");
        }

        if (hrData.getRank() == null || hrData.getRank().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "RANK CAN NOT BE BLANK");
        }

        if (hrData.getUserName() == null || hrData.getUserName().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER NAME CAN NOT BE BLANK");
        }

        if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLE ID CAN NOT BE BLANK");
        }

        if (hrData.getToDate() == null || hrData.getToDate().isEmpty()) {

        } else {
            ConverterUtils.checkDateIsvalidOrNor(hrData.getToDate());
        }

        if (hrData.getFromDate() == null || hrData.getFromDate().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "FROM DATE CAN NOT BE BLANK");
        }

        Role getRole = roleRepository.findByRoleId(hrData.getRoleId());
        if (getRole == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID ROLE ID.");
        }

        if (hrData.getRoleId().equalsIgnoreCase(HelperUtils.VIEWER)) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU CAN NOT ASSIGN THIS ROLE.BECAUSE ALL USER HAVE THIS ROLE");
        }

        ConverterUtils.checkDateIsvalidOrNor(hrData.getFromDate());

        HrData existingHrData = hrDataRepository.findByPidAndIsActive(hrData.getPid(), "1");
        String roleData = "";

        if (existingHrData != null) {

            if (!existingHrData.getUnitId().equalsIgnoreCase(hrData.getUnitId()) ) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "This user already active in unit:"+existingHrData.getUnit()+". First deactivate user.");
            } else {

                if (existingHrData.getRoleId() == null || existingHrData.getRoleId().isEmpty()) {
                    roleData = hrData.getRoleId();
                } else {
                    if (existingHrData.getRoleId().contains(hrData.getRoleId())) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS ROLE ALREADY ASSIGN");
                    }

                    if (existingHrData.getRoleId().endsWith(",")) {
                        roleData = existingHrData.getRoleId() + hrData.getRoleId();
                    } else {
                        roleData = existingHrData.getRoleId() + "," + hrData.getRoleId();
                    }
                    hrData.setPid(existingHrData.getPid());
                }
            }
        } else {
            roleData = hrData.getRoleId();
        }

        if (hrData.getRoleId().equalsIgnoreCase(HelperUtils.CBCREATER)) {

        } else {
            List<HrData> hrUnitFind = hrDataRepository.findByUnitIdAndIsActive(hrData.getUnitId(), "1");
            for (Integer i = 0; i < hrUnitFind.size(); i++) {

                if (hrUnitFind.get(i).getRoleId() != null) {
                    if (hrUnitFind.get(i).getRoleId().contains(hrData.getRoleId())) {
                        throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS ROLE IS ALREADY ASSIGN FOR THIS UNIT.");
                    }
                }

            }
        }

       if(existingHrData!=null){
           hrData.setCreatedOn(existingHrData.getCreatedOn());
       }else{
           hrData.setCreatedOn(HelperUtils.getCurrentTimeStamp());
       }

        hrData.setRoleId(roleData);
        hrData.setUpdatedOn(HelperUtils.getCurrentTimeStamp());

        hrData.setIsActive("1");
        if (flag) {
            hrData.setAdminCreatedOn(HelperUtils.getCurrentTimeStamp());
        }else{
            if(existingHrData!=null) {
                hrData.setAdminCreatedOn(existingHrData.getAdminCreatedOn());
            }
        }
        hrDataRepository.save(hrData);
        defaultResponse.setMsg("Data save successfully");
        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    @Override
    public ApiResponse<List<HradataResponse>> getAllUser() {

        List<HradataResponse> hrListData = new ArrayList<HradataResponse>();

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION. LOGIN AGAIN");
        }


        if (hrDataCheck.getRoleId() == null || hrDataCheck.getRoleId().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLE ID CAN NOT BE BLANK");
        }

        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION. LOGIN AGAIN");
        }

        String userRole = hrDataCheck.getRoleId().split(",")[0];
        List<HrData> getAllRole = new ArrayList<>();

        if (userRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
            getAllRole = hrDataRepository.findByIsActive("1");
        }
        else if (userRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
//            CgUnit cgUnit = cgUnitRepository.findByUnit(hrDataCheck.getUnitId());
            getAllRole = hrDataRepository.findByUnitIdAndIsActive(hrDataCheck.getUnitId(), "1");
        }
            Collections.sort(getAllRole, Comparator.comparing(HrData::getCreatedOn).reversed());

            for (Integer i = 0; i < getAllRole.size(); i++) {
                HrData data = getAllRole.get(i);
                HradataResponse mainFormData = new HradataResponse();
                BeanUtils.copyProperties(data, mainFormData);

                List<Role> setAllRole = new ArrayList<>();

                if (data.getRoleId() == null || data.getRoleId().isEmpty() || data.getIsActive() == null || data.getIsActive().equalsIgnoreCase("0")) {
//                Role getRoleViewer = roleRepository.findByRoleId("113");
//                setAllRole.add(getRoleViewer);
                } else {


                    String[] getRoleData = data.getRoleId().split(",");

                    for (Integer n = 0; n < getRoleData.length; n++) {

                        if (userRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {
                            if (getRoleData[n].contains(HelperUtils.UNITADMIN)) {
                                Role getRoleViewer = roleRepository.findByRoleId(HelperUtils.UNITADMIN);
                                setAllRole.add(getRoleViewer);
                            }

                        } else if (userRole.equalsIgnoreCase(HelperUtils.UNITADMIN)) {
                            if (Integer.parseInt(HelperUtils.UNITADMIN) > Integer.parseInt(getRoleData[n])) {
                                Role getRoleViewer = roleRepository.findByRoleId((getRoleData[n]));
                                setAllRole.add(getRoleViewer);
                            }
                        }

                    }
                }

                if (setAllRole.size() > 0) {
                    mainFormData.setRole(setAllRole);
                    mainFormData.setToDate(ConverterUtils.conVertDateTimeFormat(data.getToDate()));
                    mainFormData.setFromDate(ConverterUtils.conVertDateTimeFormat(data.getFromDate()));
                    //mainFormData.setAdminCreatedOn(data.getAdminCreatedOn());
                    hrListData.add(mainFormData);
                }


            }
        if (userRole.equalsIgnoreCase(HelperUtils.SYSTEMADMIN)) {

            Collections.sort(hrListData,Comparator.comparing(HradataResponse::getAdminCreatedOn).reversed());

        }

        return ResponseUtils.createSuccessResponse(hrListData, new TypeReference<List<HradataResponse>>() {
        });

    }
    @Override
    public ApiResponse<List<ICGHRDataResponse>> getAllICGUser() {
        List<ICGHRDataResponse> hrListData = new ArrayList<ICGHRDataResponse>();
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        List<HrDataicg> allUsers=hr_DataRepository.findAll();
       // List<ICGHRDataResponse> responseList=new ArrayList<>();
        for(HrDataicg user:allUsers){
            CgUnit unit=cgUnitRepository.findByUnit(user.getUnit());
            HrCodeRank rank=hrcodeRankRepository.findByRank(user.getRank());
            CgStation station=cgStationRepository.findByStationId(user.getStation());
            if(unit!=null&&rank!=null&&station!=null){
                ICGHRDataResponse userResponse=new ICGHRDataResponse();
                userResponse.setPid(user.getPid());
                userResponse.setDob(String.valueOf(user.getDob()));
                userResponse.setCadre(user.getCadre());
                userResponse.setName(user.getNameDescr());
                userResponse.setMobileNo(user.getMobileNo());
                userResponse.setPno(user.getPno());
                userResponse.setRank(rank.getDescr());
                userResponse.setJoiningDate(String.valueOf(user.getDoe()));
                userResponse.setOffEmail(user.getOffEmail());
                userResponse.setPressEmail(user.getPersEmail());
                userResponse.setUnit(unit.getCgUnitShort());
                userResponse.setUnitDate(String.valueOf(user.getUnitDt()));
                userResponse.setStation(station.getStationName());
                userResponse.setStationDate(null);
                userResponse.setUnitId(user.getUnit());
                userResponse.setUsername(user.getUserName());
                userResponse.setPid(user.getPid());
                hrListData.add(userResponse);
            }

        }
       // HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
//        if (hrDataCheck == null) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION. LOGIN AGAIN");
//        }
//
//        if (hrDataCheck == null) {
//            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID SESSION. LOGIN AGAIN");
//        }
//        Collections.sort(hrListData,Comparator.comparing(HradataResponse::getAdminCreatedOn).reversed());
        return ResponseUtils.createSuccessResponse(hrListData, new TypeReference<List<ICGHRDataResponse>>() {
        });
    }
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> removeUser(String pid) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (pid == null || pid.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PID CAN NOT BE BLANK");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(pid, "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER NOT FOUND.");
        }


        DefaultResponse defaultResponse = new DefaultResponse();

        if (hrData.getIsActive().equalsIgnoreCase("1")) {
            hrData.setIsActive("0");
            defaultResponse.setMsg("User deactivate successfully");
            hrDataRepository.save(hrData);
        } else {
            hrData.setIsActive("1");
            defaultResponse.setMsg("User activate successfully");
            hrDataRepository.save(hrData);
        }

        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> activateUser(String pid) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");
        if (pid == null || pid.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PID CAN NOT BE BLANK");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(pid, "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER NOT FOUND.");
        }


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        } else {

            if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER ASSIGN INVALID ID. PLEASE CONTACT ADMINISTER");
            }

            String[] newRoleIdData = hrData.getRoleId().split(",");
            int newRoleId = 0;
            for (int i = 0; i < newRoleIdData.length; i++) {
                if (Integer.parseInt(newRoleIdData[i].trim()) > newRoleId) {
                    newRoleId = Integer.parseInt(newRoleIdData[i]);
                }
            }


            String[] testArray = hrDataCheck.getRoleId().split(",");
            int max = 0;
            for (int i = 0; i < testArray.length; i++) {
                if (Integer.parseInt(testArray[i].trim()) > max) {
                    max = Integer.parseInt(testArray[i]);
                }
            }
            if (newRoleId >= max) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE THIS TASK");
            }

        }


        DefaultResponse defaultResponse = new DefaultResponse();


        hrData.setIsActive("1");
        defaultResponse.setMsg("User Activate successfully");
        hrDataRepository.save(hrData);


        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> deActivateUser(String pid,String rollId) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (pid == null || pid.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PID CAN NOT BE BLANK");
        }

        if (rollId == null || rollId.isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "ROLL ID CAN NOT BE BLANK");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(pid, "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER NOT FOUND.");
        }


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        } else {

            if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER ASSIGN INVALID ID. PLEASE CONTACT ADMINISTER");
            }

            String[] newRoleIdData = hrData.getRoleId().split(",");
            List<String> stringList = new ArrayList<>(Arrays.asList(newRoleIdData));
            if (!stringList.contains(rollId)){
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "THIS ROLE IS NOT ASSIGNED TO THE USER");
            }
            stringList.remove(rollId);
            String newRoll="";
            for (String item: stringList) {
                newRoll += item+",";
            }
            hrData.setRoleId(newRoll);
            if (newRoll==null || newRoll.isEmpty()){
                hrData.setIsActive("0");
            }
        }


        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setMsg("User DeActivate successfully");
        hrDataRepository.save(hrData);


        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public ApiResponse<DefaultResponse> removeRole(HrData hrDataRequest) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if (hrDataRequest.getPid() == null || hrDataRequest.getPid().isEmpty()) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "PID CAN NOT BE BLANK");
        }

        HrData hrData = hrDataRepository.findByPidAndIsActive(hrDataRequest.getPid(), "1");
        if (hrData == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER NOT FOUND.");
        }


        if (hrDataCheck == null) {
            throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "INVALID TOKEN.LOGIN AGAIN");
        } else {

            if (hrData.getRoleId() == null || hrData.getRoleId().isEmpty()) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "USER ASSIGN INVALID ID. PLEASE CONTACT ADMINISTER");
            }

            String[] newRoleIdData = hrData.getRoleId().split(",");
            int newRoleId = 0;
            for (int i = 0; i < newRoleIdData.length; i++) {
                if (Integer.parseInt(newRoleIdData[i].trim()) > newRoleId) {
                    newRoleId = Integer.parseInt(newRoleIdData[i]);
                }
            }


            String[] testArray = hrDataCheck.getRoleId().split(",");
            int max = 0;
            for (int i = 0; i < testArray.length; i++) {
                if (Integer.parseInt(testArray[i].trim()) > max) {
                    max = Integer.parseInt(testArray[i]);
                }
            }
            if (newRoleId >= max) {
                throw new SDDException(HttpStatus.UNAUTHORIZED.value(), "YOU ARE NOT AUTHORIZED TO CREATE THIS TASK");
            }
        }


        DefaultResponse defaultResponse = new DefaultResponse();
        hrData.setIsActive("0");
        defaultResponse.setMsg("User DeActivate successfully");
        hrDataRepository.save(hrData);


        return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {
        });
    }


    @Override
    public ApiResponse<Boolean> userExit() {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);
        HrData hrDataCheck = hrDataRepository.findByUserNameAndIsActive(currentLoggedInUser.getPreferred_username(), "1");

        if(hrDataCheck != null) {
            return ResponseUtils.createSuccessResponse(true, new TypeReference<Boolean>() {
            });
        } else {
            return ResponseUtils.createSuccessResponse(false, new TypeReference<Boolean>() {
            });
        }

    }

}
