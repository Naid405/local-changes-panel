package isemenov.ide.plugin.vcs.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support class for parsing Git status lines
 */
public class GitStatusLine {
    private final static Pattern TWO_FILE_PATTERN = Pattern.compile("(?<firstFilePath>.*) -> (?<secondFilePath>.*)");

    private Character indexStatus;
    private Character workTreeStatus;
    private String filePath;
    private String filePathFrom;

    public static GitStatusLine parse(String statusLine) throws MalformedGitStatusLineException {
        GitStatusLine parsedStatusLine = new GitStatusLine();
        parsedStatusLine.setIndexStatus(statusLine.charAt(0));
        parsedStatusLine.setWorkTreeStatus(statusLine.charAt(1));

        if (parsedStatusLine.getIndexStatus().equals('R') || parsedStatusLine.getIndexStatus().equals('C')) {
            Matcher matcher = TWO_FILE_PATTERN.matcher(statusLine.substring(3));
            if (!matcher.find())
                throw new MalformedGitStatusLineException(statusLine);

            parsedStatusLine.setFilePath(matcher.group("secondFilePath"));
            parsedStatusLine.setFilePathFrom(matcher.group("firstFilePath"));
        } else {
            parsedStatusLine.setFilePath(statusLine.substring(3));
        }
        return parsedStatusLine;
    }

    public Character getIndexStatus() {
        return indexStatus;
    }

    private void setIndexStatus(Character indexStatus) {
        this.indexStatus = indexStatus;
    }

    public Character getWorkTreeStatus() {
        return workTreeStatus;
    }

    private void setWorkTreeStatus(Character workTreeStatus) {
        this.workTreeStatus = workTreeStatus;
    }

    public String getFilePath() {
        return filePath;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePathFrom() {
        return filePathFrom;
    }

    private void setFilePathFrom(String pathFrom) {
        this.filePathFrom = pathFrom;
    }
}
