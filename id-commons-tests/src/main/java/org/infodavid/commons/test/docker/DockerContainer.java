package org.infodavid.commons.test.docker;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.AuthCmd;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.RemoveContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.command.StopContainerCmd;
import com.github.dockerjava.api.command.WaitContainerCmd;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.NetworkSettings;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class DockerContainer.
 */
@Slf4j
public class DockerContainer implements Closeable {

    /** The Constant NO_CONTAINER_IDENTIFIER_AVAILABLE. */
    private static final String NO_CONTAINER_IDENTIFIER_AVAILABLE = "No container identifier available";

    /**
     * Container exists.
     * @param client the client
     * @param image  the image
     * @param name   the name
     * @return the string
     */
    @SuppressWarnings("resource")
    public static String containerExists(final DockerClient client, final String image, final String name) {
        LOGGER.debug("Searching container: {} based on image: {}", name, image);

        if (client == null) {
            throw new IllegalArgumentException("Client is null");
        }

        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        try (final ListContainersCmd command = client.listContainersCmd()) {
            command.withNameFilter(Collections.singletonList(name));
            command.withShowAll(Boolean.TRUE);

            for (final Container container : command.exec()) {
                LOGGER.debug("Container: {}", container);

                if (image == null || container.getImage().equals(image)) {
                    LOGGER.debug("Container found");

                    return container.getId();
                }
            }
        }

        LOGGER.debug("Container not found");

        return null;
    }

    /**
     * Creates the container.
     * @param client     the client
     * @param image      the image
     * @param name       the name
     * @param env        the env
     * @param configurer the configurer
     * @param ports      the ports
     * @return the string
     */
    @SuppressWarnings({ "resource", "boxing" })
    protected static String createContainer(final DockerClient client, final String image, final String name, final Map<String, String> env, final Consumer<CreateContainerCmd> configurer, final ExposedPort... ports) {
        LOGGER.debug("Creating container: {} based on image: {}", name, image);

        if (client == null) {
            throw new IllegalArgumentException("Client is null");
        }

        if (StringUtils.isEmpty(image)) {
            throw new IllegalArgumentException("Image is null or empty");
        }

        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        try (final AuthCmd command = client.authCmd()) {
            command.exec();
        }

        try (final CreateContainerCmd command = client.createContainerCmd(image)) {
            final List<String> environment = new ArrayList<>();

            if (env != null) {
                for (final Entry<String, String> entry : env.entrySet()) {
                    LOGGER.debug("Adding environment entry: {}={}", entry.getKey(), entry.getValue());
                    environment.add(entry.getKey() + "=" + entry.getValue());
                }
            }

            command.withEnv(environment);

            if (ports != null) {
                command.withExposedPorts(ports);
                final List<PortBinding> portBindings = new ArrayList<>();

                for (final ExposedPort port : ports) {
                    LOGGER.debug("Adding port binding entry: 127.0.0.1:{}:{}", port.getPort(), port.getPort());
                    portBindings.add(PortBinding.parse("127.0.0.1:" + port.getPort() + ':' + port.getPort()));
                }

                command.getHostConfig().withPortBindings(portBindings);
            }

            command.withAttachStderr(Boolean.TRUE);
            command.withAttachStdout(Boolean.TRUE);
            command.withName(name);

            if (configurer != null) {
                configurer.accept(command);
            }

            final String result = command.exec().getId();
            LOGGER.debug("Creating identifier: {}", result);

            return result;
        }
    }

    /**
     * Delete container.
     * @param client the client
     * @param id     the identifier
     */
    @SuppressWarnings("resource")
    public static void deleteContainer(final DockerClient client, final String id) {
        LOGGER.debug("Deleting container: {}", id);

        if (client == null) {
            throw new IllegalArgumentException("Client is null");
        }

        if (StringUtils.isEmpty(id)) {
            return;
        }

        try (final RemoveContainerCmd command = client.removeContainerCmd(id)) {
            command.withForce(Boolean.TRUE);
            command.withRemoveVolumes(Boolean.TRUE);
            command.exec();
        }
    }

    /**
     * Gets the docker client.
     * @return the docker client
     */
    @SuppressWarnings("resource")
    public static DockerClient getDockerClient() {
        final DefaultDockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        return DockerClientBuilder.getInstance().withDockerHttpClient(new ZerodepDockerHttpClient.Builder().dockerHost(dockerClientConfig.getDockerHost()).build()).build();
    }

    /**
     * Checks if is docker host.
     * @return true, if is docker host
     */
    @SuppressWarnings("resource")
    public static boolean isDockerHost() {
        final DefaultDockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        try (DockerClient client = DockerClientBuilder.getInstance().withDockerHttpClient(new ZerodepDockerHttpClient.Builder().dockerHost(dockerClientConfig.getDockerHost()).build()).build()) {
            client.infoCmd().exec();
            LOGGER.debug("localhost has a running docker service");
            return true;
        } catch (@SuppressWarnings("unused") final Exception e) {
            LOGGER.warn("localhost has not a running docker service");

            return false;
        }
    }

    /**
     * Sleep.
     * @param duration the duration
     */
    protected static void sleep(final long duration) {
        try {
            Thread.sleep(duration);
        } catch (@SuppressWarnings("unused") final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Wait listening.
     * @param address the address
     * @param port    the port
     * @param timeout the timeout
     */
    @SuppressWarnings("boxing")
    protected static void waitListening(final String address, final int port, final int timeout) {
        LOGGER.debug("Waiting for connection on port: {}", port);
        int t0 = timeout;
        boolean disconnected = true;

        do {
            final long t1 = System.currentTimeMillis();

            try (final Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(address, port), 200);
                disconnected = false;
            } catch (@SuppressWarnings("unused") final IOException e) {
                // noop
            }

            if (disconnected) {
                sleep(200);
            }

            t0 -= System.currentTimeMillis() - t1;
        } while (disconnected && t0 > 0);

        if (!disconnected) {
            LOGGER.debug("Port connected: {}", port);
        }
    }

    /** The client. */
    private DockerClient client;

    /** The container id.entifier */
    @Getter
    private String containerId;

    /** The environment variables. */
    @Getter
    private Map<String, String> env;

    /** The image. */
    @Getter
    private String image;

    /** The name. */
    @Getter
    private String name;

    /** The ports. */
    @Getter
    private List<ExposedPort> ports;

    /**
     * Instantiates a new docker container.
     * @param image the image
     * @param name  the name
     * @param env   the environment variables
     * @param ports the ports
     */
    public DockerContainer(final String image, final String name, final Map<String, String> env, final ExposedPort... ports) {
        this.image = image;
        this.name = name;
        this.env = Collections.unmodifiableMap(env);
        this.ports = Collections.unmodifiableList(Arrays.asList(ports));
        client = getDockerClient();
        containerId = containerExists(client, image, name);

        if (containerId != null) {
            deleteContainer(client, containerId);
        }

        containerId = createContainer(client, image, name, env, null, ports);
    }

    /**
     * Instantiates a new docker container.
     * @param image      the image
     * @param name       the name
     * @param env        the environment variables
     * @param configurer the configurer
     * @param ports      the ports
     */
    public DockerContainer(final String image, final String name, final Map<String, String> env, final Consumer<CreateContainerCmd> configurer, final ExposedPort... ports) {
        this.image = image;
        this.name = name;
        this.env = Collections.unmodifiableMap(env);
        this.ports = Collections.unmodifiableList(Arrays.asList(ports));
        client = getDockerClient();
        containerId = containerExists(client, image, name);

        if (containerId != null) {
            deleteContainer(client, containerId);
        }

        containerId = createContainer(client, image, name, env, configurer, ports);
    }

    /*
     * (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        stop();
    }

    /**
     * Delete.
     */
    public void delete() {
        if (StringUtils.isNotEmpty(containerId)) {
            deleteContainer(client, containerId);
        } else {
            LOGGER.warn(NO_CONTAINER_IDENTIFIER_AVAILABLE);
        }
    }

    /**
     * Gets the client.
     * @return the client
     */
    protected DockerClient getClient() {
        return client;
    }

    /**
     * Start.
     * @param timeout the timeout in milliseconds
     */
    public void start(final int timeout) {
        if (StringUtils.isNotEmpty(containerId)) {
            LOGGER.debug("Starting container: {}", containerId);

            try (final StartContainerCmd command = client.startContainerCmd(containerId)) {
                command.exec();
            } catch (@SuppressWarnings("unused") final NotModifiedException e) {
                return;
            }

            // Wait start of container
            final CountDownLatch lock = new CountDownLatch(1);

            try (final WaitContainerCmd command = client.waitContainerCmd(containerId)) {
                command.exec(new ResultCallback<WaitResponse>() {
                    @Override
                    public void close() throws IOException {
                        LOGGER.debug("close");
                    }

                    @Override
                    public void onComplete() {
                        LOGGER.debug("onComplete");
                        lock.countDown();
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        LOGGER.debug("onError");
                        lock.countDown();
                    }

                    @Override
                    public void onNext(final WaitResponse object) {
                        LOGGER.debug("onNext");
                    }

                    @Override
                    public void onStart(final Closeable closeable) {
                        LOGGER.debug("onStart");
                        sleep(200);
                        lock.countDown();
                    }
                });
                lock.await(60, TimeUnit.SECONDS); // NOSONAR Return value
            } catch (@SuppressWarnings("unused") final InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Wait start of application by checking ports directly on the container
            try (final InspectContainerCmd command = client.inspectContainerCmd(containerId)) {
                final InspectContainerResponse response = command.exec();
                final NetworkSettings networkSettings = response.getNetworkSettings();
                final ContainerNetwork network = networkSettings.getNetworks().get("bridge");

                if (network != null) {
                    for (final ExposedPort port : networkSettings.getPorts().getBindings().keySet()) {
                        waitListening(network.getIpAddress(), port.getPort(), timeout);
                    }
                }
            }
        } else {
            LOGGER.warn(NO_CONTAINER_IDENTIFIER_AVAILABLE);
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        if (StringUtils.isNotEmpty(containerId)) {
            try (final StopContainerCmd command = client.stopContainerCmd(containerId)) {
                command.exec();
            }
        } else {
            LOGGER.warn(NO_CONTAINER_IDENTIFIER_AVAILABLE);
        }
    }
}