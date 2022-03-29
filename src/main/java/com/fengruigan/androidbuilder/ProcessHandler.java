package com.fengruigan.androidbuilder;

import java.io.*;
import java.util.Map;

public class ProcessHandler {

    public static int executeCommand(String[] commands, Map<String, String> env, String dir, String stageName)
            throws IOException, InterruptedException {
        if (commands == null || commands.length == 0) {
            System.out.println("No command to run");
            return 1;
        }

        ProcessBuilder pb = new ProcessBuilder(commands);

        if (dir != null && dir.length() != 0) {
            pb.directory(new File(dir));
        }
        if (env != null) {
            pb.environment().putAll(env);
        }
        Process process = pb.start();

        BufferedReader stdOutput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));
        // Read the output from the command
        processDataStream("/tmp/stdout.txt", stdOutput, stageName);
        // Read any errors from the attempted command
        processDataStream("/tmp/stdout.txt", stdError, null);

        process.waitFor();
        return process.exitValue();
    }

    private static void processDataStream(String filename, BufferedReader bufferedReader, String stageName)
            throws IOException {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file, true);
        if (stageName != null) {
            writer.append("================ ").append(stageName).append(" ================\n");
        }

        String s;
        while ((s = bufferedReader.readLine()) != null) {
            writer.append(s);
            writer.append("\n");
            System.out.println(s);
        }
        writer.close();
    }
}
