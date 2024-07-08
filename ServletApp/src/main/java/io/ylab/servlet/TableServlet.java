package io.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ylab.dto.BookingDto;
import io.ylab.managment.ResultResponse;
import io.ylab.mapper.BookingMapper;
import io.ylab.model.Booking;
import io.ylab.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/table")
public class TableServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BookingDto bookingDTO = objectMapper.readValue(req.getReader(), BookingDto.class);
        Booking booking = BookingMapper.INSTANCE.toEntity(bookingDTO);
        ResultResponse response = bookingService.getTable(booking);
        super.doGet(req, resp);
    }
}
