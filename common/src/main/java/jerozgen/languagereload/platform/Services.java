package jerozgen.languagereload.platform;

import jerozgen.languagereload.LanguageReload;
import jerozgen.languagereload.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * Service loaders are a built-in Java feature that enables dynamic discovery of interface implementations.
 * <p>In the {@code MultiLoader} context, they are leveraged to access a mock API within the common codebase.
 * This mock API acts as a placeholder during development, ensuring a consistent interface across shared code.
 * <p>At runtime, the service loader mechanism seamlessly replaces the mock API with a platform-specific implementation.
 * This dynamic substitution guarantees that the appropriate platform-dependent logic is executed while the common code
 * continues to interact with a unified API.
 */
public final class Services {

    /**
     * Singleton instance providing platform-specific functionality and information at runtime.
     * <p>This helper enables:
     * <ul>
     *      <li>Platform detection (Forge/Fabric/NeoForge)</li>
     *      <li>Dynamic mod dependency validation</li>
     *      <li>Safe access to platform-specific APIs and features</li>
     *      <li>Cross-platform compatibility checks</li>
     * </ul>
     * <p>The implementation is loaded dynamically via Java's ServiceLoader mechanism,
     * ensuring the correct platform-specific logic is used at runtime.
     */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Loads a service implementation for the current platform.
     * <p>Implementation is defined in META-INF/services
     *
     * @param clazz The service interface to load
     * @return {@code loadedService} The platform-specific implementation
     * @throws NullPointerException if no implementation is found
     */
    public static <T> T load(Class<T> clazz) {
        T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));

        LanguageReload.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}