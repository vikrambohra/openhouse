package com.linkedin.openhouse.cluster.storage.selector;

import com.linkedin.openhouse.cluster.storage.configs.StorageProperties;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configures the StorageSelector bean for storage-selector configured in {@link StorageProperties}
 * The return value of the bean is the {@link StorageSelector} implementation that matches the name
 * in storage-selector or is null if the storage selector is not configured.
 */
@Slf4j
@Configuration
public class StorageSelectorConfig {

  @Autowired StorageProperties storageProperties;

  @Autowired List<StorageSelector> storageSelectors;

  /**
   * Checks the name of storage-selector from {@link StorageProperties} against all implementations
   * of {@link StorageSelector} and returns the implementation that matches the name. Returns {@link
   * DefaultStorageSelector}if not configured
   *
   * @return
   */
  @Bean
  @Primary
  StorageSelector provideStorageSelector() {

    String selectorName;
    try {
      selectorName = storageProperties.getStorageSelector().getName();
      for (StorageSelector selector : storageSelectors) {
        if (selectorName.equals(selector.getName())) {
          return selector;
        }
      }
    } catch (NullPointerException e) {
      // We get NPE if storage selector or its name is not configured. Return
      // DefaultStorageSelector.
      log.error(
          "Missing storage selector config or name. Defaulting to {}.",
          DefaultStorageSelector.class.getSimpleName());
      return new DefaultStorageSelector();
    }

    throw new IllegalArgumentException("Could not find Storage selector with name=" + selectorName);
  }
}