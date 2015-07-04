package com.shav.therottengame.util.javatuples;

/*
 * =============================================================================
 * 
 *   Copyright (c) 2010, The JAVATUPLES team (http://www.javatuples.org)
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
 * 
 * =============================================================================
 */

import java.util.Collection;
import java.util.Iterator;

import com.shav.therottengame.util.valueintf.IValue0;
import com.shav.therottengame.util.valueintf.IValue1;
import com.shav.therottengame.util.valueintf.IValue2;


/**
 * <p>
 * A tuple of three elements.
 * </p> 
 * 
 * @since 1.0
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class Triplet<A,B,C> 
        extends Tuple
        implements IValue0<A>,
                   IValue1<B>,
                   IValue2<C> {

    private static final long serialVersionUID = -1877265551599483740L;

    private static final int SIZE = 3;

    private final A val0;
    private final B val1;
    private final C val2;
    
    
    
    public static <A,B,C> Triplet<A,B,C> with(final A value0, final B value1, final C value2) {
        return new Triplet<A,B,C>(value0,value1,value2);
    }

    
    /**
     * <p>
     * Create tuple from array. Array has to have exactly three elements.
     * </p>
     * 
     * @param <X> the array component type 
     * @param array the array to be converted to a tuple
     * @return the tuple
     */
    public static <X> Triplet<X,X,X> fromArray(final X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements in order to create a Triplet. Size is " + array.length);
        }
        return new Triplet<X,X,X>(array[0],array[1],array[2]);
    }

    
    /**
     * <p>
     * Create tuple from collection. Collection has to have exactly three elements.
     * </p>
     * 
     * @param <X> the collection component type 
     * @param collection the collection to be converted to a tuple
     * @return the tuple
     */
    public static <X> Triplet<X,X,X> fromCollection(final Collection<X> collection) {
        return fromIterable(collection);
    }


    
    /**
     * <p>
     * Create tuple from iterable. Iterable has to have exactly three elements.
     * </p>
     * 
     * @param <X> the iterable component type 
     * @param iterable the iterable to be converted to a tuple
     * @return the tuple
     */
    public static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable) {
        return fromIterable(iterable, 0, true);
    }

    
    
    /**
     * <p>
     * Create tuple from iterable, starting from the specified index. Iterable
     * can have more (or less) elements than the tuple to be created.
     * </p>
     * 
     * @param <X> the iterable component type 
     * @param iterable the iterable to be converted to a tuple
     * @return the tuple
     */
    public static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable, int index) {
        return fromIterable(iterable, index, false);
    }

    


    private static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {
        
        if (iterable == null) {
            throw new IllegalArgumentException("Iterable cannot be null");
        }

        boolean tooFewElements = false; 
        
        X element0 = null;
        X element1 = null;
        X element2 = null;
        
        final Iterator<X> iter = iterable.iterator();
        
        int i = 0;
        while (i < index) {
            if (iter.hasNext()) {
                iter.next();
            } else {
                tooFewElements = true;
            }
            i++;
        }
        
        if (iter.hasNext()) {
            element0 = iter.next();
        } else {
            tooFewElements = true;
        }
        
        if (iter.hasNext()) {
            element1 = iter.next();
        } else {
            tooFewElements = true;
        }
        
        if (iter.hasNext()) {
            element2 = iter.next();
        } else {
            tooFewElements = true;
        }
        
        if (tooFewElements && exactSize) {
            throw new IllegalArgumentException("Not enough elements for creating a Triplet (3 needed)");
        }
        
        if (iter.hasNext() && exactSize) {
            throw new IllegalArgumentException("Iterable must have exactly 3 available elements in order to create a Triplet.");
        }
        
        return new Triplet<X,X,X>(element0, element1, element2);
        
    }
    

    
    
    
    
    public Triplet(
            final A value0,
            final B value1,
            final C value2) {
        super(value0, value1, value2);
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
    }


    public A getValue0() {
        return this.val0;
    }


    public B getValue1() {
        return this.val1;
    }


    public C getValue2() {
        return this.val2;
    }


    @Override
    public int getSize() {
        return SIZE;
    }
    
}
