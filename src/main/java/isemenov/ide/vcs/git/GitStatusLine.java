package isemenov.ide.vcs.git;

/**
 * Support class for parsing Git status lines
 */
public class GitStatusLine {
    private Character indexStatus;
    private Character workTreeStatus;
    private String filePath;
    private String filePathFrom;

    public static GitStatusLine parse(String statusLine) throws MalformedGitStatusLineException {
        GitStatusLine parsedStatusLine = new GitStatusLine();
        parsedStatusLine.setIndexStatus(statusLine.charAt(0));
        parsedStatusLine.setWorkTreeStatus(statusLine.charAt(1));

        String[] paths = statusLine.substring(3).split(" -> ");
        if (paths.length < 1)
            throw new MalformedGitStatusLineException(statusLine);

        if (paths.length > 1) {
            parsedStatusLine.setFilePath(paths[1]);
            parsedStatusLine.setFilePathFrom(paths[0]);
        } else {
            parsedStatusLine.setFilePath(paths[0]);
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
