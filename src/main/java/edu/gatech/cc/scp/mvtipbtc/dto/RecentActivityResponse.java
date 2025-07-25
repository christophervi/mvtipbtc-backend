package edu.gatech.cc.scp.mvtipbtc.dto;

import java.util.List;

public class RecentActivityResponse {
    private List<ActivityItem> activities;

    public List<ActivityItem> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityItem> activities) {
        this.activities = activities;
    }
    
    public static class ActivityItem {
        private String type;
        private String description;
        private long timestamp;
        private String address;
        private String riskLevel;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }
    }
}

