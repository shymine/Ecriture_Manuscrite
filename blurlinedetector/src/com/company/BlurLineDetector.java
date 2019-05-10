package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** This class represents Intuidoc's detector interface.
 * It must be run on a Linux 32-bit VM.
 */
public class BlurLineDetector {
    // port to receive the image files
    private final static int FILE_PORT = 7007;

    // port to send the detector's output
    private final static int ANSWER_PORT = 7008;

    public static void main(String[] args) {
        try {
            final ServerSocket fileServerSocket = new ServerSocket(FILE_PORT);
            final ServerSocket answerSocket = new ServerSocket(ANSWER_PORT);
            System.err.println("[INFO] Server started on ports " + FILE_PORT + " and " + ANSWER_PORT);

            while (true) {
                // infinite loop with a counter reseting every 100000 iterations
                for (int counter = 0; counter < 100000; counter++) {
                    final Socket fileClient = fileServerSocket.accept();
                    final Socket answerClient = answerSocket.accept();
                    new ServiceThread(fileClient, answerClient, counter).start();
                }
            }
        } catch (final IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}