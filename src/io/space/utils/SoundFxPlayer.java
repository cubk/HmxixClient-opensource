package io.space.utils;

import java.applet.Applet;
import java.applet.AudioClip;

public class SoundFxPlayer {
    public static final Sound ENABLE_MOD = new Sound("on.wav");
    public static final Sound DISABLE_MOD = new Sound("off.wav");

    public static void playSound(Sound sound) {
        sound.audioClip.play();
    }

    public static class Sound {
        private final AudioClip audioClip;

        public Sound(String resourceName) {
            this.audioClip = Applet.newAudioClip(SoundFxPlayer.class.getResource("/assets/" + resourceName));
        }
    }
}
