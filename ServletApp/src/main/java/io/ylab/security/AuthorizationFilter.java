package io.ylab.security;

import io.ylab.managment.UserManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;


public class AuthorizationFilter implements Filter {

    private final ArrayList<String> authCommands = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        authCommands.add("registration");
        authCommands.add("authorization");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String token = httpRequest.getHeader("Authorization");
        //TODO: get needed url part
        String url = httpRequest.getRequestURL().toString();
        if (authCommands.contains(url) || UserManager.validateToken(token)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not authorized.");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
