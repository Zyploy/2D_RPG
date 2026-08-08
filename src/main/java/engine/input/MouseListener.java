package engine.input;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;
    private float x, y, lastX, lastY;
    private float scrollX, scrollY;
    private boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private MouseListener() {
        this.lastX = 0;
        this.lastY = 0;
        this.x = 0;
        this.y = 0;
        this.scrollX = 0;
        this.scrollY = 0;
    }

    public static MouseListener get() {
        if(MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        get().lastX = get().x;
        get().lastY = get().y;
        get().x = (float)xpos;
        get().y = (float)ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if(button < get().mouseButtons.length) {
            if(action == GLFW_PRESS) {
                get().mouseButtons[button] = true;
            } else if(action == GLFW_RELEASE) {
                get().mouseButtons[button] = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xoffset, double yoffset) {
        get().scrollX = (float)xoffset;
        get().scrollY = (float)yoffset;
    }

    public static float getX() {
        return get().x;
    }

    public static float getY() {
        return get().y;
    }

    public static float getDx() {
        return get().x - get().lastX;
    }

    public static float getDy() {
        return get().y - get().lastY;
    }

    public static float getScrollX() {
        return get().scrollX;
    }

    public static float getScrollY() {
        return get().scrollY;
    }

    public static boolean isMouseButtonPressed(int button) {
        if(button < get().mouseButtons.length) {
            return get().mouseButtons[button];
        }
        return false;
    }
}