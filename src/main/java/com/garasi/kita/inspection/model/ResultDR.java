package com.garasi.kita.inspection.model;

import java.util.List;

public class ResultDR {

    private List<MessageDR> data;

    public List<MessageDR> getData() {
        return data;
    }

    public void setData(List<MessageDR> data) {
        this.data = data;
    }

    public static class MessageDR {
        private String umid;
        private String subAccountId;
        private String channel;
        private String channelId;
        private String direction;
        private String country;
        private Status status;
        private User user;
        private String contentType;
        private String content;
        private String createdAt;
        private String clientMessageId;
        private String clientBatchId;
        private String batchId;
        private Integer step;

        public String getUmid() {
            return umid;
        }

        public void setUmid(String umid) {
            this.umid = umid;
        }

        public String getSubAccountId() {
            return subAccountId;
        }

        public void setSubAccountId(String subAccountId) {
            this.subAccountId = subAccountId;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getClientMessageId() {
            return clientMessageId;
        }

        public void setClientMessageId(String clientMessageId) {
            this.clientMessageId = clientMessageId;
        }

        public String getClientBatchId() {
            return clientBatchId;
        }

        public void setClientBatchId(String clientBatchId) {
            this.clientBatchId = clientBatchId;
        }

        public String getBatchId() {
            return batchId;
        }

        public void setBatchId(String batchId) {
            this.batchId = batchId;
        }

        public Integer getStep() {
            return step;
        }

        public void setStep(Integer step) {
            this.step = step;
        }
    }

    public static class User {
        String msisdn;
        String channelUserId;

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getChannelUserId() {
            return channelUserId;
        }

        public void setChannelUserId(String channelUserId) {
            this.channelUserId = channelUserId;
        }
    }

    public static class Status {
        String state;
        String detail;
        String timestamp;
        String errorCode;
        String errorMessage;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

}
