/**
 * This file is part of Everit - Authentication Context RI.
 *
 * Everit - Authentication Context RI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - Authentication Context RI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - Authentication Context RI.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.context.ri;

/**
 * Constants of the {@link AuthenticationComponent}.
 */
public final class AuthenticationContextConstants {

    /**
     * The service factory PID of the authentication component.
     */
    public static final String SERVICE_FACTORYPID_AUTHENTICATION_CONTEXT =
            "org.everit.osgi.authentication.context.ri.AuthenticationContext";

    /**
     * The property name of the OSGi filter expression defining which {@link org.everit.osgi.props.PropertyManager}
     * should be used by {@link AuthenticationComponent}.
     */
    public static final String PROP_PROPERTY_MANAGER_TARGET = "propertyManager.target";

    /**
     * The property name of the OSGi filter expression defining which
     * {@link org.everit.osgi.resource.api.ResourceService} should be used by {@link AuthenticationComponent}.
     */
    public static final String PROP_RESOURCE_SERVICE_TARGET = "resourceService.target";

    private AuthenticationContextConstants() {
    }

}
