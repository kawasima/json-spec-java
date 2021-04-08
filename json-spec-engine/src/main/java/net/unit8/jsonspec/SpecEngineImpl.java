package net.unit8.jsonspec;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.File;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

class SpecEngineImpl implements SpecEngine {
    private static final Logger LOG = Logger.getLogger(SpecEngineImpl.class.getName());
    private final Context ctx;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private String cwd = "";
    private final boolean cached;

    private static final SpecEngineImpl INSTANCE = new SpecEngineImpl(true);

    private SpecEngineImpl(boolean cached) {
        this.cached = cached;
        Map<String, String> options = new HashMap<>();
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Paths.get(cwd).toAbsolutePath().toString());
        options.put("js.foreign-object-prototype", "true");

        HostAccess.Builder builder = HostAccess.newBuilder();
        builder.targetTypeMapping(Value.class, Object.class, Value::hasArrayElements, (v) -> v.as(List.class))
                .targetTypeMapping(Value.class, Object.class, v -> v.hasMember("getTime"), v -> new Date(v.getMember("getTime").execute().asLong()));
        HostAccess access = builder
                .allowAllImplementations(true)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .allowPublicAccess(true)
                .build();
        LOG.finer("Start to build a context");
        ctx = Context.newBuilder("js")
                .allowExperimentalOptions(true)
                .allowIO(true)
                .options(options)
                .allowHostAccess(access)
                .build();
        LOG.finer("End a build context");
        LOG.finer("Start an initialization specifications");
        ctx.eval("js", "const specs = {}; const s = require('@json-spec/core');" +
                "const gen = require('@json-spec/core/gen');" +
                "const isValid = function(specName, val) { return s.isValid(specs[specName], val) };" +
                "const explain = function(specName, val) { return s.explainData(specs[specName], val) };");
        LOG.finer("End an initialization");
    }

    public static SpecEngineImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void load(File specFile) {
        String relativePath = Paths.get(cwd).toAbsolutePath().relativize(specFile.getAbsoluteFile().toPath())
                .toString()
                .replace('\\', '/');
        ctx.eval("js", "Object.assign(specs, require('./"+ relativePath + "'));");
    }

    @Override
    public <T> Stream<T> generate(String specName, Class<T> clazz) {
        Value generator = ctx.eval("js",
                "(function*() { while(true) { yield gen.generate(s.gen(specs['" + specName + "'])); }})();");

        return Stream.generate(() -> {
            Value obj = generator.invokeMember("next");
            return mapper.convertValue(obj.getMember("value").as(Object.class), clazz);
        });
    }

    private <T> Value toJsValue(T value) {
        Value jsValue = ctx.asValue(value);
        if (!jsValue.isHostObject() || jsValue.isNull()) {
            return jsValue;
        } else if (value.getClass().isArray()) {
            return ctx.asValue(ProxyArray.fromArray(value));
        } else if (value instanceof List) {
            return ctx.asValue(ProxyArray.fromList((List<Object>) value));
        } else if (value instanceof Date) {
            Value date = ctx.eval("js", "new Date()");
            date.invokeMember("setTime", ((Date) value).getTime());
            return date;
        } else if (value instanceof LocalDateTime) {
            Instant inst = ZonedDateTime.of((LocalDateTime) value, ZoneId.systemDefault()).toInstant();
            Value date = ctx.eval("js", "new Date()");
            date.invokeMember("setTime", inst.getEpochSecond() * 1000 + inst.getNano() / 100_000);
            return date;
        } else if (value instanceof LocalDate) {
            Instant inst = ((LocalDate) value).atStartOfDay().toInstant(ZoneOffset.UTC);
            Value date = ctx.eval("js", "new Date()");
            date.invokeMember("setTime", inst.getEpochSecond() * 1000 + inst.getNano() / 100_000);
            return date;
        } else {
            SerializationConfig serializationConfig = mapper.getSerializationConfig();
            ClassIntrospector classIntrospector = serializationConfig.getClassIntrospector();
            JavaType javaType = serializationConfig.getTypeFactory().constructType(value.getClass());
            BeanDescription beanDescription = classIntrospector.forSerialization(serializationConfig, javaType, serializationConfig);
            Map<String, Object> objMap = new LinkedHashMap<>();
            for (BeanPropertyDefinition propDef : beanDescription.findProperties()) {
                objMap.put(propDef.getName(), propDef.getAccessor().getValue(value));
            }
            objMap.replaceAll((k, v) -> toJsValue(objMap.get(k)));
            return ctx.asValue(ProxyObject.fromMap(objMap));
        }
    }

    @Override
    public <T> boolean isValid(String specName, T value) {
        Value validateFunc = ctx.getBindings("js").getMember("isValid");
        return validateFunc.execute(specName, toJsValue(value)).asBoolean();
    }

    @Override
    public <T> Optional<Explain> explain(String specName, T value) {
        Value explainFunc = ctx.getBindings("js").getMember("explain");
        Value result = explainFunc.execute(specName, toJsValue(value));
        if (result.isNull()) {
            return Optional.empty();
        } else {
            return Optional.of(mapper.convertValue(result.as(Object.class), Explain.class));
        }
    }

    @Override
    public void close() throws Exception {
        if (!cached) {
            ctx.close();
        }
    }
}
