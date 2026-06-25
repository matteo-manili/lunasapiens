package com.lunasapiens.filter;


import com.lunasapiens.Constants;

import com.lunasapiens.entity.PageVisit;
import com.lunasapiens.repository.PageVisitRepository;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class PageVisitFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(PageVisitFilter.class);


    private final PageVisitRepository repository;



    public PageVisitFilter(PageVisitRepository repository){
        this.repository = repository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String path=request.getServletPath();

        if(path.equals( Constants.PAGE_ACTIVITY )){
            chain.doFilter(request,response);
            return;
        }

        if(!isPage(path)){
            chain.doFilter(request,response);
            return;
        }
        if(isBot(request)){
            chain.doFilter(request,response);
            return;
        }
        if(isLoggedUser()){
            chain.doFilter(request,response);
            return;
        }
        String ip=request.getRemoteAddr();
        String ua=request.getHeader("User-Agent");

        HttpSession session = request.getSession(true);
        String sessionId = session.getId();


        PageVisit existing = repository.findTopBySessionIdOrderByIdDesc(sessionId);
        if(existing == null){
            repository.save(new PageVisit(path, ip, ua, sessionId));
        }
        else if(!existing.getPath().equals(path)){
            LocalDateTime now = LocalDateTime.now();
            if(existing.getStartTime()!=null){
                long seconds = Duration.between(existing.getStartTime(), now).getSeconds();
                existing.setSecondsSpent((int)seconds);
            }
            existing.setLastHeartbeat(now);
            repository.save(existing);
            PageVisit newVisit = new PageVisit(path, ip, ua, sessionId);
            repository.save(newVisit);
        }


        chain.doFilter(request,response);
    }


    private boolean isLoggedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null){
            return false;
        }
        if(!auth.isAuthenticated()){
            return false;
        }
        String username=auth.getName();
        return Constants.MATTEO_MANILI_GMAIL.equals(username);
    }


    private boolean isBot(HttpServletRequest request){
        String ua = request.getHeader("User-Agent");
        if(ua == null){
            return true;
        }
        ua = ua.toLowerCase();
        List<String> bots=List.of(
                "bot",
                "spider",
                "crawler",
                "google",
                "bing",
                "yandex",
                "facebookexternalhit",
                "slurp",
                "headless",
                "phantom",
                "selenium",
                "wget",
                "curl",
                "python",
                "java",
                "scrapy"
        );
        return bots.stream().anyMatch(ua::contains);
    }


    private boolean isPage(String path){
        return
                !path.equals( Constants.PAGE_ACTIVITY )
                &&
                !path.startsWith("/css")
                &&
                !path.startsWith("/js")
                &&
                !path.startsWith("/images")
                &&
                !path.contains("/manifest.json")
                &&
                !path.contains("/service-worker.js")
                &&
                !path.contains(".ico")
                &&
                !path.contains(".png")
                &&
                !path.contains(".jpg")
                &&
                !path.contains(".svg");
    }


}