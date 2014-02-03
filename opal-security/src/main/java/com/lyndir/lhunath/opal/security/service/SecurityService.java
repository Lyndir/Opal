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
package com.lyndir.lhunath.opal.security.service;

import com.lyndir.lhunath.opal.security.*;
import com.lyndir.lhunath.opal.security.error.PermissionDeniedException;
import com.lyndir.lhunath.opal.system.collection.Pair;
import com.lyndir.lhunath.opal.system.error.IllegalRequestException;
import java.util.Iterator;
import java.util.ListIterator;


/**
 * <h2>{@link SecurityService}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public interface SecurityService {

    /**
     * @param permission The permission required on the given object to proceed with the request.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param object     The object that is the target of the request.
     *
     * @return The object from parameter {@code object}.
     *
     * @throws PermissionDeniedException When permission is required and the object is not {@code null} while there is no security
     *                                   token or the token doesn't grant the necessary permission on the object.
     * @see #hasAccess(Permission, SecurityToken, SecureObject)
     */
    <S extends Subject, O extends SecureObject<S, ?>> O assertAccess(Permission permission, SecurityToken<S> token, O object)
            throws PermissionDeniedException;

    /**
     * Check whether the given token grants permission to perform an operation on the given object that requires the given permission.
     *
     * @param permission The permission required on the given object to proceed with the request.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param object     The object that is the target of the request.
     *
     * @return {@code true}: The given token grants the given permission on the given object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> boolean hasAccess(Permission permission, SecurityToken<S> token, O object);

    /**
     * Filter an iterator of SecureObjects, only allowing those on which the given token provides the given permission.
     *
     * @param permission The permission required on objects from the given iterator.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param source     The source of objects to filter.
     *
     * @return An iterator that provides elements from the source on which the given permission is granted for the given token.
     */
    <S extends Subject, O extends SecureObject<S, ?>> Iterator<O> filterAccess(Permission permission, SecurityToken<S> token, Iterator<O> source);

    /**
     * Filter an iterator of SecureObjects, only allowing those on which the given token provides the given permission.
     *
     * @param permission The permission required on objects from the given iterator.
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param source     The source of objects to filter.
     *
     * @return An iterator that provides elements from the source on which the given permission is granted for the given token.
     */
    <S extends Subject, O extends SecureObject<S, ?>> ListIterator<O> filterAccess(Permission permission, SecurityToken<S> token, ListIterator<O> source);

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return The permissions granted to any subject that doesn't have a specific subject permission set on the object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> Permission getDefaultPermission(SecurityToken<S> token, O o)
            throws PermissionDeniedException;

    /**
     * @param token   The token used to authenticate the available permissions on the given object.
     * @param subject The subject who's permission on the given object to retrieve.  {@code null} represents an anonymous subject.
     * @param o       The object whose permissions to retrieve.
     *
     * @return The effective permissions granted for this object to the given subject are this object's permissions for the subject or its
     *         parent's effective permissions for the subject if this object's permissions for the subject are {@link Permission#INHERIT}.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> Permission getEffectivePermissions(SecurityToken<S> token, Subject subject, O o)
            throws PermissionDeniedException;

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return All subjects that have non-default access to the given object and the actual permissions granted to them for the object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> Iterator<Pair<Subject, Permission>> iterateSubjectPermissions(SecurityToken<S> token, O o)
            throws PermissionDeniedException;

    /**
     * @param token The token used to authenticate the available permissions on the given object.
     * @param o     The object whose permissions to retrieve.
     *
     * @return The total number of subjects that have non-default access to the given object.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> int countPermittedSubjects(SecurityToken<S> token, O o)
            throws PermissionDeniedException;

    /**
     * Change the permissions a given to any subjects without specific permissions.
     *
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object whose permissions must be modified.
     * @param permission The new permissions the to set on the given secure object for the given subject.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     */
    <S extends Subject, O extends SecureObject<S, ?>> void setDefaultPermission(SecurityToken<S> token, O o, Permission permission)
            throws PermissionDeniedException;

    /**
     * Change the permissions a given subject has on a given secure object.
     *
     * @param token      The token used to authenticate the available permissions on the given object.
     * @param o          The object whose permissions must be modified.
     * @param subject    The subject for whom permissions must be modified.
     * @param permission The new permissions the to set on the given secure object for the given subject.
     *
     * @throws PermissionDeniedException When the security token doesn't grant {@link Permission#ADMINISTER} on the object.
     * @throws IllegalRequestException   When the given subject is the object's owner.
     */
    <S extends Subject, O extends SecureObject<S, ?>> void setPermission(SecurityToken<S> token, O o, Subject subject, Permission permission)
            throws PermissionDeniedException, IllegalRequestException;
}
