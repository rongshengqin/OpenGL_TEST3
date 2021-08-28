package com.example.opengl_test3;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;


public class Triangle {
    public int has_error=0;
    public int queue_size=0;
    private final int mProgram;
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
           //"#version 310"+
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec3 vPosition;" +
                    "varying vec4 v_color;"+
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                   "if(vPosition.z==0.0f)"+
                        "v_color=vec4(0.9647f, 0.2745f, 0.3647f,1);"+
                    "else if(vPosition.z==1.0f)"+
                        "v_color=vec4(0.1764f,0.7412f,0.52156f,1);"+
                    "else if(vPosition.z==2.0f)"+
                    "  v_color  =vec4(0.4,0.4,0.4,1);"+
                    "else if(vPosition.z==3.0f)"+
                    "  v_color  =vec4(1.0,1.0,0.0,1);"+
                    "else if(vPosition.z==4.0f)"+
                    "  v_color  =vec4(1,1,1,1);"+
                    "else if(vPosition.z==5.0f)"+
                    "  v_color  =vec4(0,0,1,1);"+
                    "else if(vPosition.z==6.0f)"+ //黄色
                    "  v_color  =vec4(0.9019f, 0.7215f, 0.1803f,1);"+
                    "else if(vPosition.z==7.0f)"+ //紫色
                    "  v_color  =vec4(0.7882f, 0.3294f, 0.6470f,1);"+
                    "  gl_Position = uMVPMatrix * vec4(vPosition.xy,0, 1.0);" +
                    "}";

    // Use to access and set the view transformation
    private int vPMatrixHandle;

    private final String fragmentShaderCode =
            //"#version 310"+
            "precision mediump float;" +
                    "varying vec4 v_color;"+
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor =v_color;" +
                    "}";

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    float color_green[]={ 0.0f, 1f, 0f, 1.0f };
    float color_red[]={ 1.0f, 0f, 0f, 1.0f };
    float color_blue[]={ 0.8f, 0.2f, 0.2f, 1.0f };
    float color_white[]={ 0.8f, 0.8f, 0.8f, 1.0f };
    float color_bg[]={ 0.5f, 0.5f, 0.5f, 1.0f };
    public Triangle() {
        // check es version
        //int[] vers = new int[2];
        //GLES20.glGetIntegerv(GLES20.GL_MAJOR_VERSION, vers, 0);
        //GLES20.glGetIntegerv(GLES20.GL_MINOR_VERSION, vers, 1);
        //System.out.println(" We have at ES version " +vers[0]+"."+vers[1]);
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                1000 *8*3* 4*2); //每条k线有4个顶点，每个顶点用3个浮点数表示，每个浮点数4个字节
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        //vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        //GLES31.glDispatchCompute();
        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = args.limit*2;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private float txt_buf[] =new float[30000];
    //private float info_lines[]=new float[1024*4];
    int num_info_line=0;
    public void draw(float[] mvpMatrix,float[] buf) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        vertexBuffer.put(buf);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
        // draw info lines
//        vertexBuffer.put(info_lines);
//        // set the buffer to read the first coordinate
//        vertexBuffer.position(0);
//        GLES31.glUniform4fv(colorHandle, 1, color_blue, 0);
//        // draw vertex array
//        GLES31.glDrawArrays(GLES31.GL_LINES, 0, args.limit*4);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void draw_grid(float[] mvpMatrix,float[] grid,int ps) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(grid);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color_bg, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, ps);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void draw_indicator(float[] mvpMatrix,float[] grid,int ps) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(grid);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color_bg, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, ps);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void  draw_text(String s,float posx,float posy,float[] mvpMatrix,float color) {
        byte[] bs = s.getBytes();
        int ps = 0;
        short line=0;
        int last_width=0;
        for (int k = 0; k < bs.length; k++) {
            int fid = (int) bs[k]-fonts_terminus_bold_16x32_koi8.char_start;
            short font_width = fonts_terminus_bold_16x32_koi8.descriptors[fid][0];
            short font_offset = fonts_terminus_bold_16x32_koi8.descriptors[fid][1];
            for (int j = 0; j < fonts_terminus_bold_16x32_koi8.font_heigh; j++) {
                for (int i = 0; i < font_width; i++) {
                    if(i%8==0){
                        line = fonts_terminus_bold_16x32_koi8.bitmaps[font_offset + (font_width+7)/8*j+i/8];
                    }
                    if ((line & 0x80) > 0) {
                        txt_buf[ps * 3] =posx+ i+last_width;
                        txt_buf[ps * 3 + 1] =posy -j;
                        txt_buf[ps * 3 + 2] =color;
                        ps+=1;
                    }
                    line=(short)(line<<1);
                }
            }
            last_width+=font_width;
//            if(s.equals(args.ORDER_INFO_STRING))
//                System.out.println(bs.length+" "+k+" "+"ps= "+ps);
        }


        GLES20.glUseProgram(mProgram);
        //GLES31.glEnable( )
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(txt_buf);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color_red, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, ps);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
    public void  draw_samll_text(String s,float posx,float posy,float[] mvpMatrix,float color) {
        byte[] bs = s.getBytes();
        int ps = 0;
        short line=0;
        int last_width=0;
        for (int k = 0; k < bs.length; k++) {
            int fid = (int) bs[k]-fonts_terminus_10x18_iso8859.char_start;
            short font_width = fonts_terminus_10x18_iso8859.descriptors[fid][0];
            short font_offset = fonts_terminus_10x18_iso8859.descriptors[fid][1];
            for (int j = 0; j < fonts_terminus_10x18_iso8859.font_heigh; j++) {
                for (int i = 0; i < font_width; i++) {
                    if(i%8==0){
                        line = fonts_terminus_10x18_iso8859.bitmaps[font_offset + (font_width+7)/8*j+i/8];
                    }
                    if ((line & 0x80) > 0) {
                        txt_buf[ps * 3] =posx+ i+last_width;
                        txt_buf[ps * 3 + 1] =posy -j;
                        txt_buf[ps * 3 + 2] =color;
                        ps+=1;
                    }
                    line=(short)(line<<1);
                }
            }
            last_width+=font_width;
//            if(s.equals(args.ORDER_INFO_STRING))
//                System.out.println(bs.length+" "+k+" "+"ps= "+ps);
        }


        GLES20.glUseProgram(mProgram);
        //GLES31.glEnable( )
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(txt_buf);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color_red, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, ps);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}


