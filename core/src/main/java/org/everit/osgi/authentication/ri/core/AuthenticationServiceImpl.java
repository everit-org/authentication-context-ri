/**
 * This file is part of org.everit.osgi.authentication.ri.core.
 *
 * org.everit.osgi.authentication.ri.core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.ri.core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.ri.core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.ri.core;

import org.everit.osgi.authentication.api.AuthenticatedAction;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.api.Subject;

class AuthenticationServiceImpl implements AuthenticationService {

    private class ImmutableSubject implements Subject {

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
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (resourceId != other.resourceId) {
                return false;
            }
            return true;
        }

        private AuthenticationServiceImpl getOuterType() {
            return AuthenticationServiceImpl.this;
        }

        @Override
        public long getResourceId() {
            return resourceId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + getOuterType().hashCode();
            result = (prime * result) + (int) (resourceId ^ (resourceId >>> 32));
            return result;
        }

        @Override
        public String toString() {
            return "ImmutableSubject [resourceId=" + resourceId + "]";
        }

    }

    private final InheritableThreadLocal<ImmutableSubject> currentSubject =
            new InheritableThreadLocal<ImmutableSubject>();

    private final ImmutableSubject defaultSubject;

    public AuthenticationServiceImpl(final long defaultSubjectResourceId) {
        super();
        defaultSubject = new ImmutableSubject(defaultSubjectResourceId);
    }

    @Override
    public Subject getCurrentSubject() {
        ImmutableSubject immutableSubject = currentSubject.get();
        if (immutableSubject != null) {
            return immutableSubject;
        }
        return defaultSubject;
    }

    @Override
    public void logout(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject cannot be null");
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T runAs(final Subject subject, final AuthenticatedAction<T> authenticatedAction) {
        if (subject == null) {
            throw new IllegalArgumentException("subject cannot be null");
        }
        if (authenticatedAction == null) {
            throw new IllegalArgumentException("authenticatedAction cannot be null");
        }
        ImmutableSubject immutableSubject = new ImmutableSubject(subject.getResourceId());
        currentSubject.set(immutableSubject);
        try {
            return authenticatedAction.run();
        } finally {
            currentSubject.set(defaultSubject);
        }
    }

}
