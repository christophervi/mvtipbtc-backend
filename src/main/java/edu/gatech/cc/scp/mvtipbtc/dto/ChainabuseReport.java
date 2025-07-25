package edu.gatech.cc.scp.mvtipbtc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainabuseReport {

    private String id;
    private String createdAt;
    private boolean trusted;
    
    @JsonProperty("scamCategory")
    private String scamCategory;
    
    private List<AddressInfo> addresses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public String getScamCategory() {
        return scamCategory;
    }

    public void setScamCategory(String scamCategory) {
        this.scamCategory = scamCategory;
    }

    public List<AddressInfo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressInfo> addresses) {
        this.addresses = addresses;
    }

    /**
     * Represents the nested address object within a Chainabuse report.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressInfo {
        private String address;
        private String chain;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getChain() {
            return chain;
        }

        public void setChain(String chain) {
            this.chain = chain;
        }
    }
}
