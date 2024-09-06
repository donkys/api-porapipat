package com.porapipat.porapipat_api.service.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PatternLogControllerService {

    public void logInfo(HttpServletRequest request, Authentication auth, boolean isAccess) {
        if (isAccess)
            log.info("Api {}: {} with user: {}", request.getMethod(), request.getRequestURL(), auth.getName());
        else
            log.info("Api {}: {} success with user: {}", request.getMethod(), request.getRequestURL(), auth.getName());
    }

    public void logInfoWithRequest(HttpServletRequest request, Authentication auth, Object requestBody, boolean isAccess) {
        if (isAccess)
            log.info("Api {}: {} with user: {} request : {}", request.getMethod(), request.getRequestURL(), auth.getName(), requestBody);
        else
            log.info("Api {}: {} success with user: {} request : {}", request.getMethod(), request.getRequestURL(), auth.getName(), requestBody);
    }

    public void logError(boolean isError, HttpServletRequest request, Exception ex) {
        if (isError)
            log.error("Api {}: {} : Have Error {}", request.getMethod(), request.getRequestURL(), ex.getMessage(), ex);
        else
            log.warn("Api {}: {} : Have Error {}", request.getMethod(), request.getRequestURL(), ex.getMessage(), ex);
    }

}
