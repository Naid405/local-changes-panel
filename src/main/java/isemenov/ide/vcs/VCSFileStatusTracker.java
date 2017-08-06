package isemenov.ide.vcs;

import isemenov.ide.Project;
import isemenov.ide.event.EventManager;
import isemenov.ide.event.vcs.VCSTrackingListChangedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VCSFileStatusTracker {
    private final static Logger logger = LogManager.getLogger(VCSFileStatusTracker.class);

    private final EventManager eventManager;

    private final String vcsName;
    private final VCSService vcsService;
    private final Map<Path, VCSFileStatus> trackedFiles;

    public VCSFileStatusTracker(String vcsName,
                                Project project,
                                VCSServiceFactory vcsServiceFactory,
                                EventManager localEventManager,
                                EventManager globalEventManager) throws VCSException {
        this.eventManager = localEventManager;
        this.vcsName = vcsName;
        this.trackedFiles = new ConcurrentHashMap<>();

        this.vcsService = vcsServiceFactory.getServiceForProject(project, globalEventManager);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public String getVcsName() {
        return vcsName;
    }

    public VCSService getVcsService() {
        return vcsService;
    }

    public void startTrackingFile(Path file) {
        VCSFileStatus status = getVcsFileStatus(file);
        trackedFiles.compute(file, (path, fileStatus) -> {
            eventManager.fireEventListeners(this,
                                            new VCSTrackingListChangedEvent(Collections.singletonMap(path, status),
                                                                            Collections.emptyMap(),
                                                                            Collections.emptySet()));
            return status;
        });
    }

    public void refreshAllTrackedFileStatuses() {
        try {
            trackedFiles.putAll(vcsService.getStatuses(new HashSet<>(trackedFiles.keySet())));
            eventManager.fireEventListeners(this,
                                            new VCSTrackingListChangedEvent(Collections.emptyMap(),
                                                                            trackedFiles,
                                                                            Collections.emptySet()));
        } catch (VCSException e) {
            logger.warn("Cannot get files statuses", e);
        }
    }

    public void refreshTrackedFileStatuses(Set<Path> updateCandidates) {
        try {
            Set<Path> foundTrackedFiles = new HashSet<>();
            for (Path path : updateCandidates) {
                if (trackedFiles.containsKey(path))
                    foundTrackedFiles.add(path);
            }


            Map<Path, VCSFileStatus> statuses = vcsService.getStatuses(foundTrackedFiles);
            Map<Path, VCSFileStatus> updated = new HashMap<>();
            //Need for concurrent updates
            for (Map.Entry<Path, VCSFileStatus> entry : statuses.entrySet()) {
                trackedFiles.computeIfPresent(entry.getKey(), (path, fileStatus) -> {
                    updated.put(entry.getKey(), entry.getValue());
                    return entry.getValue();
                });
            }
            eventManager.fireEventListeners(this,
                                            new VCSTrackingListChangedEvent(Collections.emptyMap(),
                                                                            updated,
                                                                            Collections.emptySet()));
        } catch (VCSException e) {
            logger.warn("Cannot get files statuses", e);
        }
    }

    public void refreshFileStatus(Path file) {
        VCSFileStatus status = getVcsFileStatus(file);
        trackedFiles.computeIfPresent(file, (path, fileStatus) -> {
            eventManager.fireEventListeners(this,
                                            new VCSTrackingListChangedEvent(Collections.emptyMap(),
                                                                            Collections.singletonMap(path, status),
                                                                            Collections.emptySet()));
            return status;
        });
    }

    private VCSFileStatus getVcsFileStatus(Path file) {
        VCSFileStatus status = VCSFileStatus.UNKNOWN;
        try {
            status = vcsService.getStatus(file);
        } catch (VCSException e) {
            logger.warn("Cannot get file status", e);
        }
        return status;
    }

    public void checkRemovedFiles(Set<Path> removedFiles) {
        Set<Path> removed = new HashSet<>();
        Map<Path, VCSFileStatus> notRemoved = new HashMap<>();
        for (Path file : removedFiles) {
            try {
                if (!vcsService.checkExists(file))
                    removed.add(file);
                else notRemoved.put(file, getVcsFileStatus(file));
            } catch (VCSException e) {
                logger.warn("Cannot check possibly removed file " + file.toString(), e);
            }
        }
        eventManager.fireEventListeners(this,
                                        new VCSTrackingListChangedEvent(Collections.emptyMap(),
                                                                        notRemoved,
                                                                        removed));
    }

    public Map<Path, VCSFileStatus> getTrackedFiles() {
        return Collections.unmodifiableMap(trackedFiles);
    }
}
