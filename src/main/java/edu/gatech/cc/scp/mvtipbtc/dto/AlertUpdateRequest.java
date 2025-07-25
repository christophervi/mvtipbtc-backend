package edu.gatech.cc.scp.mvtipbtc.dto;

public class AlertUpdateRequest {
    private String status;
    
    public AlertUpdateRequest() {}
    
    public AlertUpdateRequest(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

