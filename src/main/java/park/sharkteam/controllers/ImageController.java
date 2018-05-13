package park.sharkteam.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;

import park.sharkteam.models.User;
import park.sharkteam.services.UserService;
import park.sharkteam.services.ImageStorageService;
import park.sharkteam.utilities.ErrorCoder;
import park.sharkteam.views.responses.ErrorResponse;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin(origins = {"http://frontend_site.herokuapp.com", "http://localhost:3000", "http://127.0.0.1:3000"})
@RequestMapping(path = "/api/avatars")
public class ImageController {

    private UserService userService;
    private ImageStorageService imageStorageService;

    @Autowired
    public ImageController(UserService userService, ImageStorageService imageStorageService) {
        this.userService = userService;
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/upload/")
    public ResponseEntity<?> saveAvatar(@RequestParam("file") MultipartFile file, HttpSession httpSession) {
        final Integer currentUserId = (Integer) httpSession.getAttribute("id");

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        User currentUser = null;
        try {
            currentUser = userService.getUserById(currentUserId);
        } catch (EmptyResultDataAccessException e) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }
        try {
            String extension = file.getOriginalFilename();
            if (extension.lastIndexOf(".") != -1) {
                extension = extension.substring(extension.lastIndexOf("."));
            } else {
                extension = ".png";
            }
            String fileName = Integer.toString(currentUser.getId()) + extension;
            imageStorageService.saveFile(file, fileName);

            currentUser.setAvatar(fileName);
            userService.updateUser(currentUser, currentUserId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.UNCORRECT_FILE));
        }

        return ResponseEntity.ok("Image saved");
    }

    @GetMapping(value = "/{userId:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getAvatar(@PathVariable Integer userId) {

        String fileName;
        try {
            fileName = userService.getUserById(userId).getAvatar();
        } catch (EmptyResultDataAccessException e) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        if (fileName.compareTo("NULL") == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.UNEXISTED_FILE));
        }

        Resource file;
        try {
            file =  imageStorageService.getFile(fileName);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(ErrorCoder.UNCORRECT_FILE));
        }

        HttpHeaders responseHeaders = new HttpHeaders();

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\""  + file.getFilename() + '"'
                )
                .body(file);
    }
}
