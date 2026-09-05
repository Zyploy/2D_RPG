package engine.render;

import engine.Window;
import engine.input.ResizeListener;
import org.joml.Matrix4f;

import static engine.util.Constants.*;
import static engine.util.Constants.TILE_SIZE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private static final int POSITION_SIZE = 2;
    private static final int TEX_COORDS_SIZE = 2;

    private static final int POSITION_OFFSET = 0;
    private static final int TEX_COORDS_OFFSET = POSITION_OFFSET + POSITION_SIZE;

    private static final int VERTEX_SIZE = POSITION_SIZE + TEX_COORDS_SIZE;
    private static final int VERTEX_BYTES = VERTEX_SIZE * Float.BYTES;

    private int vao, vbo, ebo;
    private float[] vertices = {
            0.0f, 0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
    };
    private int[] indices = {
            0, 1, 2,
            2, 3, 0,
    };

    private Shader shader;

    public Renderer() {
        this.shader = new Shader("src/main/resources/shaders/default.vert", "src/main/resources/shaders/default.frag");

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_BYTES, POSITION_OFFSET * Float.BYTES);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_BYTES, TEX_COORDS_OFFSET * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void render(float x, float y, int scale, Texture texture) {
        shader.bind();
        texture.bind();

        float tileWidth = (ResizeListener.getVpWidth() / HOR_TILES) * (texture.getWidth() / TILE_SIZE);
        float tileHeight = (ResizeListener.getVpHeight() / VER_TILES) * (texture.getHeight() / TILE_SIZE);

        Matrix4f transform = new Matrix4f().translate(x * tileWidth, y * tileHeight, 0.0f).scale(tileWidth * scale, tileHeight * scale, 1.0f);
        shader.uploadMat4f("uTransform", transform);

        Matrix4f projection = new Matrix4f().ortho(0, ResizeListener.getVpWidth(), ResizeListener.getVpHeight(), 0, -1, 1);
        shader.uploadMat4f("uProjection", projection);

        shader.uploadInt("uTexture", 0);

        glBindVertexArray(vao);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        texture.unbind();
        shader.unbind();
    }
}