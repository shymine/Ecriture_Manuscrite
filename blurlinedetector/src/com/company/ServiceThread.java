package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/** This class represents a thread being launched every time a client connects to the server.
 * The thread gets the file from a socket, calls the detector's executable file, and sends the
 * output to another socket.
 */
public class ServiceThread extends Thread {
    private final Socket fileSocket;
    private final Socket answerSocket;
    private final String temporaryFilename;
    private final String temporaryOutputFilename;
    private final int id;

    private static String pwd = new File("").getAbsolutePath();
    private final static String lineDetectorExecutablePath = pwd + "/linedetector.sh";
    private final static String converterExecutablePath = pwd + "/convertToTIFF.sh";

    /**
     * @param fileSocket the socket from which we receive the file
     * @param answerSocket the socket to which we send the output
     * @param counter the thread's ID
     */
    public ServiceThread(final Socket fileSocket, final Socket answerSocket, int counter) {
        this.fileSocket = fileSocket;
        this.answerSocket = answerSocket;
        temporaryFilename = "tmp" + counter;
        temporaryOutputFilename = "output" + counter + ".lines";
        id = counter;
    }

    @Override
    public void run() {
        System.err.println("[INFO] " + id + " : Service thread started");
        try {
            // read image from client
            DataInputStream in = new DataInputStream(fileSocket.getInputStream());

            int length = in.readInt();
            byte[] bytes = new byte[length];
            in.readFully(bytes);

            byte[] decodedBytes = Base64.getDecoder().decode(bytes);
            FileOutputStream outTmp = new FileOutputStream(new File(temporaryFilename));
            outTmp.write(decodedBytes);
            outTmp.close();
            System.err.println("[INFO] " + id + " : Image read from the client");

            // convert the image to TIFF (line detector executable only manages TIFF and JPEG)
            final ProcessBuilder builder = new ProcessBuilder();
            builder.command(Arrays.asList(converterExecutablePath, temporaryFilename));
            Process p = builder.start();
            System.err.println("[INFO] " + id + " : Conversion process launched");

            // wait for the end of the process
            p.waitFor();
            System.err.println("[INFO] " + id + " : Conversion process finished");

            // launch executable
            final List<String> cmd = Arrays.asList(lineDetectorExecutablePath, temporaryFilename + ".tif", temporaryOutputFilename);
            final ProcessBuilder builder2 = new ProcessBuilder();
            builder2.command(cmd);
            Process p2 = builder2.start();
            System.err.println("[INFO] " + id + " : Line detection process launched");

            // wait for the end of the process
            p2.waitFor();
            System.err.println("[INFO] " + id + " : Line detection process finished");

            // read the output file
            byte[] answer = Files.readAllBytes(new File(temporaryOutputFilename).toPath());

            // answer to client
            DataOutputStream out = new DataOutputStream(answerSocket.getOutputStream());
            out.writeInt(answer.length);
            out.write(answer);
            System.err.println("[INFO] " + id + " : Answer sent to client, end of service thread " + id);

            // delete the temporary files
            new File(temporaryFilename).delete();
            new File(temporaryFilename + ".tif").delete();
            new File(temporaryOutputFilename).delete();
        } catch (final IOException | InterruptedException e) {
            System.err.println("[ERROR] " + id + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
}