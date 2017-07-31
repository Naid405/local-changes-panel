package isemenov.ide.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ShellCommandExecutor {
    public List<String> executeCommand(Path inDirectory, String... command) throws CommandExecutionException {
        try {
            Process cmdProcess = new ProcessBuilder()
                    .directory(inDirectory.toFile())
                    .command(command)
                    .start();

            CompletableFuture<List<String>> result = getContents(cmdProcess.getInputStream());
            CompletableFuture<List<String>> error = getContents(cmdProcess.getErrorStream());

            int exitValue = cmdProcess.waitFor();

            if (exitValue != 0) {
                throw new CommandExecutionException(concatStringArrayToWhitespacedString(command),
                                                    error.get().stream()
                                                            .reduce(String::concat)
                                                            .orElse("Unknown error"));
            }

            return result.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new CommandExecutionException(concatStringArrayToWhitespacedString(command), e);
        }
    }

    private String concatStringArrayToWhitespacedString(String[] command) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : command) {
            stringBuilder.append(s);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
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
