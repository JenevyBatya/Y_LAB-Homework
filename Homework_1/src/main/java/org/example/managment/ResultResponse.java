package org.example.managment;

import org.example.enumManagment.ChamberTypeEnum;
import org.example.enumManagment.ResponseEnum;

public class ResultResponse {
    private boolean status;
    private ResponseEnum response;
    private String data;
    private ChamberTypeEnum chamberTypeEnum;

    public void setStatus(boolean status) {
        this.status = status;
    }


    public ResponseEnum getResponse() {
        return response;
    }

    public void setResponse(ResponseEnum response) {
        this.response = response;
    }

    public ResultResponse(boolean status, ResponseEnum response) {
        this.status = status;
        this.response = response;
    }

    public ResultResponse(boolean status, ResponseEnum response, String data) {
        this.status = status;
        this.response = response;
        this.data = data;
    }

    public ResultResponse(boolean status, ResponseEnum response, ChamberTypeEnum chamberTypeEnum) {
        this.status = status;
        this.response = response;
        this.chamberTypeEnum = chamberTypeEnum;
    }

    public ResultResponse() {
    }

    public String getData() {
        return data;
    }

    public void printData() {
        if (response.equals(ResponseEnum.TEXT)) {
            System.out.println(data);
        } else {
            System.out.println(response);
        }

    }

    public void printResponse() {
        System.out.println(response);
    }

    public ChamberTypeEnum getChamberTypeEnum() {
        return chamberTypeEnum;
    }

    public boolean isStatus() {
        return status;
    }
}

