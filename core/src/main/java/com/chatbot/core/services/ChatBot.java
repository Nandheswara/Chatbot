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
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;

@Component(service = ChatBot.class, immediate = true)
public class ChatBot {

    private static final Logger LOG = LoggerFactory.getLogger(ChatBot.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    public Map<String, String> searchQuery() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", "/content");
        queryMap.put("type", "cq:Page");
        queryMap.put("property", "jcr:content/sling:configRef");
        queryMap.put("property.operation", "exists");
        return queryMap;
    }

    public String projectNames() {
        String projectName = null;
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);
            Session session = resourceResolver.adaptTo(Session.class);

            Query query = builder.createQuery(PredicateGroup.create(searchQuery()), session);
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                try {
                    // String path = hit.getPath();
                    projectName = hit.getResource().getName();
                    // LOG.error("Page path: " + path );
                    LOG.error("Page path: " + projectName);
                } catch (Exception e) {
                    LOG.error("Exception: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("\n ----ERROR -----{} ", e.getMessage());
        }
        return projectName;
    }

    public String Templates() {
        String templateList = null;
        try {
            ResourceResolver resourceResolver = ResolverUtil.newResolver(resourceResolverFactory);
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            String sitePath = "/content/" + projectNames();
            Collection<Template> templates = pageManager.getTemplates(sitePath);

            for (Template template : templates) {
                // LOG.error("Template Path: " + template.getPath());
                templateList = template.getTitle();
                LOG.error("Template Title: " + templateList);
            }

        } catch (Exception e) {
            LOG.error("\n ----ERROR -----{} ", e.getMessage());
        }

        return templateList;
    }

}

