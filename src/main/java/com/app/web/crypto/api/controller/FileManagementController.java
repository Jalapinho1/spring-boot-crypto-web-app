package com.app.web.crypto.api.controller;

import com.app.web.crypto.api.model.*;
import com.app.web.crypto.api.payload.CommentRequest;
import com.app.web.crypto.api.repository.UserRepository;
import com.app.web.crypto.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api/file")
public class FileManagementController {

    private final int SECRET_KEY_LENGTH = 256;

    @Autowired
    private UserService userService;

    @Autowired
    private KeyService keyService;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private FileMetadataService fileMetadataService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    @PostMapping(value = "/send")
    public boolean saveFile(HttpServletRequest request,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam("receiver") String receiverUsername,
                            @RequestParam("sender") String senderUsername) throws Exception {

        // Check if user with this username exists, if not notice client
        User receiver = userService.findByName(receiverUsername);
        if (receiver == null) {
            return false;
        }

        String receiverPublicKey = keyService.getUserPublickey(receiverUsername);
        if (receiverPublicKey == null) {
            return false;
        }
        byte[] secretKey = cryptoService.generateSecretKey();
        byte[] encryptedSecretKey = cryptoService.encryptSecretKey(secretKey, receiverPublicKey);
        byte[] encFileBytes = cryptoService.encryptFileData(file, secretKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(encryptedSecretKey);
        outputStream.write(encFileBytes);

        byte[] encryptedSecretKeyArr = new byte[SECRET_KEY_LENGTH];
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        is.read(encryptedSecretKeyArr, 0, encryptedSecretKeyArr.length);

        MultipartFile result = new MockMultipartFile(file.getName(),
                file.getOriginalFilename(), file.getContentType(), outputStream.toByteArray());

        // Get upper level of current directory
//        String filePath = null;
//        try{
//            filePath = Paths.get("C:\\Users\\edoma\\data\\cryptoBackend\\" + file.getOriginalFilename()).toString();
//        }catch (Exception e) {
//            Logger.getLogger("Setting XML parsing error!" + filePath  + " " + uploadDir).log(Level.SEVERE, filePath + " " + uploadDir, e);
//        }

        //PROD path on server
        String filePath = null;
        try{
            filePath = "/home/" + file.getOriginalFilename();
        }catch (Exception e) {
            Logger.getLogger("Setting XML parsing error!" + filePath  + " " + uploadDir).log(Level.SEVERE, filePath + " " + uploadDir, e);
        }

        try {
            result.transferTo(new File(filePath));
        } catch (IOException e) {
            // TODO: log exception
            System.out.println(e);
        }

        FileMetadata fm = new FileMetadata();
        fm.setFilePath(filePath);
        fm.setFilename(file.getOriginalFilename());
        fm.setSenderUsername(senderUsername);
        fm.getReceivers().add(receiver);

        fileMetadataService.save(fm);

        return true;
    }

    @PutMapping(value = "/update-receivers")
    public void updateReceivers(@RequestBody Map<String, String> payload) {

        User receiver = userService.findByName(payload.get("receiver"));
        FileMetadata fm = fileMetadataService.findById(new Long(payload.get("fileId")));
        if (fm.getReceivers().add(receiver)) {
            fileMetadataService.save(fm);
        }
    }

    @GetMapping(value = "/getrestriced")
    public List<FileMetadataDTO> getFiles(@RequestParam String username) {

        User user = userService.findByName(username);
        if (user == null) {
            return null;
        }

        return fileMetadataService.getAllWithRestrictDownload(user.getId());
    }

    @GetMapping(value = "/{id}")
    public FileMetadataDTO getFileById(@PathVariable Long id) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        FileMetadata fileMetadata = fileMetadataService.findById(id);
        return new FileMetadataDTO(
                fileMetadata.getId(),
                fileMetadata.getFilename(),
                fileMetadata.getSenderUsername(),
                false,
                fileMetadata.getComments().stream().map(comment -> new CommentDTO(comment.getContent(), comment.getCommentedBy(), comment.getCommentedAt().format(dtf))).collect(Collectors.toList())
        );
    }

    @DeleteMapping(value = "/deletefile")
    public Map<String, Boolean> deleteEmployee(@RequestBody FileMetadataDTO fileMetadataDto) {

        fileMetadataService.deleteFile(fileMetadataDto.getId());

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @PostMapping(value = "/download")
    public ResponseEntity<byte[]> getFile(@RequestBody FileMetadataDTO fileMetadata) throws Exception {
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByUsername(loggedInUsername).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + loggedInUsername)
        );

        FileMetadata fm = fileMetadataService.findById(fileMetadata.getId());

        File f = new File(fm.getFilePath());

        MultipartFile file = new MockMultipartFile(f.getName(),
                f.getName(), new MimetypesFileTypeMap().getContentType(f), Files.readAllBytes(f.toPath()));

        byte[] encryptedSecretKeyArr = new byte[SECRET_KEY_LENGTH];
        file.getInputStream().read(encryptedSecretKeyArr, 0, encryptedSecretKeyArr.length);

        byte[] secretKey = cryptoService.decryptSecretKey(encryptedSecretKeyArr, loggedInUser.getPrivateKeyValue());
        byte[] decFileBytes = cryptoService.decryptFileData(Arrays.copyOfRange(file.getBytes(),SECRET_KEY_LENGTH, file.getBytes().length), secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(fm.getFilename(), fm.getFilename());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = null;
        response = new ResponseEntity<>(decFileBytes, headers, HttpStatus.OK);

        return response;
    }

    @PostMapping(value = "/downloadenc")
    public ResponseEntity<byte[]> getEncFile(@RequestBody FileMetadataDTO fileMetadata) throws Exception{
        FileMetadata fm = fileMetadataService.findById(fileMetadata.getId());

        File f = new File(fm.getFilePath());

        MultipartFile file = new MockMultipartFile(f.getName(),
                f.getName(), new MimetypesFileTypeMap().getContentType(f), Files.readAllBytes(f.toPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(fm.getFilename(), fm.getFilename());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = null;
        response = new ResponseEntity<>(file.getBytes(), headers, HttpStatus.OK);

        return response;
    }

    @PostMapping(value = "/update-comments")
    public CommentDTO updateComments(@RequestBody CommentRequest commentRequest) {

        FileMetadata fm = fileMetadataService.findById(commentRequest.getFileMetadataId());
        // handle case if file was deleted

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime savedAt = LocalDateTime.parse(commentRequest.getCommentedAt(), dtf);

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setFileMetadata(fm);
        comment.setCommentedBy(commentRequest.getCommentedBy());
        comment.setCommentedAt(savedAt);
        commentService.save(comment);

        return new CommentDTO(comment.getContent(), comment.getCommentedBy(), comment.getCommentedAt().format(dtf));
    }


}
