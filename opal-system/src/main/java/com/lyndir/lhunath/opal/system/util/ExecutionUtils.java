package com.lyndir.lhunath.opal.system.util;

/**
 * <i>08 02, 2011</i>
 *
 * @author lhunath
 */
public abstract class ExecutionUtils {

    /**
     * @return The stack element that calls the current method (ie. the line that calls the method that calls {@link #caller()}).
     */
    public static StackTraceElement caller() {

        return stack( 2 );
    }

    /**
     * Obtain an element from the current execution stack at the given level.
     *
     * <table>
     * <tr>
     * <th>{@code level}</th>
     * <th>Meaning</th>
     * </tr>
     * <tr>
     * <td>0</td>
     * <td>The line that executes the call to {@link #stack(int)}</td>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>The line that calls the method which executes the call to {@link #stack(int)}</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>The line that calls the method that calls the method which executes the call to {@link #stack(int)}</td>
     * </tr>
     * <tr>
     * <td colspan="2">...</td>
     * </tr>
     * </table>
     *
     * @param level The level of the stack.
     *
     * @return The element from the current execution at the given level.
     */
    public static StackTraceElement stack(final int level) {

        return new Throwable().getStackTrace()[level + 1];
    }
}
