package engine.input;

import engine.Window;

import static engine.util.Constants.ASPECT;
import static org.lwjgl.opengl.GL11.glViewport;

public class ResizeListener {
    private static ResizeListener instance;
    private int vpWidth, vpHeight;
    private int x, y;

    private ResizeListener() {
        this.vpWidth = Window.get().getWidth();
        this.vpHeight = Window.get().getHeight();
    }

    public static ResizeListener get() {
        if(ResizeListener.instance == null) {
            ResizeListener.instance = new ResizeListener();
        }
        return ResizeListener.instance;
    }

    public static void resizeCallback(long window, int width, int height) {
        Window.get().setSize(width, height);

        get().vpHeight = height;
        get().vpWidth = (int)(get().vpHeight * ASPECT);
        if(get().vpWidth > width) {
            get().vpWidth = width;
            get().vpHeight = (int)(get().vpWidth / ASPECT);
        }

        int vpX = (width - get().vpWidth) / 2;
        int vpY = (height - get().vpHeight) / 2;

        glViewport(vpX, vpY, get().vpWidth, get().vpHeight);
    }

    public static void moveCallback(long window, int xpos, int ypos) {
        get().x = xpos;
        get().y = ypos;
    }

    public static int getVpWidth() {
        return get().vpWidth;
    }

    public static int getVpHeight() {
        return get().vpHeight;
    }

    public static int getX() {
        return get().x;
    }

    public static int getY() {
        return get().y;
    }
}