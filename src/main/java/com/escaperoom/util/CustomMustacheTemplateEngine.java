package com.escaperoom.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import spark.ModelAndView;
import spark.TemplateEngine;

import java.io.StringWriter;

public class CustomMustacheTemplateEngine extends TemplateEngine {
    
    private final MustacheFactory mustacheFactory;
    
    public CustomMustacheTemplateEngine() {
        // Templates are loaded from classpath: src/main/resources/templates
        // Use classloader to find resources
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CustomMustacheTemplateEngine.class.getClassLoader();
        }
        this.mustacheFactory = new DefaultMustacheFactory("templates");
    }
    
    public CustomMustacheTemplateEngine(String templateDirectory) {
        this.mustacheFactory = new DefaultMustacheFactory(templateDirectory);
    }
    
    @Override
    public String render(ModelAndView modelAndView) {
        String viewName = modelAndView.getViewName();
        if (!viewName.endsWith(".mustache")) {
            viewName = viewName + ".mustache";
        }
        
        try {
            Mustache mustache = mustacheFactory.compile(viewName);
            StringWriter writer = new StringWriter();
            mustache.execute(writer, modelAndView.getModel());
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template: " + viewName, e);
        }
    }
}

