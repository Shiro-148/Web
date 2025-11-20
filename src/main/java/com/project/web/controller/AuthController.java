package com.project.web.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.web.Entity.AccountEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.AddressService;
import com.project.web.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private static final Pattern PHONE_REGEX = Pattern.compile("^[0-9]{9,11}$");

    @Autowired
    private AccountService accountService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private AddressService addressService;

    @Autowired
    private com.project.web.Service.OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody Map<String, String> body,
            HttpServletResponse response) {
        String sdt = body.get("sdt");
        String password = body.get("password");

        if (sdt == null || password == null || sdt.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập đầy đủ thông tin");
        }

        AccountEntity account = accountService.getAccountByPhone(sdt);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Số điện thoại hoặc mật khẩu không đúng");
        }

        String stored = account.getPassword();
        boolean ok = false;
        if (stored != null) {
            if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                ok = passwordEncoder.matches(password, stored);
            } else {
                ok = stored.equals(password);
                if (ok) {
                    String encoded = passwordEncoder.encode(password);
                    accountService.updatePassword(account, encoded);
                }
            }
        }

        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Số điện thoại hoặc mật khẩu không đúng");
        }

        String jwt = jwtUtil.generateToken(account.getPhone());
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(cookie);

        return ResponseEntity.ok("Đăng nhập thành công");
    }

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String loginFormSubmit(@RequestParam Map<String, String> params,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        String username = params.getOrDefault("username", params.get("sdt"));
        String password = params.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin");
            redirectAttributes.addFlashAttribute("loginFailed", true);
            return "redirect:/";
        }

        AccountEntity account = accountService.getAccountByPhone(username);
        if (account == null)
            account = accountService.getAccountByEmail(username);

        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại hoặc mật khẩu không đúng");
            redirectAttributes.addFlashAttribute("loginFailed", true);
            return "redirect:/";
        }

        String stored = account.getPassword();
        boolean ok = false;
        if (stored != null) {
            if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                ok = passwordEncoder.matches(password, stored);
            } else {
                ok = stored.equals(password);
                if (ok) {
                    String encoded = passwordEncoder.encode(password);
                    accountService.updatePassword(account, encoded);
                }
            }
        }

        if (!ok) {
            redirectAttributes.addFlashAttribute("error", "Số điện thoại hoặc mật khẩu không đúng");
            redirectAttributes.addFlashAttribute("loginFailed", true);
            return "redirect:/";
        }

        String jwt = jwtUtil.generateToken(account.getPhone());
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Số điện thoại hoặc mật khẩu không đúng");
        }
        return "login";
    }

    @PostMapping(value = "/register", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody Map<String, String> body,
            HttpServletResponse response) {
        String fullName = body.get("fullName");
        String sdt = body.get("sdt");
        String password = body.get("password");

        if (fullName == null || sdt == null || password == null ||
                fullName.isBlank() || sdt.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body("Vui lòng điền đầy đủ thông tin");
        }

        if (!PHONE_REGEX.matcher(sdt).matches()) {
            return ResponseEntity.badRequest().body("Số điện thoại không hợp lệ (9-11 chữ số)");
        }

        if (accountService.phoneExists(sdt)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Số điện thoại đã được sử dụng");
        }

        AccountEntity account = accountService.createAccountByPhone(sdt, fullName, password);

        String jwt = jwtUtil.generateToken(account.getPhone());
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok("Đăng ký thành công");
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "msg", required = false) String msg,
            Model model) {
        if (error != null)
            model.addAttribute("error", error);
        if (msg != null)
            model.addAttribute("msg", msg);
        return "register";
    }

    @GetMapping("/account")
    public String getAccount(HttpServletRequest request, Model model) {
        String phone = (String) request.getAttribute("username");
        if (phone == null)
            return "redirect:/login";

        AccountEntity account = accountService.getAccountByPhoneWithFavorites(phone);
        if (account != null) {
            model.addAttribute("addresses", addressService.listByAccount(account));
            model.addAttribute("accountEntity", account);
            model.addAttribute("orders", orderService.listOrdersByAccount(account));
        }
        return "account";
    }

    @PostMapping(value = "/account/update", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateAccount(@RequestBody java.util.Map<String, String> body,
            HttpServletRequest request) {
        String phone = (String) request.getAttribute("username");
        if (phone == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không xác thực");

        AccountEntity account = accountService.getAccountByPhone(phone);
        if (account == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tài khoản không tồn tại");

        String fullName = body.get("fullName");
        String email = body.get("email");
        String birth = body.get("birthDate");
        LocalDate birthDate = null;
        if (birth != null && !birth.isBlank()) {
            try {
                birthDate = LocalDate.parse(birth);
            } catch (DateTimeParseException ex) {
                return ResponseEntity.badRequest().body("Định dạng ngày không hợp lệ (yyyy-MM-dd)");
            }
        }

        try {
            accountService.updateProfile(account, fullName, email, birthDate);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ");
        }
    }

    @GetMapping("/cart")
    public String getCart(HttpServletRequest request) {
        String phone = (String) request.getAttribute("username");
        if (phone == null)
            return "redirect:/login";
        return "cart";
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Đã đăng xuất");
    }
}
