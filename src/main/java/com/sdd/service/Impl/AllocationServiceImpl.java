package com.sdd.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sdd.entities.AllocationType;
import com.sdd.entities.repository.AllocationRepository;
import com.sdd.exception.SDDException;
import com.sdd.jwt.HeaderUtils;
import com.sdd.jwt.JwtUtils;
import com.sdd.jwtParse.TokenParseData;
import com.sdd.response.ApiResponse;
import com.sdd.service.AllocationService;
import com.sdd.utils.HelperUtils;
import com.sdd.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class AllocationServiceImpl implements AllocationService {

    @Autowired    
    AllocationRepository allocationRepository;

    @Autowired    
    private JwtUtils jwtUtils;

    @Autowired    
    private HeaderUtils headerUtils;


    @Override
    public ApiResponse<List<AllocationType>> findAllAuthors() {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


//        return ResponseUtils.createSuccessResponse(allocationRepository.findAllByOrderByFullName(), new TypeReference<List<AllocationType>>() {
//        });

        return  null;
    }

    @Override
    public ApiResponse<AllocationType> findAuthorById(String id) {


        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


//        System.out.println("id" + currentLoggedInUser.get(HeaderUtils.USER_ID));
//        System.out.println("id" + currentLoggedInUser.get(HeaderUtils.EMAIL_ID));

//        return ResponseUtils.createSuccessResponse(allocationRepository.findByAutherId(id), new TypeReference<AllocationType>() {
//        });

        return  null;
    }

    @Override
    public ApiResponse<AllocationType> createAuthor(AllocationType author) {
        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);


        AllocationType author1 = allocationRepository.save(author);
        return ResponseUtils.createSuccessResponse(author1, new TypeReference<AllocationType>() {
        });
    }

    @Override
    public ApiResponse<AllocationType> updateAuthor(AllocationType author) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

        AllocationType author1 = allocationRepository.save(author);
        return ResponseUtils.createSuccessResponse(author1, new TypeReference<AllocationType>() {
        });
    }



 
    @Override
    public ApiResponse<List<AllocationType>> findAllActiveAuthor() {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

//        return ResponseUtils.createSuccessResponse(allocationRepository.findByIsActiveOrderByFullName(BigInteger.valueOf(1)), new TypeReference<List<AllocationType>>() {
//        });
        return  null;
    }

    @Override
    public ApiResponse<List<AllocationType>> findAuthorByAllNameOrPublicationOrEdition(String keyword) {

        String token = headerUtils.getTokeFromHeader();
        TokenParseData currentLoggedInUser = headerUtils.getUserCurrentDetails(token);

//		if (keyword != null) {
//            return ResponseUtils.createSuccessResponse(allocationRepository.findByFullNameContainingIgnoreCaseOrPublicationContainingIgnoreCaseOrPublistionDateContainingIgnoreCase(keyword, keyword, keyword), new TypeReference<List<AllocationType>>() {
//            });
//        }
//        return ResponseUtils.createSuccessResponse(allocationRepository.findByIsActiveOrderByFullName(BigInteger.valueOf(1)), new TypeReference<List<AllocationType>>() {
//        });
        return  null;
    }

}
