package com.Ecommerce.project.security.jwt;


import com.Ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    // LoggerFactory.getLogger(JwtUtils.class) → Ye JwtUtils class ke liye ek logger banata hai, taaki tumhari logs me clearly dikh sake kis class ne log print kiya.
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String  jwtCookie;

    @Value("${app.secure-cookie:false}")
    private boolean secureCookie;

    //   "HttpServletRequest ek interface hai jo client ke HTTP request ko represent karta hai. Iske through hum request
//   headers, parameters, method (GET/POST), body, cookies aur client-related information access kar sakte hain. Ye basically client aur server ke beech bridge ka kaam karta hai."

//    -------------------- GET JWT TOKEN FROM HEADER  FORMATE---------------------------------
//    in jwt token time --> jwt token will be passed to us in the form of header
//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header: {}", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove Bearer prefix
//        }
//        return null;
//    }
//----------------------------<<<<  getJWT  >>>>>>>>>>-------------------------------
//    ---------------------JWT COOKIE FORMATE----------------------------------
//    cookie is just used for storage purpose: it store the jwt in the cooki
//    other than cookie we have two more storage formate:  1))Browser Web App → ✅ Cookie-based JWT,  2)) React / Mobile App → ✅ Header + LocalStorage , 3) Microservices → ❌ Session
//Browser se cookie read karke JWT return karta hai. Filter/Interceptor me call hota hai.
    public String getJwtFromCookies(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);  //WebUtils.getCookie--> Spring ka helper function hai jo request me specific cookie name search karta hai.
        if (cookie!=null){
            return cookie.getValue();
        }else{
            return null;
        }

    }


//    .httpOnly(false)
//    HTTP-only flag define karta hai.
//false → JS code se access ho sakta hai (document.cookie)
//true → JS se access nahi hoga → sirf HTTP request me bheja ja sakta hai → more secure.
//    Usually JWT ke liye security reason se true recommended hota hai.

//JWT generate karke response cookie create karta hai. Login ke time call hota hai.
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt) //builder func h
//                .path("/api")
                .path("/")   // NOT /api

                .maxAge(24*60*60)
//                .httpOnly(false)
//                .secure(false)
                .httpOnly(true)                // SECURITY
                .secure(secureCookie)                      // ← hardcoded true tha, ab env se aayega
                .sameSite(secureCookie ? "None" : "Lax")   // ← hardcoded "None" tha, ab env se aayega
                .build();
        return  cookie;
    }


    public ResponseCookie getCleanCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,null) //builder func h
                .path("/api")
                .build();
        return  cookie;
    }



    //"UserDetails:::: ek contract hai jo batata hai ki user ke paas kaunse credentials aur authorities hain. Isme
// username, password, roles/authorities, aur account ki status (expired, locked, enabled) jaise details hoti hain.
// Spring Security internally isi interface ke objects use karta hai authentication aur authorization decisions ke liye."
//    public String generateTokenFromUsername(UserDetails userDetails) {
//        String username = userDetails.getUsername();   // User ka username nikalna
// Jwts actually JJWT library (io.jsonwebtoken) ka ek helper class hai jo JWT tokens ko banane, parse karne aur validate karne ke liye use hota hai.
// Commonly tum ise builder() aur parserBuilder() ke sath use karte ho.
    public String generateTokenFromUsername(String username){
        return Jwts.builder()                         // JWT builder start
                .subject(username)                    // JWT ka subject claim me username store karta hai.
                .issuedAt(new Date())                 // Token generate hone ka time
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Expiry time set
                .signWith(key())                      // Token ko secret/private key se digitally sign karta hai (verify karne ke liye).
                .compact();                           // Final token string banakar return
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())   // tumhari secret key
                .build()
                .parseSignedClaims(token)        // signed token ko parse karega
                .getPayload()                    // claims (payload) milega
                .getSubject();                   // subject se username return karega
    }




    //    jwtSecret → ek secret string hoti hai jo tumne config me rakhi hai (usually Base64 encoded).
//    Example:
//    jwt.secret=YXJzaF9zZWNyZXRfa2V5XzEyMzQ1Njc4OQ==
//     Decoders.BASE64.decode(jwtSecret) → secret string ko Base64 se decode karta hai aur raw bytes banata hai.
//     Keys.hmacShaKeyFor(...) → un bytes ka use karke ek HMAC-SHA key banata hai (HS256, HS512 jaisa algorithm ke liye).
//    Return → ye Key JWT ko sign karne aur verify karne dono me use hota hai.
//   -----------Generate signing Key---------------------------
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())   // secret key se token verify karega
                    .build()
                    .parseSignedClaims(authToken);   // token ko tod kar claims check karega
            return true;                             // agar sab sahi hai
        } catch (MalformedJwtException e) {
            logger.error("❌Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("❌JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("❌JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("❌JWT claims string is empty: {}", e.getMessage());
        }
        return false;  // agar koi bhi exception aaya toh token invalid
    }

}
