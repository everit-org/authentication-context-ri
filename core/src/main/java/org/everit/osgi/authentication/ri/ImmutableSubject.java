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

public class ImmutableSubject implements Subject {

    private final long resourceId;

    public ImmutableSubject(final long resourceId) {
        super();
        this.resourceId = resourceId;
    }

    @Override
    public boolean equals(final Object obj) {
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
    public long getResourceId() {
        return resourceId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (resourceId ^ (resourceId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableSubject [resourceId=" + resourceId + "]";
    }

}
