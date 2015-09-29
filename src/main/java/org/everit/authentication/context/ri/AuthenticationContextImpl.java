/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.context.ri;

import java.util.Objects;
import java.util.function.Supplier;

import org.everit.authentication.context.AuthenticationContext;
import org.everit.authentication.context.AuthenticationPropagator;
import org.everit.props.PropertyManager;
import org.everit.resource.ResourceService;

/**
 * The reference implementation of the {@link AuthenticationContext} and
 * {@link AuthenticationPropagator} interfaces.
 */
public class AuthenticationContextImpl implements AuthenticationContext, AuthenticationPropagator {

  /**
   * The property key of the ID of the <a href="https://github.com/everit-org/resource">resource</a>
   * assigned to the Default Resource ID. The Default Resource ID (a.k.a. guest or not authenticated
   * resource) is the resource that is returned by the
   * {@link org.everit.osgi.authentication.context.AuthenticationContext#getCurrentResourceId()}
   * method if there is no authenticated Resource ID assigned to the current thread. The value of
   * this property is stored in the configuration.
   */
  public static final String PROP_DEFAULT_RESOURCE_ID =
      "org.everit.authentication.context.ri.DEFAULT_RESOURCE_ID";

  /**
   * The Resource ID assigned to the actual thread.
   */
  private final ThreadLocal<Long> currentResourceId = new ThreadLocal<Long>();

  /**
   * The default Resource ID.
   */
  private long defaultResourceId;

  /**
   * Constructor. It initializes the {@link AuthenticationContextImpl#defaultResourceId}.
   *
   * @param propertyManager
   *          The {@link PropertyManager} used to load/store the value of the
   *          {@link AuthenticationContextImpl#PROP_DEFAULT_RESOURCE_ID}.
   * @param resourceService
   *          The {@link ResourceService} used to initialize the resource of the default subject.
   */
  public AuthenticationContextImpl(final PropertyManager propertyManager,
      final ResourceService resourceService) {
    String defaultSubjectResourceIdProperty = propertyManager.getProperty(PROP_DEFAULT_RESOURCE_ID);
    if (defaultSubjectResourceIdProperty == null) {
      defaultResourceId = resourceService.createResource();
      propertyManager.addProperty(PROP_DEFAULT_RESOURCE_ID, String.valueOf(defaultResourceId));
    } else {
      defaultResourceId = Long.parseLong(defaultSubjectResourceIdProperty);
    }
  }

  @Override
  public long getCurrentResourceId() {
    Long resourceId = currentResourceId.get();
    if (resourceId == null) {
      return defaultResourceId;
    }
    return resourceId.longValue();
  }

  @Override
  public long getDefaultResourceId() {
    return defaultResourceId;
  }

  @Override
  public <T> T runAs(final long authenticatedResourceId, final Supplier<T> authenticatedAction) {
    Objects.requireNonNull(authenticatedAction, "authenticatedAction cannot be null");
    Long localResourceId = currentResourceId.get();
    currentResourceId.set(authenticatedResourceId);
    T rval = null;
    try {
      rval = authenticatedAction.get();
    } finally {
      currentResourceId.set(localResourceId);
    }
    return rval;
  }

}
