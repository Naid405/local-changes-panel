package isemenov.ide;

import isemenov.ide.event.EventManager;
import isemenov.ide.event.UnorderedEventManager;
import isemenov.ide.vcs.StaticVCSPluginRegistry;
import isemenov.ide.vcs.VCSPluginBungle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IDEHeadlessNoVCSStartTest {
    private Path rootDirectory;
    private IDE ide;

    @Before
    public void setUp() throws Exception {
        rootDirectory = Files.createTempDirectory(Paths.get("target"), "projectTest");
        EventManager eventManager = new UnorderedEventManager();

        ide = new IDE(rootDirectory, eventManager, new StaticVCSPluginRegistry() {
            @Override
            public Optional<VCSPluginBungle> getBundleForVCS(String vcsName) {
                return Optional.empty();
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(rootDirectory);
    }

    @Test
    public void testStart() throws Exception {
        ide.start();
    }
}