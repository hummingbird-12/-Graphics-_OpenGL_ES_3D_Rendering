package com.anative.grmillet.hw5_code;

import android.opengl.GLES30;

/**
 * Created by grmillet on 2018-06-22.
 */

public class Mario extends Object {
    float vertices[];
    int n_triangles;
    int vertex_offset = 0;

    public void addGeometry(float[] geom_vertices) {
        vertices = geom_vertices;
        n_triangles = vertices.length / ( 8 * 3 ); // (vert(3) + norm(3) + tex(2)) * 3(삼각형)
    }

    public void prepare() {
        int n_bytes_per_vertex, n_bytes_per_triangels, n_total_triangles = 0;

        n_bytes_per_vertex = 8 * 4;
        n_bytes_per_triangels = 3 * n_bytes_per_vertex;

        n_total_triangles = n_triangles;

        GLES30.glGenBuffers(1, mVBO, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, n_total_triangles * n_bytes_per_triangels, null, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferSubData(
                GLES30.GL_ARRAY_BUFFER,
                vertex_offset * n_bytes_per_vertex, // 위치에서
                n_triangles * n_bytes_per_triangels, // 이정도 만큼
                BufferConverter.floatArrayToBuffer(vertices));


        // graphic memory에 정점 데이터가 복사 됨. 기존의 데이터는 삭제한다.
        vertices = null;

        GLES30.glGenVertexArrays(1, mVAO, 0);
        GLES30.glBindVertexArray(mVAO[0]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[0]);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, n_bytes_per_vertex, 0);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, n_bytes_per_vertex, 3 * 4);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, n_bytes_per_vertex, 6 * 4);
        GLES30.glEnableVertexAttribArray(2);

        GLES30.glBindVertexArray(0);
    }

    public void draw() {
        GLES30.glBindVertexArray(mVAO[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, vertex_offset, 3 * n_triangles);
        GLES30.glBindVertexArray(0);
    }
}
