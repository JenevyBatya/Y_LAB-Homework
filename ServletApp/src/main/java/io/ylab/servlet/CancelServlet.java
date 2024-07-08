package io.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ylab.dto.BookingDto;
import io.ylab.managment.ResultResponse;
import io.ylab.mapper.BookingMapper;
import io.ylab.model.Booking;
import io.ylab.repo.BookingRepo;
import io.ylab.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cancel")
public class CancelServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultResponse response = bookingService.getUserBooking(req);
        super.doGet(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BookingDto bookingDTO = objectMapper.readValue(req.getReader(), BookingDto.class);
        Booking booking = BookingMapper.INSTANCE.toEntity(bookingDTO);
        ResultResponse response = bookingService.deleteBooking(booking);
        super.doDelete(req, resp);
    }
}
