package net.unit8.jsonspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class SpecEngineImpl implements SpecEngine {
    private final Context ctx;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private String cwd = "";
    SpecEngineImpl() {
        Map<String, String> options = new HashMap<>();
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Paths.get(cwd).toAbsolutePath().toString());

        HostAccess.Builder builder = HostAccess.newBuilder();
        builder.targetTypeMapping(Value.class, Object.class, Value::hasArrayElements, (v) -> v.as(List.class))
                .targetTypeMapping(Value.class, Object.class, v -> v.hasMember("getTime"), v -> new Date(v.getMember("getTime").execute().asLong()));
        HostAccess access = builder.build();

        ctx = Context.newBuilder("js")
                .allowExperimentalOptions(true)
                .allowIO(true)
                .options(options)
                .allowHostAccess(access)
                .build();
        ctx.eval("js", "const specs = {}; const s = require('@json-spec/core'); const gen = require('@json-spec/core/gen');");
    }

    @Override
    public void load(File specFile) throws IOException {
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

    @Override
    public void close() throws Exception {
        ctx.close();
    }
}
