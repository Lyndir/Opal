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
package com.lyndir.lhunath.opal.security.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.lyndir.lhunath.opal.security.*;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.security.service.SecurityService;
import com.lyndir.lhunath.opal.system.collection.Iterators2;
import com.lyndir.lhunath.opal.system.collection.Pair;
import com.lyndir.lhunath.opal.system.error.IllegalRequestException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.logging.exception.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SecurityServiceImpl}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class SecurityServiceImpl implements SecurityService {

    static final Logger logger = Logger.get( SecurityServiceImpl.class );

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAccess(final Permission permission, final SecurityToken token, final SecureObject<?, ?> object) {

        try {
            assertAccess( permission, token, object );
            return true;
        }

        catch (PermissionDeniedException ignored) {
            return false;
        }
    }

    @Override
    public <T extends SecureObject<?, ?>> Iterator<T> filterAccess(final Permission permission, final SecurityToken token,
                                                                final Iterator<T> source) {

        return Iterators.filter(
                source, new Predicate<T>() {

            @Override
            public boolean apply(final T input) {

                return hasAccess( permission, token, input );
            }
        } );
    }

    @Override
    public <T extends SecureObject<?, ?>> ListIterator<T> filterAccess(final Permission permission, final SecurityToken token,
                                                                    final ListIterator<T> source) {

        return Iterators2.filter(
                source, new Predicate<T>() {

            @Override
            public boolean apply(final T input) {

                return hasAccess( permission, token, input );
            }
        } );
    }

    @Override
    public <S extends SecureObject<?, ?>> S assertAccess(final Permission permission, final SecurityToken token, final S object)
            throws PermissionDeniedException {

        checkNotNull( token, "Given security token must not be null." );

        // Automatically grant permission when no object is given or required permission is NONE.
        if (object == null || permission == Permission.NONE) {
            logger.dbg(
                    "Permission Granted: No permission necessary for: %s@%s", //
                    permission, object );
            return object;
        }

        // Automatically grant permission to INTERNAL_USE token.
        if (token.isInternalUseOnly()) {
            logger.dbg(
                    "Permission Granted: INTERNAL_USE token for: %s@%s", //
                    permission, object );
            return object;
        }

        // Determine what permission level to grant on the object for the token.
        Permission tokenPermission;
        if (ObjectUtils.isEqual( object.getOwner(), token.getActor() ))
            tokenPermission = Permission.ADMINISTER;
        else
            tokenPermission = object.getACL().getSubjectPermission( token.getActor() );

        // If INHERIT, recurse.
        if (tokenPermission == Permission.INHERIT) {
            if (object.getParent() == null) {
                logger.dbg(
                        "Permission Denied: Can't inherit permissions, no parent set for: %s@%s", //
                        permission, object );
                throw new PermissionDeniedException( permission, object, "Had to inherit permission but no parent set" );
            }

            logger.dbg(
                    "Inheriting permission for: %s@%s", //
                    permission, object );
            assertAccess( permission, token, object.getParent() );
            return object;
        }

        // Else, check if granted permission provides required permission.
        if (!isPermissionProvided( tokenPermission, permission )) {
            logger.dbg(
                    "Permission Denied: Token authorizes %s (ACL default? %s), insufficient for: %s@%s", //
                    tokenPermission, object.getACL().isSubjectPermissionDefault( token.getActor() ), permission, object );
            throw new PermissionDeniedException( permission, object, "Security Token %s grants permissions %s ", token, tokenPermission );
        }

        // No permission denied thrown, grant permission.
        logger.dbg(
                "Permission Granted: Token authorization %s matches for: %s@%s", //
                tokenPermission, permission, object );
        return object;
    }

    private static boolean isPermissionProvided(final Permission givenPermission, final Permission requestedPermission) {

        if (givenPermission == requestedPermission)
            return true;
        if (givenPermission == null || requestedPermission == null)
            return false;

        for (final Permission inheritedGivenPermission : givenPermission.getProvided())
            if (isPermissionProvided( inheritedGivenPermission, requestedPermission ))
                return true;

        return false;
    }

    @Override
    public Permission getDefaultPermission(final SecurityToken token, final SecureObject<?, ?> o)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );
        assertAccess( Permission.ADMINISTER, token, o );

        return o.getACL().getDefaultPermission();
    }

    @Override
    public Permission getEffectivePermissions(final SecurityToken token, final Subject subject, final SecureObject<?, ?> o)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );
        assertAccess( Permission.ADMINISTER, token, o );

        Permission permission = o.getACL().getSubjectPermission( subject );
        if (permission == Permission.INHERIT) {
            SecureObject<?, ?> parent = checkNotNull( o.getParent(), "Secure object's default permission is INHERIT but has no parent." );

            return getEffectivePermissions( token, subject, parent );
        }

        return permission;
    }

    @Override
    public Iterator<Pair<Subject, Permission>> iterateSubjectPermissions(final SecurityToken token, final SecureObject<?, ?> o)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );
        assertAccess( Permission.ADMINISTER, token, o );

        return Iterators.unmodifiableIterator(
                new AbstractIterator<Pair<Subject, Permission>>() {

                    public final Iterator<Subject> permittedSubjects;

                    {
                        permittedSubjects = o.getACL().getPermittedSubjects().iterator();
                    }

                    @Override
                    protected Pair<Subject, Permission> computeNext() {

                        try {
                            if (permittedSubjects.hasNext()) {
                                Subject subject = permittedSubjects.next();
                                return Pair.of( subject, getEffectivePermissions( token, subject, o ) );
                            }
                        }
                        catch (PermissionDeniedException e) {
                            throw new InternalInconsistencyException( "While evaluating subject permissions", e );
                        }

                        return endOfData();
                    }
                } );
    }

    @Override
    public int countPermittedSubjects(final SecurityToken token, final SecureObject<?, ?> o)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );

        assertAccess( Permission.ADMINISTER, token, o );
        return o.getACL().getPermittedSubjects().size();
    }

    @Override
    public void setDefaultPermission(final SecurityToken token, final SecureObject<?, ?> o, final Permission permission)
            throws PermissionDeniedException {

        checkNotNull( o, "Given secure object must not be null." );

        assertAccess( Permission.ADMINISTER, token, o );
        o.getACL().setDefaultPermission( permission );
    }

    @Override
    public void setPermission(final SecurityToken token, final SecureObject<?, ?> object, final Subject subject, final Permission permission)
            throws PermissionDeniedException, IllegalRequestException {

        checkNotNull( object, "Given secure object must not be null." );
        Preconditions.checkNotNull( subject, "Given subject must not be null." );

        if (ObjectUtils.isEqual( object.getOwner(), subject ))
            throw new IllegalRequestException( "Given subject must not be the object's owner." );

        assertAccess( Permission.ADMINISTER, token, object );
        object.getACL().setSubjectPermission( subject, permission );
    }
}
