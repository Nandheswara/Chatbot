package com.chatbot.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatbot.core.services.ChatBot;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component(service = Servlet.class, immediate = true, property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/inputmessage"
})
public class MessageServelt extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServelt.class);

    @Reference
    ChatBot chatBot;

    private String message;
    private String userinputProjectName = "";
    private String userinputTemplateName = "";
    private String userinputPageName = "";
    private String userinputPagePath = "";
    private boolean isPageCreationInitiated = false;
    private boolean isPageName = false;
    private boolean isPagePath = false;

    List<String> pageName = new ArrayList<>();

    @Override
    public void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");

        message = getMessage();
        LOG.error("Message do get: " + message);

        JsonObject responseMessage = new JsonObject();
        if ("Create a Page".equals(message)) {
            isPageCreationInitiated = true;
            responseMessage = chatBot.projectNames();
        } else if (isPageCreationInitiated) {
            // For Template Name Response
            JsonObject projectNamesJson = chatBot.projectNames();
            List<String> projectNames = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : projectNamesJson.entrySet()) {
                projectNames.add(entry.getValue().getAsString());
            }
            if (projectNames.contains(message)) {
                userinputProjectName = message; // Save the message
                chatBot.pagePath(userinputProjectName);
                responseMessage = chatBot.Templates(message);
                JsonObject pageNameJson = chatBot.Templates(message);

                for (Map.Entry<String, JsonElement> entry : pageNameJson.entrySet()) {
                    pageName.add(entry.getValue().getAsString());
                }
                // Reset the state variable
                isPageCreationInitiated = true;
            } else if (isPageCreationInitiated) {
                if (pageName.contains(message)) {
                    userinputTemplateName = message; // Save the message
                    LOG.error("User Input Template Name: " + userinputTemplateName);
                    responseMessage = chatBot.pageName();
                    // Reset the state variable
                    isPageName = true;
                } else if (isPageName) {
                    if (message != null) {
                        userinputPageName = message; // Save the message
                        responseMessage = chatBot.pagePath(userinputProjectName);
                        isPageName = false;
                        isPagePath = true;
                    }
                } else if (isPagePath) {
                    if (message != null) {
                        userinputPagePath = message; // Save the message
                        chatBot.pageCreated(userinputPageName, userinputPagePath, userinputTemplateName,
                                userinputProjectName);
                        responseMessage = chatBot.getPageCreationStatus();
                        isPagePath = false;
                    }
                }
            }
        } else {
            responseMessage = chatBot.defaultResponse();
        }

        if ("Main Menu".equals(message)) {
            responseMessage = chatBot.defaultResponse();
        } else {
            LOG.error("Error in Main Menu : " + message);
        }
        // Write the JSONObject to the response
        resp.getWriter().write(responseMessage.toString());
    }

    @Override
    public void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp)
            throws ServletException, IOException {

        message = req.getParameter("message");
        LOG.error("Message do post: " + message);

        ResourceResolver resourceResolver = req.getResourceResolver();
        Resource resource = resourceResolver
                .getResource("/content/chatbot/language-masters/en/home/jcr:content/root/container/container/chatbot");
        Node node = resource.adaptTo(Node.class);
        if (node != null) {
            try {
                node.setProperty("message", message);
                node.getSession().save();
            } catch (Exception e) {
                throw new ServletException("Failed to save node properties", e);
            }
        } else {
            throw new ServletException("Failed to get JCR node");
        }
    }

    public String getMessage() {
        LOG.error("Get Message : " + message);
        return message;
    }
}