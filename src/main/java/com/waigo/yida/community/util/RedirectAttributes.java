package com.waigo.yida.community.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * author waigo
 * create 2022-03-30 21:53
 */
@SuppressWarnings("unchecked")
public class RedirectAttributes {
    public static final String FLASH_ATTRIBUTES_KEY = "MY_REDIRECT_ATTRIBUTES_KEY";
    private HttpSession session;
    private Map<String, String> flashAttributes = new HashMap<>();

    public RedirectAttributes(HttpServletRequest request) {
        session = request.getSession(true);
    }

    public RedirectAttributes addFlashAttribute(String attributeName, String attributeValue) {
        flashAttributes.put(attributeName, attributeValue);
        return this;
    }

    public void completeAttributesSet() {
        session.setAttribute(FLASH_ATTRIBUTES_KEY, flashAttributes);
    }

    /**
     * 只能取一次，取完就从session中删去了
     * @return
     */
    public Map<String, String> getFlashAttributes() {
        Map<String, String> attribute = (Map<String, String>) session.getAttribute(FLASH_ATTRIBUTES_KEY);
        session.removeAttribute(FLASH_ATTRIBUTES_KEY);
        return attribute;
    }

}
