package net.unit8.jsonspec;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.Date;
import java.util.Set;

/**
 * A proxy object of the Java Date.
 *
 * @author kawasima
 */
public class DateProxy extends Date implements ProxyObject {
    private static final Set<String> PROTOTYPE_FUNCTIONS = Set.of(
            "getTime",
            "toISOString",
            "toJSON",
            "toString"
    );

    public DateProxy(Date date) {
        super(date.getTime());
    }

    public DateProxy(long epoch) {
        super(epoch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getMember(String key) {
        switch (key) {
            case "getTime":
                return (ProxyExecutable) arguments -> getTime();
            case "toJSON":
            case "toISOString":
                return (ProxyExecutable) arguments -> ISO8601Utils.format(this, true);
            case "toString":
                // Currently defaulting to Date.toString, but could improve
                return (ProxyExecutable) arguments -> toString();
            default:
                throw new UnsupportedOperationException("This date does not support: " + key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getMemberKeys() {
        return PROTOTYPE_FUNCTIONS.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasMember(String key) {
        return PROTOTYPE_FUNCTIONS.contains(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putMember(String key, Value value) {
        throw new UnsupportedOperationException("This date does not support adding new properties/functions.");
    }
}