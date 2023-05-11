package com.sdd.utils;

import lombok.Builder;
import lombok.Setter;

@Builder
    @Setter
public  class InputData {
        private String formId;
        private String formRemark;
        private String formStatus;
        private String userAccId;
        private String userType;
    }