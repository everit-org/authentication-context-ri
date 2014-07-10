/**
 * This file is part of org.everit.osgi.authentication.context.ri.
 *
 * org.everit.osgi.authentication.context.ri is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.context.ri is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.context.ri.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.context.ri.internal;

/**
 * Constants of the {@link AuthenticationComponent}.
 */
public final class AuthenticationConstants {

    /**
     * The component name of the {@link AuthenticationComponent}.
     */
    public static final String COMPONENT_NAME = "org.everit.osgi.authentication.context.ri.Authentication";

    /**
     * The property name of the OSGi filter expression defining which {@link org.everit.osgi.props.PropertyService}
     * should be used by {@link AuthenticationComponent}.
     */
    public static final String PROP_PROPERTY_SERVICE_TARGET = "propertyService.target";

    /**
     * The property name of the OSGi filter expression defining which
     * {@link org.everit.osgi.resource.api.ResourceService} should be used by {@link AuthenticationComponent}.
     */
    public static final String PROP_RESOURCE_SERVICE_TARGET = "resourceService.target";

    private AuthenticationConstants() {
    }

}
