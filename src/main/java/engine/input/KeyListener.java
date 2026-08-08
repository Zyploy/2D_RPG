package engine.input;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private boolean[] keys = new boolean[GLFW_KEY_LAST + 1];

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
            }
        }
    }

    public static boolean isKeyPressed(int key) {
        if(key < get().keys.length) {
            return get().keys[key];
        }
        return false;
    }
}