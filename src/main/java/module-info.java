import org.jspecify.annotations.NullMarked;

@NullMarked
module dev.mccue.microhttp.handler {
    requires transitive org.jspecify;
    requires transitive org.microhttp;

    exports dev.mccue.microhttp.handler;
}