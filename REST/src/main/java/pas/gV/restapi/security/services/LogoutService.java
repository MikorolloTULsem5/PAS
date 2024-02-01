//package pas.gV.restapi.security.services;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@FieldDefaults(makeFinal = true)
//public class LogoutService implements LogoutHandler {
//
//    private JwtService jwtService;
//    private UserDetailsService userDetailsService;
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        String userLogin = jwtService.extractUsername(token);
//
//        if (jwtService.isTokenValid(token, userDetailsService.loadUserByUsername(userLogin))) {
//            SecurityContextHolder.clearContext();
//        }
//    }
//}
