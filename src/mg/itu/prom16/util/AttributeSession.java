package mg.itu.prom16.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AttributeSession {
    HttpSession session;
    
    public void get(String key) {
        session.getAttribute(key);
    }

    public void add(String key, Object value) {
        session.setAttribute(key, value);
    }

    public void clear() {
        session.invalidate();
    }

    public void remove (String key) {
        session.removeAttribute(key);
    }

    public AttributeSession(HttpServletRequest request) {
        this.session = request.getSession();
    }
}
