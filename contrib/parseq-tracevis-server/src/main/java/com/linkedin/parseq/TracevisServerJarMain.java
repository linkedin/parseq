package com.linkedin.parseq;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TracevisServerJarMain {

  public static void main(String[] args) throws Exception {

    if (args.length < 1 || args.length > 2) {
      System.out.println("Incorrect arguments, expecting: DOT_LOCATION <PORT>\n"
          + "  DOT_LOCATION - location of graphviz dot executable\n"
          + "  <PORT>       - optional port number, default is " + Constants.DEFAULT_PORT);
      System.exit(1);
    }
    final String dotLocation = args[0];
    final int port = (args.length == 2) ? Integer.parseInt(args[1]) : Constants.DEFAULT_PORT;

    String path = TracevisServerJarMain.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String onwJarFile = URLDecoder.decode(path, "UTF-8");

    final Path base = Files.createTempDirectory("tracevis-server");
    try(JarFile jar = new JarFile(onwJarFile)) {

      //extract tracevis
      Enumeration<JarEntry> enums = jar.entries();
      while(enums.hasMoreElements()) {
        JarEntry entry = enums.nextElement();
        if (entry.getName().startsWith("tracevis/") || entry.getName().startsWith("heapster/")) {
          if (entry.isDirectory()) {
            base.resolve(entry.getName()).toFile().mkdirs();
          } else {
            Files.copy(jar.getInputStream(entry), base.resolve(entry.getName()));
          }
        }
      }

      new TracevisServer(dotLocation, port, base, base, Constants.DEFAULT_CACHE_SIZE, Constants.DEFAULT_TIMEOUT_MS)
        .start();

    } finally {
      //delete base directory recursively
      Files.walkFileTree(base, new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return FileVisitResult.CONTINUE;
        }
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

}
