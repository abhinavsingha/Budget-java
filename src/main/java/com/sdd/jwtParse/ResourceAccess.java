
package com.sdd.jwtParse;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;


public class ResourceAccess {

    private Account account;
    private Budget budget;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
