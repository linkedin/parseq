package com.linkedin.parseq;

import com.linkedin.parseq.exec.Exec;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Tuple2;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphvizEngine {

  private static final Logger LOG = LoggerFactory.getLogger(GraphvizEngine.class);

  private final String _dotLocation;
  private final Path _cacheLocation;
  private final long _timeoutMs;
  private final HashManager _hashManager;
  private final Exec _exec;
  private final ConcurrentHashMap<String, Task<Exec.Result>> _inFlightBuildTasks;

  public GraphvizEngine(final String dotLocation, final Path cacheLocation, final int cacheSize, final long timeoutMs,
      final int numThreads, final long reaperDelayMs, final int processQueueSize) {
    _dotLocation = dotLocation;
    _cacheLocation = cacheLocation;
    _timeoutMs = timeoutMs;
    _hashManager = new HashManager(this::removeCached, cacheSize);
    _exec = new Exec(numThreads, reaperDelayMs, processQueueSize);
    _inFlightBuildTasks = new ConcurrentHashMap<>();
  }

  public void start() {
    _exec.start();
  }

  public void stop() {
    _exec.stop();
  }

  /**
   * Return task that has general HTTP status and body information based on the build task's result.
   */
  public Task<Tuple2<Integer, String>> build(final String hash, final InputStream body)
      throws IOException {
    if (hash == null) {
      // Missing hash
      String content = "Missing hash.";
      LOG.info(content);
      return Task.value(new Tuple2<>(HttpServletResponse.SC_BAD_REQUEST, content));
    } else {
      // Have cache
      if (_hashManager.contains(hash)) {
        LOG.info("hash found in cache: " + hash);
        return Task.value(new Tuple2<>(HttpServletResponse.SC_OK, ""));
      } else {
        if (body == null) {
          // Missing body
          String content = "Missing body.";
          LOG.info(content);
          return Task.value(new Tuple2<>(HttpServletResponse.SC_BAD_REQUEST, content));
        } else if (_dotLocation == null) {
          // Missing dot
          String content = "Missing dot.";
          LOG.info(content);
          return Task.value(new Tuple2<>(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, content));
        } else {
          // Build task
          final Task<Exec.Result> buildTask = getBuildTask(hash, body);
          return buildTask.transform("result", result -> {
            Integer status = null;
            String content = null;
            if (result.isFailed()) {
              // Task fail
              status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
              content = result.getError().toString();
            } else {
              // Task success
              switch (result.get().getStatus()) {
                // Success
                case 0:
                  _hashManager.add(hash);
                  status = HttpServletResponse.SC_OK;
                  content = "";
                  break;
                // Timeout
                case 137:
                  status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                  content = "graphviz process was killed because it did not finish within " + _timeoutMs + "ms";
                  break;
                // Unknown
                default:
                  status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                  content = writeGenericFailureInfo(result.get());
                  break;
              }
            }
            // Clean up cache
            _inFlightBuildTasks.remove(hash, buildTask);
            return Success.of(new Tuple2<>(status, content));
          });
        }
      }
    }
  }

  /**
   * Returns task that builds graph using graphviz. Returned task might be shared with other concurrent requests.
   */
  private Task<Exec.Result> getBuildTask(final String hash, final InputStream body) {
    Task<Exec.Result> existing = _inFlightBuildTasks.get(hash);
    if (existing != null) {
      LOG.info("using in flight shareable: " + hash);
      return existing.shareable();
    } else {
      Task<Exec.Result> newBuildTask = createNewBuildTask(hash, body);
      existing = _inFlightBuildTasks.putIfAbsent(hash, newBuildTask);
      if (existing != null) {
        LOG.info("using in flight shareable: " + hash);
        return existing.shareable();
      } else {
        return newBuildTask;
      }
    }
  }

  /**
   * Returns new task that builds graph using graphviz.
   */
  private Task<Exec.Result> createNewBuildTask(final String hash, final InputStream body) {

    LOG.info("building: " + hash);

    final Task<Void> createDotFile = Task.action("createDotFile",
        () -> Files.copy(body, pathToCacheFile(hash, "dot"), StandardCopyOption.REPLACE_EXISTING));

    // Task that runs a graphviz command.
    // We give process TIMEOUT_MS time to finish, after that
    // it will be forcefully killed.
    final Task<Exec.Result> graphviz = _exec
        .command("graphviz", _timeoutMs, TimeUnit.MILLISECONDS, _dotLocation, "-T" + Constants.OUTPUT_TYPE,
            "-Grankdir=LR", "-Gnewrank=true", "-Gbgcolor=transparent", pathToCacheFile(hash, "dot").toString(), "-o",
            pathToCacheFile(hash, Constants.OUTPUT_TYPE).toString());

    // Since Exec utility allows only certain number of processes
    // to run in parallel and rest is enqueued, we also specify
    // timeout on a task level equal to 2 * graphviz timeout.
    final Task<Exec.Result> graphvizWithTimeout = graphviz.withTimeout(_timeoutMs * 2, TimeUnit.MILLISECONDS);

    return createDotFile.andThen(graphvizWithTimeout);
  }

  private Path pathToCacheFile(String hash, String ext) {
    return _cacheLocation.resolve(hash + "." + ext);
  }

  private File cacheFile(String hash, String ext) {
    return pathToCacheFile(hash, ext).toFile();
  }

  private void removeCached(String hash) {
    cacheFile(hash, Constants.OUTPUT_TYPE).delete();
    cacheFile(hash, "dot").delete();
  }

  /**
   * Writes error info to a String.
   */
  private String writeGenericFailureInfo(final Exec.Result result)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("graphviz process returned: ").append(result.getStatus()).append("\n").append("stdout:\n");
    Files.lines(result.getStdout()).forEach(sb::append);
    sb.append("stderr:\n");
    Files.lines(result.getStderr()).forEach(sb::append);
    return sb.toString();
  }
}
