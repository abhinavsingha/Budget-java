package com.sdd.service;

import com.sdd.entities.AllocationType;
import com.sdd.response.ApiResponse;;

import java.util.List;

public interface AllocationService {

	ApiResponse<List<AllocationType>> findAllAuthors();

	ApiResponse<AllocationType> findAuthorById(String id);

	ApiResponse<AllocationType> createAuthor(AllocationType author);

	ApiResponse<AllocationType> updateAuthor(AllocationType author);

	ApiResponse<List<AllocationType>> findAllActiveAuthor();

	ApiResponse<List<AllocationType>> findAuthorByAllNameOrPublicationOrEdition(String id);
}
