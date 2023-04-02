package com.bescom.simplephotoeditorapp.imageEdit;

import android.opengl.GLES20;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class GPUImageMyPhotoFilter extends GPUImageFilter {
    public static final String EXPOSURE_CONTRAST_FRAGMENT_SHADER = "" +
            " varying highp vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform highp float exposure;\n" +
            " uniform lowp float contrast;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "     \n" +
            "     gl_FragColor = vec4((textureColor.rgb * pow(2.0, exposure) - vec3(0.5)) * contrast + vec3(0.5), textureColor.w);\n" +
            " } ";

    private int exposureLocation;
    private float exposure;
    private int contrastLocation;
    private float contrast;


    public GPUImageMyPhotoFilter(final float exposure, final float contrast) {
        super(NO_FILTER_VERTEX_SHADER, EXPOSURE_CONTRAST_FRAGMENT_SHADER);
        this.contrast = contrast;
        this.exposure = exposure;
    }

    @Override
    public void onInit() {
        super.onInit();
        exposureLocation = GLES20.glGetUniformLocation(getProgram(), "exposure");
        contrastLocation = GLES20.glGetUniformLocation(getProgram(), "contrast");
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        set(exposure, contrast);
    }

    public void set(final float exposure, final float contrast) {
        this.exposure = exposure;
        this.contrast = contrast;
        setFloat(exposureLocation, this.exposure);
        setFloat(contrastLocation, this.contrast);
    }
}
