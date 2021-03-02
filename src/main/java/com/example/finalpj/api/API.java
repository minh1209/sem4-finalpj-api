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
    @GetMapping("/user/get")
    public ResponseEntity<?> getUserById(@RequestParam String id) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else {
            result.put("message", "ok");
            result.put("data", user.get());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/get-username")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        result = new HashMap<>();
        Optional<User> user = userService.findByUsername(username);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else {
            result.put("message", "ok");
            result.put("data", user.get());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        result = new HashMap<>();
        boolean isAdmin = false;
        Optional<User> user = userService.findByEmail(email);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else if (!user.get().getActive()) {
            result.put("message", "Email have not activated.");
        } else if (!encoder().matches(password, user.get().getPassword())) {
            result.put("message", "Wrong email or password.");
        } else {
            for (Role role : user.get().getRoles()) {
                if (role.getName().equals("role_admin")) {
                    isAdmin = true;
                }
            }
            if (isAdmin) {
                result.put("message", "Admin account is not allowed.");
            } else {
                result.put("message", "Log in successfully.");
                result.put("data", user.get());
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        result = new HashMap<>();
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            result.put("message", "Email is existed.");
            result.put("data", false);
        } else {
            result.put("message", "Email is available.");
            result.put("data", true);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        result = new HashMap<>();
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            result.put("message", "Username has been used.");
            result.put("data", false);
        } else {
            result.put("message", "Username is available.");
            result.put("data", true);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> register(@Valid @RequestBody User u, BindingResult bindingResult) {
        result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            result.put("message", "Wrong information.");
        } else {
            u.setPassword(encoder().encode(u.getPassword()));
            Optional<Role> role = roleService.findByName("role_user");
            List<Role> roles = new ArrayList<>();
            if (!role.isPresent()) {
                Role new_role = new Role();
                new_role.setName("role_user");
                roles.add(roleService.save(new_role));
            } else {
                roles.add(role.get());
            }
            u.setRoles(roles);

            result.put("data", userService.save(u));
            result.put("message", "Registration successfully.");
            createVerification(u.getEmail());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/verify")
    public ResponseEntity<?> sendVerification(@RequestParam String email) {
        result = new HashMap<>();
        Optional<User> user = userService.findByEmail(email);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else if (user.get().getActive()) {
            result.put("message", "Account has already been activated.");
        } else {
            createVerification(email);
            result.put("message", "Send email successfully.");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String code) {
        result = new HashMap<>();
        Optional<Token> findToken = tokenService.findByToken(code);
        if (!findToken.isPresent()) {
            result.put("message", "Code is not available.");
        } else if (findToken.get().getExpiredDateTime().isBefore(LocalDateTime.now())) {
            result.put("message", "Expired code.");
        } else {
            Optional<User> user = userService.findById(findToken.get().getUser().getId());
            if (!user.isPresent()) {
                result.put("message", "User is not existed.");
            } else if (user.get().getActive()) {
                result.put("message", "Account has already been activated.");
            } else {
                user.get().setActive(true);
                result.put("data", userService.save(user.get()));
                result.put("message", "Account activation successfully.");
            }
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/edit")
    public ResponseEntity<?> editUser(@RequestParam String id, @Valid @RequestBody User u, BindingResult bindingResult) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "User is not existed");
        } else if (bindingResult.hasErrors()) {
            result.put("message", "Wrong information.");
        } else {
            u.setId(id);
            u.setCreateAt(user.get().getCreateAt());
            u.setActive(user.get().getActive());
            u.setPassword(user.get().getPassword());
            result.put("message", "Edit successfullly.");
            result.put("data", userService.save(u));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/edit-password")
    public ResponseEntity<?> editUserPassword(@RequestParam String id, @RequestParam String password) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else {
            user.get().setPassword(encoder().encode(password));
            result.put("message", "Update password successfully.");
            result.put("data", userService.save(user.get()));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/user/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam String id, @RequestParam String avatar) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else {
            user.get().setAvatar(avatar);
            result.put("message", "Update avatar successfully.");
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

    @GetMapping("/user/search")
    public ResponseEntity<?> userSearch(@RequestParam String username) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", userService.findAllByUsernameLike(username));
        return ResponseEntity.ok(result);
    }

    //  Song------------------------------------------------------------------------------------------------------------
    @GetMapping("/song/all")
    public ResponseEntity<?> findAllSong(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAll(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/category")
    public ResponseEntity<?> findAllSongCategoryName(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam String category) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAllByCategory_Name(page, size, category));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/get")
    public ResponseEntity<?> getSong(@RequestParam(required = false) String id) {
        result = new HashMap<>();
        if (!id.isEmpty()) {
            Optional<Song> song = songService.findById(id);
            if (!song.isPresent()) {
                result.put("message", "Beat is not existed.");
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
            result.put("message", "Beat is not existed.");
        } else if (bindingResult.hasErrors()) {
            result.put("message", "Wrong information.");
        } else {
            s.setId(id);
            s.setCreateAt(song.get().getCreateAt());
            result.put("message", "Edit beat successfully.");
            result.put("data", songService.save(s));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/song/add")
    public ResponseEntity<?> addSong(@Valid @RequestBody Song s, BindingResult bindingResult) {
        result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            result.put("message", "Wrong information.");
        } else {
            result.put("message", "Add beat successfully.");
            result.put("data", songService.save(s));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/song/demo")
    public ResponseEntity<?> updateDemo(@RequestParam String demo, @RequestParam String id) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            result.put("message", "Beat is not existed.");
        } else {
            song.get().setDemo(demo);
            result.put("message", "Update demo beat successfully.");
            result.put("data", songService.save(song.get()));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/song/main")
    public ResponseEntity<?> updateMain(@RequestParam String main, @RequestParam String id) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(id);
        if (!song.isPresent()) {
            result.put("message", "Beat is not existed.");
        } else {
            song.get().setMain(main);
            result.put("message", "Update main beat successfully.");
            result.put("data", songService.save(song.get()));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/top-6")
    public ResponseEntity<?> getFirst6ByCreate() {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findTop6ByOrderByCreate_atDesc());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/search")
    public ResponseEntity<?> songSearch(@RequestParam String name) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAllByNameLike(name));
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
    public ResponseEntity<?> addCategory(@RequestBody @Valid Category c, BindingResult bindingResult) {
        result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            result.put("message", "Wrong information.");
        } else {
            result.put("message", "Add category successfully.");
            result.put("data", categoryService.save(c));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/category/get")
    public ResponseEntity<?> getCategoryById(@RequestParam String id) {
        result = new HashMap<>();
        Optional<Category> category = categoryService.findById(id);
        if (!category.isPresent()) {
            result.put("message", "Category is not existed.");
        } else {
            result.put("message", "ok");
            result.put("data", category.get());
        }
        return ResponseEntity.ok(result);
    }


    //  Transaction-----------------------------------------------------------------------------------------------------
    @GetMapping("/transaction/customer")
    public ResponseEntity<?> getAllTransactionWithCustomer(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam String id) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAllByCustomerIdOrderByCreateAtDesc(id, page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transaction/song")
    public ResponseEntity<?> getAllTransactionWithSong(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size, @RequestParam String id) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAllBySongIdOrderByCreateAtDesc(id, page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transaction/get")
    public ResponseEntity<?> getTransactionById(@RequestParam String id) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findById(id));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/transaction/add")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction t) {
        result = new HashMap<>();
        Optional<Song> song = songService.findById(t.getSong().getId());
        Optional<User> user = userService.findById(t.getCustomer().getId());
        Optional<Transaction> transactionCheck = transactionService.findBySongIdAndCustomerId(t.getSong().getId(), t.getCustomer().getId());
        if (!song.isPresent() || !user.isPresent() || transactionCheck.isPresent()) {
            result.put("message", "Transaction failed.");
            result.put("status", false);
        } else {
            transactionService.save(t);
            result.put("message", "Transaction successfully.");
            result.put("status", true);
        }

        return ResponseEntity.ok(result);
    }


    //  Admin-----------------------------------------------------------------------------------------------------------
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestParam String email, @RequestParam String password) {
        result = new HashMap<>();
        boolean isAdmin = false;
        Optional<User> user = userService.findByEmail(email);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else if (!user.get().getActive()) {
            result.put("message", "User is not activated.");
        } else if (!encoder().matches(password, user.get().getPassword())) {
            result.put("message", "Wrong email or password.");
        } else {
            for (Role role : user.get().getRoles()) {
                if (role.getName().equals("role_admin")) {
                    isAdmin = true;
                }
            }
            if (!isAdmin) {
                result.put("message", "User do not have permissions.");
            } else {
                result.put("message", "Log in successfully.");
                result.put("data", user.get());
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/user/all")
//    public ResponseEntity<?> findAllUser(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
    public ResponseEntity<?> findAllUser() {
        result = new HashMap<>();
        result.put("message", "ok");
//        result.put("data", userService.findAll(page, size));
//        result.put("data", userService.findAll());
        result.put("data", userService.findAllUserNotAdmin());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/admin/user/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id) {
        result = new HashMap<>();
        Optional<User> user = userService.findById(id);
        if (!user.isPresent()) {
            result.put("message", "User is not existed.");
        } else {
            userService.deleteById(id);
            result.put("message", "Delete user successfully.");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/transaction/all")
    public ResponseEntity<?> findAllTransaction(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAll(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/song/revenue-category")
    public ResponseEntity<?> findAllSongByCategoryAndTime(@RequestParam String id, @RequestParam java.sql.Date start, @RequestParam java.sql.Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAllByCategoryIdAndTransactions_CreateAtBetween(id, start, end));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/transaction/timefilter")
    public ResponseEntity<?> findAllTransactionWithTimeFilter(@RequestParam(required = false) String id, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        if (id == null) {
            result.put("data", transactionService.findAllByCreateAtBetweenOrderByCreateAtDesc(start, end));
        } else {
            result.put("data", transactionService.findAllBySong_Creator_IdAndCreateAtBetweenOrderByCreateAtDesc(start, end, id));
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/admin/transaction/author-payment")
    public ResponseEntity<?> updateAuthorPayment(@RequestParam String id) {
        Optional<Transaction> transaction = transactionService.findById(id);
        if (!transaction.isPresent()) {
            result.put("message", "Transaction is not existed.");
        } else if (transaction.get().getAuthorPayment()) {
            result.put("message", "Transaction has already been paid to musician.");
        } else {
            transaction.get().setAuthorPayment(true);
            transactionService.save(transaction.get());
            result.put("message", "Transaction is paid successfully.");
        }
        return ResponseEntity.ok(result);
    }


    //  Function--------------------------------------------------------------------------------------------------------

    private void createVerification(String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            Optional<Token> findToken = tokenService.findByUserId(user.get().getId());
            Token token;
            if (!findToken.isPresent()) {
                token = new Token();
                token.setUser(user.get());
                tokenService.save(token);
                user.get().setToken(token);
                userService.save(user.get());
            } else {
                if (findToken.get().getExpiredDateTime().isBefore(LocalDateTime.now())) {
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
