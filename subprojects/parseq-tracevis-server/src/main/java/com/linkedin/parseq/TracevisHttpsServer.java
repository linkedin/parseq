package com.linkedin.parseq;

import java.nio.file.Path;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;


/**
 * This class is to set up SSL connector for supporting https.
 *
 * @author Jiaqi Guan
 */
public class TracevisHttpsServer extends TracevisServer {

  private final int _sslPort;
  private final String _keyStorePath;
  private final String _keyStorePassword;
  private final String _trustStorePath;
  private final String _trustStorePassword;

  public TracevisHttpsServer(final String dotLocation, final int port, final Path baseLocation, final Path heapsterLocation,
      final int cacheSize, final long timeoutMs,
      int sslPort,
      String keyStorePath,
      String keyStorePassword,
      String trustStorePath,
      String trustStorePassword)
  {
    super(dotLocation, port, baseLocation, heapsterLocation, cacheSize, timeoutMs);
    _sslPort = sslPort;
    _keyStorePath = keyStorePath;
    _keyStorePassword = keyStorePassword;
    _trustStorePath = trustStorePath;
    _trustStorePassword = trustStorePassword;
  }

  @Override
  protected Connector[] getConnectors(Server server)
  {
    SslContextFactory sslContextFactory = new SslContextFactory();
    sslContextFactory.setKeyStorePath(_keyStorePath);
    sslContextFactory.setKeyStorePassword(_keyStorePassword);
    sslContextFactory.setTrustStorePath(_trustStorePath);
    sslContextFactory.setTrustStorePassword(_trustStorePassword);


    HttpConfiguration config = new HttpConfiguration();
    config.setSecureScheme(HttpScheme.HTTPS.asString());
    config.addCustomizer(new SecureRequestCustomizer());

    ServerConnector sslConnector =
        new ServerConnector(_server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(config));
    sslConnector.setPort(_sslPort);


    Connector[] httpConnectors = super.getConnectors(server);
    Connector[] connectors = new Connector[httpConnectors.length + 1];

    int i  = 0;
    for (Connector c : httpConnectors)
    {
      connectors[i++] = c;
    }
    connectors[i++] = sslConnector;

    return connectors;
  }
}
