/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.opal.security;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.util.*;
import javax.annotation.Nonnull;


/**
 * <h2>{@link ACL}<br> <sub>A list of access control grants.</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class ACL {

    static final Logger logger = Logger.get( ACL.class );

    private final Map<Subject, Permission> subjectPermissions = new HashMap<>();
    @Nonnull
    private Permission defaultPermission;

    /**
     * An {@link ACL} that grants subjects the {@link Permission#INHERIT} permission by default.
     */
    public ACL() {

        this( Permission.INHERIT );
    }

    /**
     * @param defaultPermission The permission granted to subjects not explicitly specified.
     */
    public ACL(@Nonnull final Permission defaultPermission) {

        this.defaultPermission = defaultPermission;
    }

    /**
     * Change the permission of a subject in this access control. Any current permission of the subject is revoked and replaced by the
     * given
     * permission.
     *
     * @param permission The permission that will be granted to the given subject.
     */
    public void setDefaultPermission(@Nonnull final Permission permission) {

        checkNotNull( permission, "Given permission must not be null." );

        defaultPermission = permission;
    }

    /**
     * Change the permission of a subject in this access control. Any current permission of the subject is revoked and replaced by the
     * given permission.
     *
     * @param subject    The subject that will be granted the given permission.
     * @param permission The permission that will be granted to the given subject.
     */
    public void setSubjectPermission(final Subject subject, final Permission permission) {

        checkNotNull( subject, "Given subject must not be null." );
        checkNotNull( permission, "Given permission must not be null." );

        subjectPermissions.put( subject, permission );
    }

    /**
     * Unset any specific permissions for the given subjects in this ACL.  After this operation, the subject's permissions will be
     * determined by the ACL's default permissions.
     *
     * @param subject The subject whose permissions to unset.
     *
     * @return The subject's former permissions in this ACL or {@code null} if this subject's permissions were already determined by
     *         the
     *         default permissions.
     */
    public Permission unsetSubjectPermission(final Subject subject) {

        checkNotNull( subject, "Given subject must not be null." );

        return subjectPermissions.remove( subject );
    }

    /**
     * Revoke the permission of a subject in this access control.
     *
     * @param subject The subject that will be granted the given permission.
     */
    public void revokeSubjectPermission(final Subject subject) {

        checkNotNull( subject, "Given subject must not be null." );

        subjectPermissions.remove( subject );
    }

    /**
     * @return The permission granted to subjects that have no specific subject permission in this ACL.
     */
    public Permission getDefaultPermission() {

        return checkNotNull( defaultPermission, "Default permission is unset." );
    }

    /**
     * The subject's permission is either the one set for him through {@link #setSubjectPermission(Subject, Permission)} or the default
     * permission
     * of
     * this ACL.
     *
     * @param subject The subject whose permission to look up. {@code null} represents an anonymous subject.
     *
     * @return The permission granted to the given subject by this access control.
     */
    public Permission getSubjectPermission(final Subject subject) {

        if (isSubjectPermissionDefault( subject ))
            return getDefaultPermission();

        return checkNotNull( subjectPermissions.get( subject ), "Permission for %s is unset.", subject );
    }

    /**
     * @param subject The subject whose permission to look up. {@code null} represents an anonymous subject.
     *
     * @return {@code true} if the subject's permissions in this ACL are determined by the default ACL.
     */
    public boolean isSubjectPermissionDefault(final Subject subject) {

        return !subjectPermissions.containsKey( subject );
    }

    /**
     * @return The subjects that have non-default permissions set in this ACL.
     */
    public ImmutableSet<Subject> getPermittedSubjects() {

        return ImmutableSet.copyOf( subjectPermissions.keySet() );
    }

    @Override
    public String toString() {

        return String.format( "{acl: default=%s, subjects=%s}", defaultPermission, subjectPermissions );
    }
}
