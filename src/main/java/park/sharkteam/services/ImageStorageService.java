package park.sharkteam.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageStorageService {
    private final Path storageLocation = Paths.get("upload/img/");

    @Autowired
    public ImageStorageService() throws IOException {
        Files.createDirectories(storageLocation);
    }

    public Path getPathToFile(String filename) {
        return storageLocation.resolve(filename);
    }

    public void saveFile(MultipartFile file, String fileName)  throws  IOException {
        final String filename = StringUtils.cleanPath(fileName);
        Files.copy(
                file.getInputStream(),
                this.storageLocation.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    public Resource getFile(String filename) throws MalformedURLException {
         final Path file = getPathToFile(filename);
         final Resource resource = new UrlResource(file.toUri());
         return resource;
    }
}
