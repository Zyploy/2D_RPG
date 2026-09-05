package engine.input;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];
    private boolean[] lastKeys = new boolean[GLFW_KEY_LAST + 1];

    private KeyListener() {}

    public static KeyListener get() {
        if(KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if(key < get().keys.length) {
            if(action == GLFW_PRESS) {
                get().keys[key] = true;
            } else if(action == GLFW_RELEASE) {
                get().keys[key] = false;
                get().lastKeys[key] = false;
            }
        }
    }

    public static void endFrame() {
        for(int i = 0; i < get().keys.length; i++) {
            get().lastKeys[i] = get().keys[i];
        }
    }

    public static boolean isKeyPressed(int key) {
        if(key < get().keys.length) {
            return get().keys[key];
        }
        return false;
    }

    public static boolean isKeyPressedOnce(int key) {
        if(key < get().keys.length) {
            return get().keys[key] && !get().lastKeys[key];
        }
        return false;
    }
}