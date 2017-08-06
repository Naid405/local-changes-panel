package isemenov.ide.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ShellCommandExecutor {
    private final static Logger logger = LogManager.getLogger(ShellCommandExecutor.class);

    public List<String> executeCommand(Path inDirectory, String... command) throws CommandExecutionException {
        try {
            logger.info("Executing " + Arrays.toString(command));
            Process cmdProcess = new ProcessBuilder()
                    .directory(inDirectory.toFile())
                    .command(command)
                    .start();

            CompletableFuture<List<String>> result = getContents(cmdProcess.getInputStream());
            CompletableFuture<List<String>> error = getContents(cmdProcess.getErrorStream());

            int exitValue = cmdProcess.waitFor();

            if (exitValue != 0) {
                throw new CommandExecutionException(Arrays.toString(command),
                                                    error.get().stream()
                                                         .reduce(String::concat)
                                                         .orElse("Unknown error"));
            }

            return result.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new CommandExecutionException(Arrays.toString(command), e);
        }
    }

    private CompletableFuture<List<String>> getContents(InputStream resultStream) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> result = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream))) {
                String line;
                while ((line = reader.readLine()) != null)
                    result.add(line);
                return result;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
