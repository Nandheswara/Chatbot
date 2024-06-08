package com.chatbot.core.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatbot.core.utils.ResolverUtil;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.google.gson.JsonObject;

@Component(service = ChatBot.class, immediate = true)
public class ChatBot {

    JsonObject pageCreated = new JsonObject();

    private static final Logger LOG = LoggerFactory.getLogger(ChatBot.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    public JsonObject defaultResponse() {

        JsonObject defaultResponse = new JsonObject();
        try {
            // Add default values
            defaultResponse.addProperty("createpage", "Create a Page");
            defaultResponse.addProperty("createcomponent", "Create a Component");
            defaultResponse.addProperty("mainmenu", "Main Menu");
        } catch (Exception e) {
            LOG.error("\n ERROR {} ", e.getMessage());
        }
        return defaultResponse;
    }

    public JsonObject pageName() {
        JsonObject pageName = new JsonObject();
        try {
            pageName.addProperty("enterthepagename", "Enter the Page Name");
        } catch (Exception e) {
            LOG.error("\n ERROR {} ", e.getMessage());
        }
        return pageName;
    }

    public void pageCreated(String userinputPageName, String userinputPagePath, String userinputTemplateName, String userinputProjectName) {
        final String pageTitle = userinputPageName;
        String pageName = userinputPageName.replace(" ", "-").toLowerCase();
        String templateName = userinputTemplateName.replace(" ", "-").toLowerCase();
        try {
            String templatePath = "/conf/"+userinputProjectName+"/settings/wcm/templates/" + templateName;
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page prodPage = pageManager.create(userinputPagePath, pageName, templatePath, pageTitle, true);
            LOG.error("Page Created chatbot : " + prodPage.getPath());
            if (prodPage.isValid()) {
                LOG.error("Page Created Successfully : " + prodPage.getPath());
            } else {
                LOG.error("Page Creation Failed : " + prodPage.getPath());
            }
            pageCreated.addProperty("pagecreated", "Page Created Successfully");
            pageCreated.addProperty("mainmenu", "Main Menu");
        } catch (Exception e) {
            pageCreated.addProperty("pagecreationfailure", "Unable to create page");
            pageCreated.addProperty("mainmenu", "Main Menu");
        }
    }

    public JsonObject getPageCreationStatus() {
        return pageCreated;
    }

    public JsonObject pagePath(String userinputProjectName) {
        JsonObject pageCreated = new JsonObject();
        try {
            pageCreated.addProperty("pagepath", "/content/" + userinputProjectName + "/");
        } catch (Exception e) {
            LOG.error("\n ERROR {} ", e.getMessage());
        }
        return pageCreated;
    }

    public Map<String, String> searchQuery() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", "/content");
        queryMap.put("type", "cq:Page");
        queryMap.put("property", "jcr:content/sling:configRef");
        queryMap.put("property.operation", "exists");
        return queryMap;
    }

    public JsonObject projectNames() {
        String projectName = null;
        JsonObject projectNames = new JsonObject();
        int counter = 1;
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);
            Session session = resourceResolver.adaptTo(Session.class);

            Query query = builder.createQuery(PredicateGroup.create(searchQuery()), session);
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                try {
                    projectName = hit.getResource().getName();
                    projectNames.addProperty("projectName-" + counter, projectName);
                    counter++;
                } catch (Exception e) {
                    LOG.error("Exception: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("\n ----ERROR -----{} ", e.getMessage());
        }
        return projectNames;
    }

    public JsonObject Templates(String message) {
        String templateList = null;
        JsonObject templateLists = new JsonObject();
        int counter = 1;
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            String sitePath = "/content/" + message;
            Collection<Template> templates = pageManager.getTemplates(sitePath);

            for (Template template : templates) {
                templateList = template.getName();
                templateLists.addProperty("templateName-" + counter, templateList);
                counter++;
            }

        } catch (Exception e) {
            LOG.error("\n ----ERROR -----{} ", e.getMessage());
        }
        return templateLists;
    }

}
