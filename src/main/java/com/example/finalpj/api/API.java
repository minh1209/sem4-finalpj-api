package com.example.finalpj.api;

import com.example.finalpj.entity.*;
import com.example.finalpj.service.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import freemarker.template.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping
public class API {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SongService songService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private Configuration template;

    private BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    private HashMap<String, Object> result;


    //  User------------------------------------------------------------------------------------------------------------
    @GetMapping("/user/all")
    public ResponseEntity<?> findAllUser(@RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "10") int size) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", userService.findAll(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/get")
    public ResponseEntity<?> getUserById(@RequestParam String id) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else {
            result.put("message", "ok");
            result.put("data", user.get());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        result = new HashMap<>();
        Optional<User> user = userService.findByEmail(email);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else if (!user.get().getActive()) {
            result.put("message", "Người dùng chưa xác minh qua email.");
        } else if (!encoder().matches(password, user.get().getPassword())) {
            result.put("message", "Sai email hoặc mật khẩu.");
        } else {
            result.put("message", "Đăng nhập thành công.");
            result.put("data", user.get());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/check")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        result = new HashMap<>();
        Optional<User> user = userService.findByEmail(email);
        if(user.isPresent()) {
            result.put("message", "Email đã được sử dụng.");
            result.put("data", false);
        } else {
            result.put("message", "Email khả dụng.");
            result.put("data", true);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> register(@Valid @RequestBody User u, BindingResult bindingResult) {
        result = new HashMap<>();
        if(bindingResult.hasErrors()) {
            result.put("message", "Thông tin chưa đúng.");
        } else {
            u.setPassword(encoder().encode(u.getPassword()));
            Optional<Role> role = roleService.findByName("role_user");
            List<Role> roles = new ArrayList<>();
            if(!role.isPresent()) {
                Role new_role = new Role();
                new_role.setName("role_user");
                roles.add(roleService.save(new_role));
            } else {
                roles.add(role.get());
            }
            u.setRoles(roles);

            result.put("data", userService.save(u));
            result.put("message", "Đăng ký thành công.");
            createVerification(u.getEmail());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/verify")
    public ResponseEntity<?> sendVerification(@RequestParam String email) {
        result = new HashMap<>();
        Optional<User> user = userService.findByEmail(email);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else if (user.get().getActive()) {
            result.put("message", "Tài khoản đã được xác thực.");
        } else {
            createVerification(email);
            result.put("message", "Gửi email xác nhận thành công.");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String code) {
        result = new HashMap<>();
        Optional<Token> findToken = tokenService.findByToken(code);
        if(!findToken.isPresent()) {
            result.put("message", "Mã xác thực không khả dụng.");
        } else if (findToken.get().getExpiredDateTime().isBefore(LocalDateTime.now())) {
            result.put("message", "Mã xác thực đã hết hạn sử dụng.");
        } else {
            Optional<User> user = userService.findById(findToken.get().getUser().getId());
            if (!user.isPresent()) {
                result.put("message", "Người dùng không tồn tại.");
            } else if (user.get().getActive()) {
                result.put("message", "Tài khoản đã được xác thực.");
            } else {
                user.get().setActive(true);
                result.put("data", userService.save(user.get()));
                result.put("message", "Xác thực thành công.");
            }
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/edit")
    public ResponseEntity<?> editUser(@RequestParam String id, @Valid @RequestBody User u, BindingResult bindingResult) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else if (bindingResult.hasErrors()) {
            result.put("message", "Thông tin chưa đúng.");
        } else {
            u.setId(id);
            u.setCreateAt(user.get().getCreateAt());
            u.setActive(user.get().getActive());
            u.setPassword(user.get().getPassword());
            result.put("message", "Cập nhật thông tin thành công.");
            result.put("data", userService.save(u));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/edit-password")
    public ResponseEntity<?> editUserPassword(@RequestParam String id, @RequestParam String password) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else {
            user.get().setPassword(encoder().encode(password));
            result.put("message", "Cập nhật mật khẩu thành công.");
            result.put("data", userService.save(user.get()));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam String id, @RequestParam String avatar) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if(!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else {
            user.get().setAvatar(avatar);
            result.put("message", "Cập nhật ảnh đại diện thành công.");
            result.put("data", userService.save(user.get()));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/top-20")
    public ResponseEntity<?> getTop20() {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", userService.findTop20UserGetPaid());
        return ResponseEntity.ok(result);
    }


    //  Song------------------------------------------------------------------------------------------------------------
    @GetMapping("/song/all")
    public ResponseEntity<?> findAllSong(@RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "10") int size) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAll(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/get")
    public ResponseEntity<?> getSong(@RequestParam(required = false) String id) {
        result = new HashMap<>();
        if(!id.isEmpty()) {
            Optional<Song> song = songService.findById(id);
            if (!song.isPresent()) {
                result.put("message", "Beat không tồn tại.");
            } else {
                result.put("message", "ok");
                result.put("data", song.get());
            }
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/song/edit")
    public ResponseEntity<?> editSong(@RequestParam String id, @Valid @RequestBody Song s, BindingResult bindingResult) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            result.put("message", "Không tìm thấy beat.");
        } else if(bindingResult.hasErrors()) {
            result.put("message", "Thông tin chưa đúng hoặc chưa đủ.");
        } else {
            s.setId(id);
            s.setCreateAt(song.get().getCreateAt());
            result.put("message", "Cập nhật thông tin thành công.");
            result.put("data", songService.save(s));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/song/add")
    public ResponseEntity<?> addSong(@Valid @RequestBody Song s, BindingResult bindingResult) {
        result = new HashMap<>();
        if(bindingResult.hasErrors()) {
            result.put("message", "Thông tin chưa đúng hoặc chưa đủ.");
        } else {
            result.put("message", "Thêm beat thành công.");
            result.put("data", songService.save(s));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/song/demo")
    public ResponseEntity<?> updateDemo(@RequestParam String demo, @RequestParam String id) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(id);
        if(!song.isPresent()) {
            result.put("message", "Beat không tồn tại.");
        } else {
            song.get().setDemo(demo);
            result.put("message", "Cập nhật demo beat thành công.");
            result.put("data", songService.save(song.get()));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/song/main")
    public ResponseEntity<?> updateMain(@RequestParam String main, @RequestParam String id) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(id);
        if(!song.isPresent()) {
            result.put("message", "Beat không tồn tại.");
        } else {
            song.get().setMain(main);
            result.put("message", "Cập nhật main beat thành công.");
            result.put("data", songService.save(song.get()));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/top-6")
    public ResponseEntity<?> getFirst6ByCreate() {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findTop6ByStatusOrderByCreateAtDesc());
        return ResponseEntity.ok(result);
    }


    //  Category--------------------------------------------------------------------------------------------------------
    @GetMapping("/category/all")
    public ResponseEntity<?> getAll() {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", categoryService.findAll());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/category/add")
    public ResponseEntity<?> save(@RequestBody @Valid Category c, BindingResult bindingResult) {
        result = new HashMap<>();
        if(bindingResult.hasErrors()) {
            result.put("message", "Thông tin chưa đúng hoặc chưa đủ.");
        } else {
            result.put("message", "Thêm thể loại thành công.");
            result.put("data", categoryService.save(c));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category/get")
    public ResponseEntity<?> getById(@RequestParam String id) {
        result = new HashMap<>();
        Optional<Category> category = categoryService.findById(id);
        if(!category.isPresent()) {
            result.put("message", "Thể loại không tồn tại.");
        } else {
            result.put("message", "ok");
            result.put("data", category.get().getName());
        }
        return ResponseEntity.ok(result);
    }


    //  Transaction-----------------------------------------------------------------------------------------------------
    @GetMapping("/transaction/customer")
    public ResponseEntity<?> getAllTransactionWithCustomer(@RequestParam(required = false, defaultValue = "0") int page,
                                                @RequestParam(required = false, defaultValue = "10") int size,
                                                @RequestParam String id) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAllByCustomerId(page, size, id));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transaction/add")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction t) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(transactionService.save(t).getSong().getId());
        if(!song.isPresent()) {
            transactionService.deleteById(t.getId());
            result.put("message", "Giao dịch thất bại.");
            result.put("status", false);
        } else {
            song.get().setStatus(false);
            song.get().setTransaction(t);
            songService.save(song.get());
            result.put("message", "Xử lý giao dịch thành công.");
            result.put("status", true);
        }

        return ResponseEntity.ok(result);
    }



    //  Admin-----------------------------------------------------------------------------------------------------------
    @DeleteMapping("/admin/user/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "Người dùng không tồn tại.");
        } else {
            userService.deleteById(id);
            result.put("message", "Xoá người dùng thành công.");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/transaction/all")
    public ResponseEntity<?> findAllTransaction(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAll(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/transaction/timefilter")
    public ResponseEntity<?> findAllTransactionWithTimeFilter(@RequestParam(required = false) String id,
                                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        if(id == null) {
            result.put("data", transactionService.findAllByCreateAtBetween(start, end));
        } else {
            result.put("data", transactionService.findAllBySong_Creator_IdAndCreateAtBetween(start, end, id));
        }
        return ResponseEntity.ok(result);
    }


    //  Function--------------------------------------------------------------------------------------------------------

    private void createVerification(String email) {
        Optional<User> user = userService.findByEmail(email);
        if(user.isPresent()) {
            Optional<Token> findToken = tokenService.findByUserId(user.get().getId());
            Token token;
            if (!findToken.isPresent()) {
                token = new Token();
                token.setUser(user.get());
                tokenService.save(token);
            } else {
                if(findToken.get().getExpiredDateTime().isBefore(LocalDateTime.now())) {
                    token = new Token();
                    token.setId(findToken.get().getId());
                    token.setUser(user.get());
                    tokenService.save(token);
                } else {
                    token = findToken.get();
                }
            }
            sendVerificationMail(email, token.getToken());
        }
    }

    private void sendVerificationMail(String toEmail, String verificationCode) {
        String URL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
        String subject = "Please verify your email";
        String body = "";
        try {
            Template t = template.getTemplate("email-verification.ftl");
            Map<String, String> map = new HashMap<>();
            map.put("VERIFICATION_URL", URL + "/user/verify?code=" + verificationCode);
            body = FreeMarkerTemplateUtils.processTemplateIntoString(t, map);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        sendMail(toEmail, subject, body);
    }

    private void sendMail(String toEmail, String subject, String body) {
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.debug", "true");
        Session session = Session.getDefaultInstance(properties);
//        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("starsecurities1@gmail.com", "FPT-Aptech-T1811E"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport transport = session.getTransport();
            transport.connect("smtp.gmail.com", "starsecurities1@gmail.com", "Starsecurity@123");
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
