package com.fengruigan.androidbuilder;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AndroidBuilder implements RequestHandler<HandlerInput, HandlerOutput>{

    @Override
    public HandlerOutput handleRequest(HandlerInput input, Context context) {
        Map<String, String> data = input.getData();
        Map<String, String> env = input.getEnv();
        String buildId = data.get("buildId");
        String repoName = data.get("repoName");
        String repoOwner = data.get("repoOwner");
        String artifactName = data.get("artifactName");
        String fileKeyPrefix = FileTransferUtil.generateFileKeyPrefix(repoOwner, repoName, buildId);

        String token = env.get("GITHUB_TOKEN");
        String gitDirName = "/tmp/gitDir";
        String[] commands = {
                "git",
                "clone",
                "https://" + repoOwner + ":" + token + "@github.com/"+ repoOwner +"/" + repoName + ".git",
                gitDirName};
        try {
            System.out.println("Starting git clone");
            ProcessHandler.executeCommand(commands, env, "/tmp", "Git Clone");
            System.out.println("Finish git clone");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            FileTransferUtil.uploadToS3("/tmp/stdout.txt", fileKeyPrefix + "stdout.txt");
            return new HandlerOutput(1, "Error cloning. " + e.getMessage());
        }

        commands = new String[]{"./gradlew", "assembleRelease"};
        int exitCode;
        try {
            System.out.println("Starting build");
            exitCode = ProcessHandler.executeCommand(commands, env, gitDirName, "Build");
            System.out.println("Finish build");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            FileTransferUtil.uploadToS3("/tmp/stdout.txt", fileKeyPrefix + "stdout.txt");
            return new HandlerOutput(1, "Error building. " + e.getMessage());
        }

        if (exitCode != 0) {
            FileTransferUtil.uploadToS3("/tmp/stdout.txt", fileKeyPrefix + "stdout.txt");
            return new HandlerOutput(1, "Build failed");
        }

        commands = new String[]{
                "apksigner", "sign",
                "--ks", "app/keystore",
                "--ks-pass", "env:KEYSTORE_PASS",
                "--in", "app/build/outputs/apk/release/app-release-unsigned.apk",
                "--out", artifactName
        };

        try {
            System.out.println("Starting apk signing");
            exitCode = ProcessHandler.executeCommand(commands, env, gitDirName, "Apk Signing");
            System.out.println("Finish apk signing");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            FileTransferUtil.uploadToS3("/tmp/stdout.txt", fileKeyPrefix + "stdout.txt");
            return new HandlerOutput(1, "Error signing apk. " + e.getMessage());
        }

        System.out.println("Starting artifact upload");
        FileTransferUtil.uploadToS3("/tmp/stdout.txt", fileKeyPrefix + "stdout.txt");
        FileTransferUtil.uploadToS3(gitDirName + "/" + artifactName, fileKeyPrefix + artifactName);
        System.out.println("Finish artifact upload");

        return new HandlerOutput(exitCode, exitCode == 0? "Build finished: 0": "Build finished: " + exitCode);
    }
}
