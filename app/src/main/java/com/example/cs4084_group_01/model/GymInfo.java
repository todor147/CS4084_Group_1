package com.example.cs4084_group_01.model;

import java.util.List;
import java.util.Map;

public class GymInfo {
    private String name;
    private String description;
    private String location;
    private String contactPhone;
    private String contactEmail;
    private List<String> facilities;
    private List<String> rules;
    private Map<String, OperatingHours> hours;
    private List<SpecialHours> specialHours;

    public static class OperatingHours {
        private String open;
        private String close;

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }
    }

    public static class SpecialHours {
        private String date;
        private String open;
        private String close;
        private String note;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public Map<String, OperatingHours> getHours() {
        return hours;
    }

    public void setHours(Map<String, OperatingHours> hours) {
        this.hours = hours;
    }

    public List<SpecialHours> getSpecialHours() {
        return specialHours;
    }

    public void setSpecialHours(List<SpecialHours> specialHours) {
        this.specialHours = specialHours;
    }
} 