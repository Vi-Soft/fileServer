package com.visoft.files.handler;

import com.visoft.files.entity.Token;
import com.visoft.files.entity.User;
import com.visoft.files.service.util.PageService;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.visoft.files.service.DI.DependencyInjectionService.*;
import static com.visoft.files.service.ErrorConst.NO_TOKEN;
import static com.visoft.files.service.ErrorConst.TOKEN_NOT_FOUND;
import static com.visoft.files.service.util.SenderService.sendMessage;

public class FileResourceHandler extends ResourceHandler {

    FileResourceHandler(ResourceManager resourceSupplier) {
        super(resourceSupplier);
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
//        HeaderMap headerMap = exchange.getRequestHeaders();
//        String authorization = headerMap.getFirst(Headers.AUTHORIZATION);
//        if (authorization == null) {
//            sendMessage(exchange,NO_TOKEN);
//           return;
//        } else {
//            authorization = authorization.substring(7);
//        }
        Token token = TOKEN_SERVICE.findByToken("eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI1YzBmY2MzNzMwNjljMTBhNWM1MzJhM2QifQ.xIGWS8FlDEJpHQl_te72L_CY5AY8qt2zgG2zlmPmVw4Xtpba5NYIks4GJo-RjSRLuVxZFD3AyjpfwkT5Node4g");
        if (token==null){
            sendMessage(exchange,TOKEN_NOT_FOUND);
            return;
        }
        User user = USER_SERVICE.findById(token.getUserId());
        String requestURI = exchange.getRequestURI();
        if (user.getRole().toString().equals("USER")) {
            if (requestURI.equals("/")) {
                Cookie cookie = new CookieImpl("token","123");
                exchange.setResponseCookie(cookie);
                PageService.getMainUserHtml(exchange, getFolders(user));
            }
            requestURI = reorganizeRequestURI(requestURI);
            if (!getFolders(user).contains(requestURI)) {
                PageService.getMainUserHtml(exchange, getFolders(user));
            }
            else {
                PageService.getFolderUserHtml(exchange, requestURI);
            }
        }else {
            super.handleRequest(exchange);
        }
    }

    private String reorganizeRequestURI(String requestURI) {
        int lastCharacterRequestURI = requestURI.length() - 1;
        if (requestURI.substring(lastCharacterRequestURI).equals("/")) {
            return requestURI.substring(0, lastCharacterRequestURI);
        }
        return requestURI;
    }

    private List<String> getFolders(User user){
        List<String> folders = new ArrayList<>();
            for (ObjectId folder : user.getFolders()) {
                folders.add(FOLDER_SERVICE.findById(folder).getFolder());
            }
            return folders;
    }

}
