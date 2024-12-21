package org.jetbrains.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* loaded from: classes4.dex */
public final class Debug {

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    /* loaded from: classes4.dex */
    public @interface Renderer {
        String childrenArray() default "";

        String hasChildren() default "";

        String text() default "";
    }

    private Debug() {
        throw new AssertionError("Debug should not be instantiated");
    }
}
