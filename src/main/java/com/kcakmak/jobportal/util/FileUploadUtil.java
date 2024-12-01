package com.kcakmak.jobportal.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    // This method is to save the profile image file
    // Required parameters are the directory, filename and the multipart file which is the actual image file
    public static void saveFile(String uploadDir, String filename, MultipartFile multipartFile) throws IOException {

        // Create the path of the directory
        Path uploadPath = Paths.get(uploadDir);
        // If the directory is not created yet, create it
        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }


        try(InputStream inputStream = multipartFile.getInputStream();) {
            // Create the path of the filename
            // Similar to a string concatenation,
            // "resolve" method of the "Path" class does not check the file system
            // or validate whether the file exists.
            // Instead, it constructs a new "Path" object by logically
            // appending the given file name (or relative path) to the current path.
            Path path = uploadPath.resolve(filename);
            //System.out.println("Filepath " + path);
            //System.out.println("Filename " + filename);
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + filename, ioe);
        }
    }
}
