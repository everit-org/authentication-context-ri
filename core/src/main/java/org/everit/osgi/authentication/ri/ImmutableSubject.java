/**
 * This file is part of org.everit.osgi.authentication.ri.
 *
 * org.everit.osgi.authentication.ri is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.ri is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.ri.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri;

import org.everit.osgi.authentication.api.Subject;

/**
 * An immutable {@link Subject} thats resource ID cannot be changed.
 */
public class ImmutableSubject implements Subject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 8682418286332340365L;

    /**
     * The resource ID of this subject.
     */
    private final long resourceId;

    /**
     * Constructor.
     * 
     * @param resourceId
     *            the resource ID of this subject.
     */
    public ImmutableSubject(final long resourceId) {
        super();
        this.resourceId = resourceId;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImmutableSubject other = (ImmutableSubject) obj;
        if (resourceId != other.resourceId) {
            return false;
        }
        return true;
    }

    @Override
    public final long getResourceId() {
        return resourceId;
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (resourceId ^ (resourceId >>> 32));
        return result;
    }

    @Override
    public final String toString() {
        return "ImmutableSubject [resourceId=" + resourceId + "]";
    }

}
