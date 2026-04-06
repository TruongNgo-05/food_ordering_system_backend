package com.example.project_back.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// lay name khi login
public class SecurityUtils {

    public static String getCurrentUsername(){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            return null;
        }

        return authentication.getName();
    }
}