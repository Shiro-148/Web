package com.project.web.Controller;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.web.Entity.AccountEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.AddressService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {
    @Autowired
    private AccountService accountService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response) {
        AccountEntity account = accountService.getAccountByUsername(username);
        if (account == null) {
            account = accountService.getAccountByEmail(username);
        }

        if (account != null && account.getPassword().equals(password)) {
            String jwt = Jwts.builder()
                    .setSubject(account.getUsername())
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                    .compact();

            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);

            return "redirect:/";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        return "login";
    }

    @Autowired
    private AddressService addressService;

    @GetMapping("/account")
    public String getAccount(HttpServletRequest request, Model model) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        // load account and addresses for the logged-in user
        AccountEntity account = accountService.getAccountByUsername(username);
        if (account != null) {
            model.addAttribute("addresses", addressService.listByAccount(account));
            model.addAttribute("accountEntity", account);
        }
        return "account";
    }

    @GetMapping("/cart")
    public String getCart(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        return "cart";
    }
}