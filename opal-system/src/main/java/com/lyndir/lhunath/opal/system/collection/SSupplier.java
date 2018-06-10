package com.lyndir.lhunath.opal.system.collection;

import java.io.Serializable;
import java.util.function.Supplier;


/**
 * <i>09 10, 2011</i>
 *
 * @author lhunath
 */
@FunctionalInterface
public interface SSupplier<T> extends Supplier<T>, Serializable {

}
