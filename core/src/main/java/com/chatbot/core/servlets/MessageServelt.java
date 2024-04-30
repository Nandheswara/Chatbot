package com.chatbot.core.servlets;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service=Servlet.class,
           property={
                   "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                   "sling.servlet.paths="+ "/bin/inputmessage"
           })
public class MessageServelt extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServelt.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");
        LOG.error("Message: " + message);
     ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource("/content/chatbot/language-masters/en/home/jcr:content/root/container/container/chatbot");
        Node node = resource.adaptTo(Node.class);
        if (node != null) {
            try {
                node.setProperty("message", message);
                node.getSession().save();
            } catch (Exception e) {
                throw new ServletException("Failed to save node properties", e);
            }
        }else{
            throw new ServletException("Failed to get JCR node");
        }
       
    }
}