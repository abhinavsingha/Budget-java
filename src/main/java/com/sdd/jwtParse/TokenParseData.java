
package com.sdd.jwtParse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;


public class TokenParseData {

    private Integer exp;
    private Integer iat;
    private Integer auth_time;
    private String jti;
    private String iss;
//    private String aud;
    private String sub;
    private String typ;
    private String azp;
    private String nonce;
    private String session_state;
    private String acr;
//    private List<String> allowed;
    private RealmAccess realm_access;
    private ResourceAccess resource_access;
    private String scope;
    private String sid;
    private Boolean email_verified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String locale;
    private String family_name;
    private String email;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getIat() {
        return iat;
    }

    public void setIat(Integer iat) {
        this.iat = iat;
    }

    public Integer getAuth_time() {
        return auth_time;
    }

    public void setAuth_time(Integer auth_time) {
        this.auth_time = auth_time;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }
//
//    public String getAud() {
//        return aud;
//    }
//
//    public void setAud(String aud) {
//        this.aud = aud;
//    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getAzp() {
        return azp;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSession_state() {
        return session_state;
    }

    public void setSession_state(String session_state) {
        this.session_state = session_state;
    }

    public String getAcr() {
        return acr;
    }

    public void setAcr(String acr) {
        this.acr = acr;
    }

//    public List<String> getAllowed() {
//        return allowed;
//    }
//
//    public void setAllowed(List<String> allowed) {
//        this.allowed = allowed;
//    }

    public RealmAccess getRealm_access() {
        return realm_access;
    }

    public void setRealm_access(RealmAccess realm_access) {
        this.realm_access = realm_access;
    }

    public ResourceAccess getResource_access() {
        return resource_access;
    }

    public void setResource_access(ResourceAccess resource_access) {
        this.resource_access = resource_access;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Boolean getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(Boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
