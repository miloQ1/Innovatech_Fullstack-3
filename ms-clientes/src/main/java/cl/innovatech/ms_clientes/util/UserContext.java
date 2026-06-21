package cl.innovatech.ms_clientes.util;





import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class UserContext {

    public static String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return (String) auth.getPrincipal();
        }
        return null;
    }
}
