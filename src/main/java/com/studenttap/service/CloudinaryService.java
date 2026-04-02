

package com.studenttap.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true
        ));
    }

    // ✅ Upload any photo to Cloudinary
    public String uploadPhoto(MultipartFile file,
            String folder) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", "studenttap/" + folder,
                "resource_type", "image",
                "quality", "auto",
                "fetch_format", "auto"
            )
        );

        // Returns the permanent URL
        return (String) uploadResult.get("secure_url");
    }

    // ✅ Delete photo from Cloudinary
    public void deletePhoto(String publicId) {
        try {
            cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
            );
        } catch (IOException e) {
            System.out.println("Delete failed: "
                + e.getMessage());
        }
    }
/*
    // ✅ Get public ID from URL for deletion
    public String getPublicIdFromUrl(String url) {
        if (url == null) return null;
        // Extract public ID from Cloudinary URL
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];
        // Remove extension
        return "studenttap/" + filename.split("\\.")[0];
    }
    */
 // ✅ Extract public ID from Cloudinary URL
    public String getPublicIdFromUrl(String url) {
        if (url == null || !url.contains("cloudinary")) 
            return null;
        try {
            // URL format: .../studenttap/folder/filename.jpg
            String[] parts = url.split("/");
            String filename = parts[parts.length - 1];
            String name = filename.split("\\.")[0];
            String folder = parts[parts.length - 2];
            return "studenttap/" + folder + "/" + name;
        } catch (Exception e) {
            return null;
        }
    }
    
    
    
}