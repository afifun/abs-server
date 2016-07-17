package com.fmse.absserver;

import ABS.Framework.Http.ABSHttpRequestImpl_c;
import ABS.Framework.Http.ABSHttpRequest_i;
import ABS.StdLib.List_Cons;
import ABS.StdLib.List_Nil;
import ABS.StdLib.Pair;
import ABS.StdLib.Pair_Pair;
import abs.backend.java.lib.runtime.ABSObject;
import abs.backend.java.lib.runtime.COG;
import abs.backend.java.lib.types.ABSString;
import abs.backend.java.lib.types.ABSUnit;
import abs.backend.java.lib.types.ABSValue;
import com.fmse.absserver.helper.DataTransformer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.TemplateResolver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kandito Agung
 */
public class ABSCaller extends ABSObject {

    private String response;
    private HttpServletRequest request;

    public ABSCaller(COG cog) throws IOException {
        super(cog);
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public static void main(java.lang.String[] args) throws Exception {
        abs.backend.java.lib.runtime.StartUp.startup(args, ABSCaller.class);
    }

    @Override
    public String getClassName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getFieldNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ABSUnit run() {
        try {
            String contextPath = request.getContextPath();
            String requestURI = request.getRequestURI();
            String route = this.getRoute(requestURI, contextPath);
            /**
             * ************ CALL ABS HERE *********************
             */
            Class resolver = Class.forName("ABS.Framework.Route.RouteConfigImpl_c");
            ABS.Framework.Route.RouteConfigImpl_c router = ABS.Framework.Route.RouteConfigImpl_c.__ABS_createNewObject(this);
            String routeUrl = router.route(ABSString.fromString(route)).getString();
            System.out.println("Route: " + routeUrl + "from" + route);

            if (routeUrl != null && routeUrl.length() > 0) {
                String controllerName = routeUrl.split("@")[0] + "_c";
                String methodName = routeUrl.split("@")[1];

                Class controllerClass = Class.forName(controllerName);
                Object controllerObject = controllerClass.getMethod("__ABS_createNewObject", ABSObject.class).invoke(controllerClass, this);

                //todo REQUEST
                ABS.Framework.Http.ABSHttpRequestImpl_c absrequest = (ABS.Framework.Http.ABSHttpRequestImpl_c) this.createABSHttpRequest();

                Pair<ABSString, ABS.StdLib.List<ABSValue>> pair = (Pair<ABSString, ABS.StdLib.List<ABSValue>>) controllerObject.getClass().getMethod(methodName, ABSHttpRequest_i.class).invoke(controllerObject, absrequest);

                this.setResponse(pair);
            } else {
                this.setResponse(null);
            }
            return ABSUnit.UNIT;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ABSCaller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ABSUnit.UNIT;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(Pair<ABSString, ABS.StdLib.List<ABSValue>> pair) throws Exception {
        TemplateResolver templateResolver = new TemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setSuffix(".html");
        templateResolver.setResourceResolver(new ResourceResolver());

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        List_Cons<ABSValue> data;

        if (pair != null) {
            String view = DataTransformer.convertABSStringToJavaString((ABSString) pair.getArg(0));
            Context ctx = new Context();
            if (!(pair.getArg(1) instanceof List_Nil)) {
                data = (List_Cons<ABSValue>) pair.getArg(1);
                ArrayList<Object> dataModels = DataTransformer.convertABSListToJavaList(data);
                this.response = DataTransformer.convertObjectToJSON(dataModels);
            }
//            ctx.setVariable("requestMethod", this.request.getMethod());
//            ctx.setVariable("requestURI", this.request.getRequestURI());
//            ctx.setVariable("requestContextPath", this.request.getContextPath());
//            ctx.setVariable("requestRoute", this.getRoute(request.getRequestURI(), request.getContextPath()));

//            this.response = templateEngine.process(view, ctx);
            
        } else {
            this.response = "<h3>ERROR 404: PAGE NOT FOUND</h3>";
            this.response += "<span style='font-size:0.8em;'>ABS Server: " + new Date() + "</span><br />";
        }
    }

    private ABSHttpRequest_i createABSHttpRequest() throws Exception {
        ABS.StdLib.Map<ABSString, ABSString> absRequestInputMap = new ABS.StdLib.Map_EmptyMap<ABSString, ABSString>();
        Enumeration<String> parameterNames = this.request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValue = this.request.getParameterValues(paramName);

            ABSString key = ABSString.fromString(paramName);
            ABSString value = null;
            if (paramValue.length == 1) {
                value = ABSString.fromString(paramValue[0]);
            } else {
                value = ABSString.fromString(paramValue.toString());
            }
            absRequestInputMap = this.addPair(absRequestInputMap, key, value);
        }

        //add property
        ABS.StdLib.Map<ABSString, ABSString> absRequestPropertyMap = new ABS.StdLib.Map_EmptyMap<ABSString, ABSString>();

        ABSString key = null;
        ABSString value = null;

        //method
        String httpMethod = this.request.getMethod();
        key = ABSString.fromString("request.method");
        value = ABSString.fromString(httpMethod);
        absRequestPropertyMap = this.addPair(absRequestPropertyMap, key, value);

        //context path
        String contextPath = this.request.getContextPath();
        key = ABSString.fromString("request.contextPath");
        value = ABSString.fromString(contextPath);
        absRequestPropertyMap = this.addPair(absRequestPropertyMap, key, value);

        //add URI
        String requestURI = request.getRequestURI();
        key = ABSString.fromString("request.URI");
        value = ABSString.fromString(contextPath);
        absRequestPropertyMap = this.addPair(absRequestPropertyMap, key, value);

        //add route        
        key = ABSString.fromString("request.route");
        value = ABSString.fromString(this.getRoute(requestURI, contextPath));
        absRequestPropertyMap = this.addPair(absRequestPropertyMap, key, value);

        ABSHttpRequest_i absHttpRequest = new ABSHttpRequestImpl_c(absRequestInputMap, absRequestPropertyMap);
        return absHttpRequest;
    }

    private ABS.StdLib.Map_InsertAssoc<ABSString, ABSString> addPair(ABS.StdLib.Map<ABSString, ABSString> absRequestInputMap, ABSString key, ABSString value) {
        Pair<ABSString, ABSString> methodPair = new Pair_Pair<ABSString, ABSString>(key, value);
        return new ABS.StdLib.Map_InsertAssoc<ABSString, ABSString>(methodPair, absRequestInputMap);
    }

    private String getRoute(String requestURI, String contextPath) {
        //add route
        String route = requestURI.substring(contextPath.length(), requestURI.length());
        if (route.length() == 0) {
            route = "/";
        }
        return route;
    }

}
