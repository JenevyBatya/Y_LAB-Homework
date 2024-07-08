package io.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ylab.dto.ChamberDto;
import io.ylab.mapper.ChamberMapper;
import io.ylab.model.Chamber;
import io.ylab.service.ChamberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/expert/deletechamber")
public class DeleteChamberServlet extends HttpServlet {
    private final ChamberService bookingService = new ChamberService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ChamberDto chamberDto = objectMapper.readValue(req.getReader(), ChamberDto.class);
        Chamber chamber = ChamberMapper.INSTANCE.toEntity(chamberDto);
        super.doDelete(req, resp);
    }
}
