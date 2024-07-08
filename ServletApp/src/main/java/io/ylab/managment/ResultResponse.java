package io.ylab.managment;

import io.ylab.managment.enums.ResponseEnum;

public class ResultResponse {
    private final boolean success;
    private final ResponseEnum response;
    private Object object = null;

    public ResultResponse(boolean success, ResponseEnum response) {
        this.success = success;
        this.response = response;
    }

    public ResultResponse(boolean success, ResponseEnum response, Object object) {
        this.success = success;
        this.response = response;
        this.object = object;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResponseEnum getResponse() {
        return response;
    }

    public Object getObject() {
        return object;
    }
}
