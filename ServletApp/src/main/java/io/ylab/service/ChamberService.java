package io.ylab.service;

import io.ylab.managment.ResultResponse;
import io.ylab.managment.enums.ResponseEnum;
import io.ylab.repo.AdminRepo;
import io.ylab.repo.ChamberRepo;

import java.net.http.HttpRequest;
import java.sql.SQLException;

public class ChamberService {
    private ChamberRepo chamberRepo = new ChamberRepo();
    public ResultResponse deleteChamber(HttpRequest httpRequest){
        try {
            if (!AdminRepo.isAdmin()) {
                return new ResultResponse(false, ResponseEnum.NO_ACCESS);
            }
            return chamberRepo.delete(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
