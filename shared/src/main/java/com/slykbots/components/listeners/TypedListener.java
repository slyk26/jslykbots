package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TypedListener<T extends Event> implements EventListener {

    private final Consumer<T> c;
    private final Class<T> t;

    public TypedListener(Consumer<T> c, Class<T> t) {
        this.c = c;
        this.t = t;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (t.isInstance(event)) {
            this.c.accept(t.cast(event));
        }
    }

}