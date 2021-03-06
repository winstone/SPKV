/*
 *  Copyright (c) 2012 Jan Kotek
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mapdb;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Functional stuff. Tuples, function, callback methods etc..
 *
 * @author Jan Kotek
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class Fun {


	public static final Comparator<Comparable> COMPARATOR = new Comparator<Comparable>() {
        @Override
        public int compare(Comparable o1, Comparable o2) {
            if(o1 == null)
                return o2 == null?0:-1;
            if(o2 == null) return 1;

            if(o1 == HI)
                return o2 == HI?0:1;
            if(o2 == HI) return -1;

            return o1.compareTo(o2);
        }
    };

    public static final Comparator<Comparable> REVERSE_COMPARATOR = new Comparator<Comparable>() {
        @Override
        public int compare(Comparable o1, Comparable o2) {
            return -COMPARATOR.compare(o1,o2);
        }
    };

    public static final Comparator<Tuple2> TUPLE2_COMPARATOR = new Tuple2Comparator(null,null);
    public static final Comparator<Tuple3> TUPLE3_COMPARATOR = new Tuple3Comparator(null,null,null);
    public static final Comparator<Tuple4> TUPLE4_COMPARATOR = new Tuple4Comparator(null,null,null,null);

    private Fun(){}

    /** positive infinity object. Is larger than anything else. Used in tuple comparators.
     * Negative infinity is represented by 'null' */
    public static final Comparable HI = new Comparable(){
        @Override public String toString() {
            return "HI";
        }

        @Override
        public int compareTo(final Object o) {
            return o==HI?0:1; //always greater than anything else
        }
    };

    /** autocast version of `HI`*/
    public static final <A> A HI(){
        return (A) HI;
    }

    public static <A,B> Tuple2<A,B> t2(A a, B b) {
        return new Tuple2<A, B>(a,b);
    }

    public static <A,B,C> Tuple3<A,B,C> t3(A a, B b, C c) {
        return new Tuple3<A, B, C>((A)a, (B)b, (C)c);
    }

    public static <A,B,C,D> Tuple4<A,B,C,D> t4(A a, B b, C c, D d) {
        return new Tuple4<A, B, C, D>(a,b,c,d);
    }

    public static <K> Function1<K,K> noTransformExtractor() {
        return new Function1<K, K>() {
            @Override
            public K run(K k) {
                return k;
            }
        };
    }


    static public final class Tuple2<A,B> implements Comparable, Serializable {
		
    	private static final long serialVersionUID = -8816277286657643283L;
		
		final public A a;
        final public B b;

        public Tuple2(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override public int compareTo(Object o) {
            return TUPLE2_COMPARATOR.compare(this, (Tuple2) o);
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Tuple2 tuple2 = (Tuple2) o;

            if (a != null ? !a.equals(tuple2.a) : tuple2.a != null) return false;
            if (b != null ? !b.equals(tuple2.b) : tuple2.b != null) return false;

            return true;
        }

        @Override public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }

        @Override public String toString() {
            return "Tuple2[" + a +", "+b+"]";
        }
    }

    final static public class Tuple3<A,B,C> implements Comparable, Serializable{

    	private static final long serialVersionUID = 11785034935947868L;
    	
		final public A a;
        final public B b;
        final public C c;

        public Tuple3(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override public int compareTo(Object o) {
            return TUPLE3_COMPARATOR.compare(this, (Tuple3) o);
        }


        @Override public String toString() {
            return "Tuple3[" + a +", "+b+", "+c+"]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple3 tuple3 = (Tuple3) o;

            if (a != null ? !a.equals(tuple3.a) : tuple3.a != null) return false;
            if (b != null ? !b.equals(tuple3.b) : tuple3.b != null) return false;
            if (c != null ? !c.equals(tuple3.c) : tuple3.c != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            return result;
        }
    }

    final static public class Tuple4<A,B,C,D> implements Comparable, Serializable{

    	private static final long serialVersionUID = 1630397500758650718L;
    	
		final public A a;
        final public B b;
        final public C c;
        final public D d;

        public Tuple4(A a, B b, C c, D d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        @Override public int compareTo(Object o) {
            return TUPLE4_COMPARATOR.compare(this, (Tuple4) o);
        }


        @Override public String toString() {
            return "Tuple4[" + a +", "+b+", "+c+", "+d+"]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple4 tuple4 = (Tuple4) o;

            if (a != null ? !a.equals(tuple4.a) : tuple4.a != null) return false;
            if (b != null ? !b.equals(tuple4.b) : tuple4.b != null) return false;
            if (c != null ? !c.equals(tuple4.c) : tuple4.c != null) return false;
            if (d != null ? !d.equals(tuple4.d) : tuple4.d != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            result = 31 * result + (d != null ? d.hashCode() : 0);
            return result;
        }
    }


    public static final class Tuple2Comparator<A,B> implements Comparator<Tuple2<A,B>>,Serializable {

        protected final Comparator a;
        protected final Comparator b;

        public Tuple2Comparator(Comparator<A> a, Comparator<B> b) {
            this.a = a==null? COMPARATOR :a;
            this.b = b==null? COMPARATOR :b;
        }

        @Override
        public int compare(final Tuple2 o1, final Tuple2 o2) {
            int i = a.compare(o1.a,o2.a);
            if(i!=0) return i;
            i = b.compare(o1.b,o2.b);
            return i;
        }
    }

    public static final class Tuple3Comparator<A,B,C> implements Comparator<Tuple3<A,B,C>>,Serializable  {

        protected final Comparator a;
        protected final Comparator b;
        protected final Comparator c;

        public Tuple3Comparator(Comparator<A> a, Comparator<B> b, Comparator<C> c) {
            this.a = a==null? COMPARATOR :a;
            this.b = b==null? COMPARATOR :b;
            this.c = c==null? COMPARATOR :c;
        }

        @Override
        public int compare(final Tuple3 o1, final Tuple3 o2) {
            int i = a.compare(o1.a,o2.a);
            if(i!=0) return i;
            i = b.compare(o1.b,o2.b);
            if(i!=0) return i;
            return c.compare(o1.c,o2.c);
        }
    }

    public static final class Tuple4Comparator<A,B,C,D> implements Comparator<Tuple4<A,B,C,D>>,Serializable  {

        protected final Comparator a;
        protected final Comparator b;
        protected final Comparator c;
        protected final Comparator d;

        public Tuple4Comparator(Comparator<A> a, Comparator<B> b, Comparator<C> c, Comparator<C> d) {
            this.a = a==null? COMPARATOR :a;
            this.b = b==null? COMPARATOR :b;
            this.c = c==null? COMPARATOR :c;
            this.d = d==null? COMPARATOR :d;
        }

        @Override
        public int compare(final Tuple4 o1, final Tuple4 o2) {
            int i = a.compare(o1.a,o2.a);
            if(i!=0) return i;
            i = b.compare(o1.b,o2.b);
            if(i!=0) return i;
            i = c.compare(o1.c,o2.c);
            if(i!=0) return i;
            return d.compare(o1.d,o2.d);
        }
    }

    public interface Function1<R,A>{
        R run(A a);
    }

    public interface Function2<R,A,B>{
        R run(A a, B b);
    }

    public interface Runnable2<A,B>{
        void run(A a, B b);
    }

    public interface Runnable3<A,B,C>{
        void run(A a, B b, C c);
    }


    public static <K,V> Fun.Function1<K,Fun.Tuple2<K,V>> keyExtractor(){
        return new Fun.Function1<K, Fun.Tuple2<K, V>>() {
            @Override
            public K run(Fun.Tuple2<K, V> t) {
                return t.a;
            }
        };
    }

    public static <K,V> Fun.Function1<V,Fun.Tuple2<K,V>> valueExtractor(){
        return new Fun.Function1<V, Fun.Tuple2<K, V>>() {
            @Override
            public V run(Fun.Tuple2<K, V> t) {
                return t.b;
            }
        };
    }



}
