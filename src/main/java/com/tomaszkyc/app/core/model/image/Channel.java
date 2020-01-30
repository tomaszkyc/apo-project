package com.tomaszkyc.app.core.model.image;

import com.tomaszkyc.app.core.services.ServiceFactory;

import java.util.Objects;

public enum Channel {
    RED("red"),
    GREEN("green"),
    BLUE("blue"),
    GRAY("gray");


    Channel(String name) {
        this.name = name;
    }

    public String getName() {

        String translationKey = String.format("app.core.model.channel.%s", name);
        return ServiceFactory.getTranslationService().getTranslation(translationKey);

    }

    public void setName(String name) {
        this.name = name;
    }


    private String name;

    /**
     * method useful when we want to change text to Channel enum object
     * @param text text with one from 4 options, which will be checked if there is any
     *             channel name value
     * @return channel object or null (if proper channel object can't be found)
     */
    public static Channel parseFromText(String text) {

        Channel channel = null;
        Objects.requireNonNull(text, "Given text is null");

        if ( text.toLowerCase().contains( Channel.RED.getName() ) ) {
            channel = Channel.RED;
        }
        else if ( text.toLowerCase().contains( Channel.GREEN.getName() ) ) {
            channel = Channel.GREEN;
        }
        else if ( text.toLowerCase().contains( Channel.BLUE.getName() ) ) {
            channel = Channel.BLUE;
        }
        else if ( text.toLowerCase().contains( Channel.GRAY.getName() ) ) {
            channel = Channel.GRAY;
        }
        return channel;
    }

}
