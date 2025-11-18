package define;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class VideoRecording {


    private Process ffmpegProcess;
    private boolean isRecording = false;

    public void startRecording(String filePath) throws IOException {
        // Full FFmpeg command for screen recording to ensure compatibility with most players
        String ffmpegCommand = "D:\\Automation\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe -y -f gdigrab -framerate 30 -i desktop -c:v libx264 -crf 18 -preset fast -pix_fmt yuv420p " + filePath;


        // Start the FFmpeg process
        ffmpegProcess = Runtime.getRuntime().exec(ffmpegCommand);

        // Capture and print the output and error streams of the FFmpeg process
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg Output: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("FFmpeg Error: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        isRecording = true;
    }

    // Stop recording
    public void stopRecording() {
        if (ffmpegProcess != null) {
            ffmpegProcess.destroy();  // Stops the FFmpeg process
            isRecording = false;
        }
    }

    // Check if the recording is in progress
    public boolean isRecording() {
        return isRecording;
    }

    // You can also implement a method to check if the process is still alive
    public boolean isProcessAlive() {
        return ffmpegProcess != null && ffmpegProcess.isAlive();
    }
}