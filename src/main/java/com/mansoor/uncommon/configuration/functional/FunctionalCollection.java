/*
 * Copyright 2012. Muhammad M. Ashraf
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.mansoor.uncommon.configuration.functional;


import com.mansoor.uncommon.configuration.functional.functions.BinaryFunction;
import com.mansoor.uncommon.configuration.functional.functions.Function;
import com.mansoor.uncommon.configuration.functional.functions.Predicate;
import com.mansoor.uncommon.configuration.functional.functions.UnaryFunction;

import java.util.*;

/**
 * @author mansoor
 * @since 2/7/12
 */
public class FunctionalCollection<T> implements Collection<T> {
    final Collection<T> collection;

    public FunctionalCollection() {
        collection = new ArrayList<T>();
    }

    public FunctionalCollection(final Collection<T> collection) {
        this.collection = collection;
    }

    public FunctionalCollection(final T... values) {
        this.collection = new ArrayList<T>(Arrays.asList(values));
    }


    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean contains(final Object o) {
        return collection.contains(o);
    }

    public Iterator<T> iterator() {
        return collection.iterator();
    }


    public Object[] toArray() {
        return collection.toArray();
    }


    public <T> T[] toArray(final T[] ts) {
        return collection.toArray(ts);
    }


    public boolean add(final T t) {
        return collection.add(t);
    }


    public boolean remove(final Object o) {
        return collection.remove(o);
    }


    public boolean containsAll(final Collection<?> objects) {
        return collection.contains(objects);
    }


    public boolean addAll(final Collection<? extends T> ts) {
        return collection.addAll(ts);
    }


    public boolean removeAll(final Collection<?> objects) {
        return collection.removeAll(objects);
    }


    public boolean retainAll(final Collection<?> objects) {
        return collection.retainAll(objects);
    }


    public void clear() {
        collection.clear();
    }

    /**
     * Applies the given function to each element in the collection
     *
     * @param function function that will be applied
     */
    public void forEach(final Function<T> function) {
        if (function != null) {
            for (final T t : collection) {
                function.apply(t);
            }
        }
    }

    /**
     * Transform this collection to a new collection by applying given
     * function to each element of this collection
     *
     * @param function function that will be applied
     * @param <B>      Type of new collection
     * @return Collection of type B
     */
    public <B> FunctionalCollection<B> map(final UnaryFunction<T, B> function) {
        final FunctionalCollection<B> result = new FunctionalCollection<B>();
        if (function != null) {
            for (final T t : collection) {
                final B b = function.apply(t);
                result.add(b);
            }
        }
        return result;
    }

    /**
     * The infamous FoldLeft method, also know as FOLDL in Haskell, FoldLeft in Scala
     * and Inject in Ruby
     *
     * @param seed     initial value
     * @param function function that will be applied
     * @param <B>      type of the result
     * @return return Object of type B
     */
    public <B> B foldLeft(final B seed, final BinaryFunction<T, B> function) {
        B result = seed;
        if (function != null) {
            for (final T t : collection) {
                result = function.apply(result, t);
            }
        }
        return result;
    }

    Pair<List<T>, List<T>> partition(final Predicate<T> function) {
        final Pair<List<T>, List<T>> seed = new Pair<List<T>, List<T>>(new ArrayList<T>(), new ArrayList<T>());

        return foldLeft(seed, new BinaryFunction<T, Pair<List<T>, List<T>>>() {
            public Pair<List<T>, List<T>> apply(final Pair<List<T>, List<T>> seed, final T input) {
                if (function.apply(input)) {
                    seed.getLeft().add(input);
                } else {
                    seed.getRight().add(input);
                }
                return seed;
            }
        });
    }


    public List<T> asList() {
        if (List.class.isAssignableFrom(collection.getClass())) {
            return (List<T>) collection;
        } else {
            return foldLeft(new ArrayList<T>(), new BinaryFunction<T, ArrayList<T>>() {
                public ArrayList<T> apply(final ArrayList<T> seed, final T input) {
                    seed.add(input);
                    return seed;
                }
            });
        }
    }

    public String toString(final char separator) {
        final StringBuilder stringBuilder = foldLeft(new StringBuilder(), new BinaryFunction<T, StringBuilder>() {
            public StringBuilder apply(final StringBuilder seed, final T input) {
                seed.append(input).append(separator);
                return seed;
            }
        });
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    public String toString() {
        return foldLeft(new StringBuilder(), new BinaryFunction<T, StringBuilder>() {
            public StringBuilder apply(final StringBuilder seed, final T input) {
                seed.append(input).append(" ");
                return seed;
            }
        }).toString();
    }
}
