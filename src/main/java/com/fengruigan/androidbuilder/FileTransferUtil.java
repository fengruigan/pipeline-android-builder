package com.fengruigan.androidbuilder;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

public class FileTransferUtil {

    private static final String BUCKET_NAME = "fengruigan-pipeline";

    public static void uploadToS3(String filename, String fileKey) {
        Regions regions = Regions.DEFAULT_REGION;
        AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(regions).build();
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, fileKey, new File(filename));
        s3client.putObject(request);
    }

    public static String generateFileKeyPrefix(
            String repoOwner,
            String repoName,
            String buildId) {
        return repoOwner + "/" + repoName + "/" + buildId + "/";
    }
}
