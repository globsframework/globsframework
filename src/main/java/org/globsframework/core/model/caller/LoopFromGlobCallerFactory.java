package org.globsframework.core.model.caller;

import org.globsframework.core.metamodel.GlobType;
import org.globsframework.core.metamodel.fields.Field;
import org.globsframework.core.model.Glob;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * The {@link FromGlobCallerFactory} of a type whose factory generates nothing : the plain loop over a table
 * of functions, behind a {@link Proxy} implementing the codec's own interface.
 * <p>
 * Behaviourally identical to a generated one — same functions, same order, and the same isSet / isNull /
 * value, isNull being what {@code getValue} answers null for. Like a generated one it walks the fields it was
 * given, in that order, or every field of the type in index order when it was given none. What it does not
 * give is the point of generating: one call site for the whole loop, seeing every function class it is ever
 * handed, i.e. exactly the megamorphic dispatch a generated caller exists to remove.
 * <p>
 * And it gives it back twice over, because the interfaces are the codec's own : a Proxy reaches its functions
 * reflectively, so every primitive argument is boxed on every call and every call goes through an
 * {@code Object[]}. It is a fallback so that a codec <em>can</em> keep one code path, not an alternative —
 * one that cares should ask {@link FromGlobCallerFactory#generatedCallerFor} and keep its own hand-written
 * path when that answers null.
 * <p>
 * Unlike a generated caller it accepts any Glob of the type, whoever built it.
 */
public class LoopFromGlobCallerFactory implements FromGlobCallerFactory {
    private final GlobType type;

    public LoopFromGlobCallerFactory(GlobType type) {
        this.type = type;
    }

    public GlobType getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T, D> T create(String name, Functions<D> functions, Field[] order,
                           Class<T> tClass, Class<D> dClass, Class<?>... argument) {
        CallerName.check(name);
        Method callerMethod = FromGlobCallerFactory.callerMethod(tClass, argument);
        Method functionMethod = FromGlobCallerFactory.functionMethod(dClass, argument);
        Field[] fields = FromGlobCallerFactory.fieldsToCall(type, order);
        Object[] table = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            table[i] = CallerShape.checked(functions.forField(fields[i]),
                    fields[i].getName() + " of " + type.getName());
        }
        functionMethod.setAccessible(true);
        InvocationHandler handler = (proxy, method, args) -> {
            // by shape rather than by identity : a Proxy hands back the Method of the interface the call
            // came through, which is not always the one getMethods() found it in
            if (method.getName().equals(callerMethod.getName())
                && Arrays.equals(method.getParameterTypes(), callerMethod.getParameterTypes())) {
                Glob data = (Glob) args[0];
                // isSet, isNull and the value, then whatever the pass carries : the same arguments the
                // generated caller pushes, in the same order
                Object[] call = new Object[FUNCTION_BEFORE.length + args.length - 1];
                System.arraycopy(args, 1, call, FUNCTION_BEFORE.length, args.length - 1);
                for (int i = 0; i < fields.length; i++) {
                    Object value = data.getValue(fields[i]);
                    call[0] = data.isSet(fields[i]);
                    call[1] = value == null;
                    call[2] = value;
                    try {
                        functionMethod.invoke(table[i], call);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                }
                return null;
            }
            return switch (method.getName()) {
                case "toString" -> tClass.getName() + " looping over " + fields.length + " "
                                   + dClass.getName() + " of " + type.getName();
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException(
                        method + " : this caller only answers " + callerMethod.getName() + ".");
            };
        };
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, handler);
    }
}
