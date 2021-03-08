package com.example.finalpj.api;

import com.example.finalpj.dto.TransactionChildrenDto;
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
    public ResponseEntity<?> getUser(@RequestParam(required = false, defaultValue = "") String id,
                                     @RequestParam(required = false, defaultValue = "") String email,
                                     @RequestParam(required = false, defaultValue = "") String username) {
        result = new HashMap<>();
        result.put("message", "ok");
        if (!id.isEmpty()) {
            result.put("data", userService.findDtoById(id));
        } else if (!email.isEmpty()) {
            result.put("data", userService.findDtoByEmail(email));
        } else if (!username.isEmpty()) {
            result.put("data", userService.findDtoByUsername(username));
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
                result.put("data", userService.findDtoByEmail(email));
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

    @GetMapping("/user/search")
    public ResponseEntity<?> userSearch(@RequestParam String username) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", userService.findAllDtoByUsernameSearch(username));
        return ResponseEntity.ok(result);
    }

    //  Song------------------------------------------------------------------------------------------------------------
    @GetMapping("/song/get")
    public ResponseEntity<?> getSongDto(@RequestParam(required = false, defaultValue = "") String id,
                                        @RequestParam(required = false, defaultValue = "") String creator_id,
                                        @RequestParam(required = false, defaultValue = "") String category_id) {
        result = new HashMap<>();
        result.put("message", "ok");

        if (!id.isEmpty()) {
            result.put("data", songService.findDtoById(id));
        } else {
            if (!creator_id.isEmpty()) {
                if (!category_id.isEmpty()) {
                    result.put("data", songService.findAllDtoByCreatorAndCategory(creator_id, category_id));
                } else {
                    result.put("data", songService.findAllDtoByCreator(creator_id));
                }
            } else {
                if (!category_id.isEmpty()) {
                    result.put("data", songService.findAllDtoByCategory(category_id));
                } else {
                    result.put("data", songService.findAllDto());
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/get-pageable")
    public ResponseEntity<?> getSongDtoPageable(@RequestParam(required = false, defaultValue = "") String creator_id,
                                                @RequestParam(required = false, defaultValue = "") String category_id,
                                                @RequestParam(required = false, defaultValue = "0") Integer page,
                                                @RequestParam(required = false, defaultValue = "5") Integer size) {
        result = new HashMap<>();
        result.put("message", "ok");

        if (!creator_id.isEmpty()) {
            if (!category_id.isEmpty()) {
                result.put("data", songService.findAllDtoByCreatorAndCategoryPage(creator_id, category_id, page, size));
            } else {
                result.put("data", songService.findAllDtoByCreatorPage(creator_id, page, size));
            }
        } else {
            if (!category_id.isEmpty()) {
                result.put("data", songService.findAllDtoByCategoryPage(category_id, page, size));
            } else {
                result.put("data", songService.findAllDtoPage(page, size));
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/get/category-transaction")
    public ResponseEntity<?> getSongDtoTransactions(@RequestParam String category_id,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findallDtoByCategoryAndTransactionTime(category_id, start, end));
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
        result.put("data", songService.findTop6NewestDto());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/song/search")
    public ResponseEntity<?> songSearch(@RequestParam String name) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", songService.findAllDtoBySearchName(name));
        return ResponseEntity.ok(result);
    }

    //  Category--------------------------------------------------------------------------------------------------------
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
    public ResponseEntity<?> getCategoryDto(@RequestParam(required = false, defaultValue = "") String id) {
        result = new HashMap<>();
        result.put("message", "ok");

        if (!id.isEmpty()) {
            result.put("data", categoryService.findDtoById(id));
        } else {
            result.put("data", categoryService.findAllDto());
        }
        return ResponseEntity.ok(result);
    }

    //  Transaction-----------------------------------------------------------------------------------------------------
    @GetMapping("/transaction/get")
    public ResponseEntity<?> getTransactions(@RequestParam(required = false, defaultValue = "") String id,
                                             @RequestParam(required = false, defaultValue = "") String creator_id,
                                             @RequestParam(required = false, defaultValue = "") String song_id,
                                             @RequestParam(required = false, defaultValue = "") String customer_id,
                                             @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                             @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        if (!id.isEmpty()) {
            result.put("data", transactionService.findDtoById(id));
        } else {
            if (!creator_id.isEmpty()) {
                if (start == null || end == null) {
                    result.put("data", transactionService.findAllDtoByCreator(creator_id));
                } else {
                    result.put("data", transactionService.findAllDtoByCreatorAndTime(creator_id, start, end));
                }
            } else if (!song_id.isEmpty()) {
                if (start == null || end == null) {
                    result.put("data", transactionService.findAllDtoBySong(song_id));
                } else {
                    result.put("data", transactionService.findAllDtoBySongAndTime(song_id, start, end));
                }
            } else if (!customer_id.isEmpty()) {
                if (start == null || end == null) {
                    result.put("data", transactionService.findAllDtoByCustomer(customer_id));
                } else {
                    result.put("data", transactionService.findAllDtoByCustomerAndTime(customer_id, start, end));
                }
            } else {
                result.put("data", null);
            }
        }
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

    @GetMapping("/transaction/check")
    public ResponseEntity<?> check(@RequestParam String customer_id, @RequestParam String song_id) {
        result = new HashMap<>();
        result.put("message", "ok");
        Optional<TransactionChildrenDto> t = transactionService.checkUserPurchasedSong(customer_id, song_id);
        if (!t.isPresent()) {
            result.put("data", false);
        } else {
            result.put("data", true);
        }
        return ResponseEntity.ok(result);
    }

    //  Admin-----------------------------------------------------------------------------------------------------------
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestParam String username, @RequestParam String password) {
        result = new HashMap<>();
        boolean isAdmin = false;
        Optional<User> user = userService.findByUsername(username);
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

    @GetMapping("/admin/user/get")
    public ResponseEntity<?> adminGetUserDto(@RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                             @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        if(start == null || end == null) {
            result.put("data", userService.findAllDtoNotAdmin());
        } else {
            result.put("data", userService.findAllDtoNotAdminAndTime(start, end));
        }
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

    @GetMapping("/admin/song/get")
    public ResponseEntity<?> adminGetSongDto(@RequestParam(required = false, defaultValue = "") String creator_id,
                                             @RequestParam(required = false, defaultValue = "") String category_id,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        if (!creator_id.isEmpty()) {
            if (!category_id.isEmpty()) {
                result.put("data", songService.findAllDtoByCreatorAndCategoryAndTime(creator_id, category_id, start, end));
            } else {
                result.put("data", songService.findallDtoByCreatorAndTime(creator_id, start, end));
            }
        } else {
            if (!category_id.isEmpty()) {
                result.put("data", songService.findallDtoByCategoryAndTime(category_id, start, end));
            } else {
                result.put("data", null);
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/transaction/get")
    public ResponseEntity<?> adminGetTransactions(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        result = new HashMap<>();
        result.put("message", "ok");
        result.put("data", transactionService.findAllDtoByTime(start, end));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/admin/transaction/author-payment")
    public ResponseEntity<?> updateAuthorPayment(@RequestParam String id) {
        result = new HashMap<>();
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
