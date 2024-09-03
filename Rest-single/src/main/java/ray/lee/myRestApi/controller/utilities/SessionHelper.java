package ray.lee.myRestApi.controller.utilities;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import ray.lee.myRestApi.common.pojo.UserCredential;
import ray.lee.myRestApi.common.utilities.MyRestConstants;

@Slf4j
public class SessionHelper {
    public static void setAttribute(HttpServletRequest request, String attributeName, Object objAttribute) throws Exception {
        setAttribute(request, MyRestConstants.SESS_ATTR_COMMON, attributeName, objAttribute);
    }

    @SuppressWarnings("unchecked")
    public static void setAttribute(HttpServletRequest request, String useCaseId, String attributeName, Object objAttribute) {
        try {
            HttpSession session = request.getSession();
            if(session == null) {
            	throw new IllegalStateException("session timeout!");	
            }
            Map hstAttrs = (Map)session.getAttribute(useCaseId);
            if(hstAttrs == null) {
            	hstAttrs = new HashMap();
            }
            if(hstAttrs.containsKey(attributeName)) {
            	hstAttrs.remove(attributeName);
            }

            hstAttrs.put(attributeName, objAttribute);
            session.setAttribute(useCaseId, hstAttrs);
        } catch (Exception e) {
            log.debug("setting session attribute is failed:" + e, e);
        }
    }

    public static void removeAttribute(HttpServletRequest request, String attributeName) {
        removeAttribute(request, MyRestConstants.SESS_ATTR_COMMON, attributeName);
    }

    @SuppressWarnings("unchecked")
    public static void removeAttribute(HttpServletRequest request, String useCaseId, String attributeName) {
        try {
            Map hstAttrs = getAllAttribute(request, useCaseId);
            if(hstAttrs != null && hstAttrs.containsKey(attributeName)) {
            	hstAttrs.remove(attributeName);	
            }
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getAttribute(HttpServletRequest request, String attributeName) {
        return getAttribute(request, MyRestConstants.SESS_ATTR_COMMON, attributeName);
    }

    @SuppressWarnings("unchecked")
    public static Object getAttribute(HttpServletRequest request, String useCaseId, String attributeName) {
        try {
            Map hstAttrs = getAllAttribute(request, useCaseId);
            if(hstAttrs != null && hstAttrs.containsKey(attributeName)) {
                return hstAttrs.get(attributeName);
            }
        } catch (Throwable e) {
            log.debug("setting session attribute is failed:" + e, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map getAllAttribute(HttpServletRequest request, String useCaseId) throws Exception {
        try {
            HttpSession session = request.getSession(false);
            if(session == null) {
            	log.debug("SessionHelper.getUserSession() : java.lang.IllegalStateException: session timeout!");
            	return null;
            }
            return (Map)session.getAttribute(useCaseId);
        } catch (Throwable e) {
            log.debug("setting session attribute is failed:" + e, e);
            throw new Exception(e);
        }
    }

    public static void clearSession(HttpServletRequest request) {
        clearSession(request, null);
    }

    @SuppressWarnings("unchecked")
    public static void clearSession(HttpServletRequest request, String useCaseId) {
        try {
            HttpSession session = request.getSession();
            Enumeration attrs = session.getAttributeNames();
            String attrName = "";
            while(attrs.hasMoreElements()) {
                attrName = (String)attrs.nextElement();
                if(attrName != null) {
                    if((StringUtils.hasText(useCaseId) && attrName.equalsIgnoreCase(useCaseId))) {
                    	continue;	
                    }
                    try {
                        session.removeAttribute(attrName);
                    } catch (Exception e) {
                        log.debug("clear session exception:" + e);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public static UserCredential getUserSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if(session == null) {
            	log.debug("SessionHelper.getUserSession() : java.lang.IllegalStateException: session timeout!");
            	return null;
            }
            return (UserCredential)session.getAttribute(MyRestConstants.SESS_ATTR_USER_SESSION);
        } catch (Exception e) {
            log.debug("getting session attribute is failed:" + e);
        }
        return null;
    }

    public static void setUserSession(HttpServletRequest request, UserCredential user) {
        try {
            request.getSession().setAttribute(MyRestConstants.SESS_ATTR_USER_SESSION, user);
        } catch (Exception e) {
            log.debug("setting session attribute is failed:" + e, e);
        }
    }

    public static void invalidate(HttpServletRequest request) {
        try {
            request.getSession().invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
