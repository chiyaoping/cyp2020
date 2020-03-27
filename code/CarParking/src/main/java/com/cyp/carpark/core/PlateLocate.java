package com.cyp.carpark.core;


import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import java.util.Vector;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.RotatedRect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;

public class PlateLocate {

    /**
     * ����ģʽ�빤ҵģʽ�л�
     * 
     * @param islifemode
     *            ���Ϊ�棬�����ø������Ϊ��λ�������Ƭ����ٶ�ͼƬ���Ĳ���������ָ�Ĭ��ֵ��
     * 
     */
    public void setLifemode(boolean islifemode) {
        if (islifemode) {
            setGaussianBlurSize(5);
            setMorphSizeWidth(9);
            setMorphSizeHeight(3);
            setVerifyError(0.9f);
            setVerifyAspect(4);
            setVerifyMin(1);
            setVerifyMax(30);
        } else {
            setGaussianBlurSize(DEFAULT_GAUSSIANBLUR_SIZE);
            setMorphSizeWidth(DEFAULT_MORPH_SIZE_WIDTH);
            setMorphSizeHeight(DEFAULT_MORPH_SIZE_HEIGHT);
            setVerifyError(DEFAULT_ERROR);
            setVerifyAspect(DEFAULT_ASPECT);
            setVerifyMin(DEFAULT_VERIFY_MIN);
            setVerifyMax(DEFAULT_VERIFY_MAX);
        }
    }

    /**
     * ��λ����ͼ��
     * 
     * @param src
     *            ԭʼͼ��
     * @return һ��Mat���������洢����ץȡ����ͼ��
     */
    public Vector<Mat> plateLocate(Mat src) {
        Vector<Mat> resultVec = new Vector<Mat>();

        Mat src_blur = new Mat();
        Mat src_gray = new Mat();
        Mat grad = new Mat();

        int scale = SOBEL_SCALE;
        int delta = SOBEL_DELTA;
        int ddepth = SOBEL_DDEPTH;

        // ��˹ģ����Size�е�����Ӱ�쳵�ƶ�λ��Ч����
        GaussianBlur(src, src_blur, new Size(gaussianBlurSize, gaussianBlurSize), 0, 0, BORDER_DEFAULT);
        if (debug) {
            imwrite("tmp/debug_GaussianBlur.jpg", src_blur);
        }

        // Convert it to gray ��ͼ����лҶȻ�
        cvtColor(src_blur, src_gray, CV_RGB2GRAY);
        if (debug) {
            imwrite("tmp/debug_gray.jpg", src_gray);
        }

        // ��ͼ�����Sobel ���㣬�õ�����ͼ���һ��ˮƽ��������

        // Generate grad_x and grad_y
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();

        Sobel(src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT);
        convertScaleAbs(grad_x, abs_grad_x);

        Sobel(src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT);
        convertScaleAbs(grad_y, abs_grad_y);

        // Total Gradient (approximate)
        addWeighted(abs_grad_x, SOBEL_X_WEIGHT, abs_grad_y, SOBEL_Y_WEIGHT, 0, grad);

        if (debug) {
            imwrite("tmp/debug_Sobel.jpg", grad);
        }

        // ��ͼ����ж�ֵ�������Ҷ�ͼ��ÿ�����ص���256 ��ȡֵ���ܣ�ת��Ϊ��ֵͼ��ÿ�����ص����1 ��0 ����ȡֵ���ܣ���

        Mat img_threshold = new Mat();
        threshold(grad, img_threshold, 0, 255, CV_THRESH_OTSU + CV_THRESH_BINARY);

        if (debug) {
            imwrite("tmp/debug_threshold.jpg", img_threshold);
        }

        // ʹ�ñղ�������ͼ����бղ����Ժ󣬿��Կ��������������ӳ�һ������װ������

        Mat element = getStructuringElement(MORPH_RECT, new Size(morphSizeWidth, morphSizeHeight));
        morphologyEx(img_threshold, img_threshold, MORPH_CLOSE, element);

        if (debug) {
            imwrite("tmp/debug_morphology.jpg", img_threshold);
        }

        // Find ���� of possibles plates �����������ͼ�����е�����������㷨���ȫͼ��������������������Ҫ����ɸѡ��

        MatVector contours = new MatVector();
        findContours(img_threshold, contours, // a vector of contours
                CV_RETR_EXTERNAL, // ��ȡ�ⲿ����
                CV_CHAIN_APPROX_NONE); // all pixels of each contours

        Mat result = new Mat();
        if (debug) {
            // Draw red contours on the source image
            src.copyTo(result);
            drawContours(result, contours, -1, new Scalar(0, 0, 255, 255));
            imwrite("tmp/debug_Contours.jpg", result);
        }

        // Start to iterate to each contour founded
        // ɸѡ������������С��Ӿ��Σ�Ȼ����֤����������������̭��

        Vector<RotatedRect> rects = new Vector<RotatedRect>();

        for (int i = 0; i < contours.size(); ++i) {
            RotatedRect mr = minAreaRect(contours.get(i));
            if (verifySizes(mr))
                rects.add(mr);
        }

        int k = 1;
        for (int i = 0; i < rects.size(); i++) {
            RotatedRect minRect = rects.get(i);
            if (verifySizes(minRect)) {

                if (debug) {
                    Point2f rect_points = new Point2f(4);
                    minRect.points(rect_points);

                    for (int j = 0; j < 4; j++) {
                        Point pt1 = new Point(rect_points.position(j).asCvPoint2D32f());
                        Point pt2 = new Point(rect_points.position((j + 1) % 4).asCvPoint2D32f());

                        line(result, pt1, pt2, new Scalar(0, 255, 255, 255), 1, 8, 0);
                    }
                }

                // rotated rectangle drawing
                // ��ת�ⲿ�ִ���ȷʵ���Խ�ĳЩ��б�ĳ��Ƶ�������������Ҳ���󽫸������ĳ��Ƹ����б�������ۺϿ��ǣ����ǲ�ʹ����δ��롣
                // 2014-08-14,�����µ���һ��ͼƬ�з����кܶ೵������б�ģ���˾����ٴγ�����δ��롣

                float r = minRect.size().width() / minRect.size().height();
                float angle = minRect.angle();
                Size rect_size = new Size((int) minRect.size().width(), (int) minRect.size().height());
                if (r < 1) {
                    angle = 90 + angle;
                    rect_size = new Size(rect_size.height(), rect_size.width());
                }
                // ���ץȡ�ķ�����ת����m_angle�Ƕȣ����ǳ��ƣ���������
                if (angle - this.angle < 0 && angle + this.angle > 0) {
                    // Create and rotate image
                    Mat rotmat = getRotationMatrix2D(minRect.center(), angle, 1);
                    Mat img_rotated = new Mat();
                    warpAffine(src, img_rotated, rotmat, src.size()); // CV_INTER_CUBIC
                    
                    Mat resultMat = showResultMat(img_rotated, rect_size, minRect.center(), k++);
                    resultVec.add(resultMat);
                }
            }
        }
        if (debug) {
            imwrite("tmp/debug_result.jpg", result);
        }

        return resultVec;
    }

    // �������ȡ����

    public void setGaussianBlurSize(int gaussianBlurSize) {
        this.gaussianBlurSize = gaussianBlurSize;
    }

    public final int getGaussianBlurSize() {
        return this.gaussianBlurSize;
    }

    public void setMorphSizeWidth(int morphSizeWidth) {
        this.morphSizeWidth = morphSizeWidth;
    }

    public final int getMorphSizeWidth() {
        return this.morphSizeWidth;
    }

    public void setMorphSizeHeight(int morphSizeHeight) {
        this.morphSizeHeight = morphSizeHeight;
    }

    public final int getMorphSizeHeight() {
        return this.morphSizeHeight;
    }

    public void setVerifyError(float error) {
        this.error = error;
    }

    public final float getVerifyError() {
        return this.error;
    }

    public void setVerifyAspect(float aspect) {
        this.aspect = aspect;
    }

    public final float getVerifyAspect() {
        return this.aspect;
    }

    public void setVerifyMin(int verifyMin) {
        this.verifyMin = verifyMin;
    }

    public void setVerifyMax(int verifyMax) {
        this.verifyMax = verifyMax;
    }

    public void setJudgeAngle(int angle) {
        this.angle = angle;
    }

    /**
     * �Ƿ�������ģʽ
     * 
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * ��ȡ����ģʽ״̬
     * 
     * @return
     */
    public boolean getDebug() {
        return debug;
    }
    
    /**
     * ��minAreaRect��õ���С��Ӿ��Σ����ݺ�Ƚ����ж�
     * 
     * @param mr
     * @return
     */
    private boolean verifySizes(RotatedRect mr) {
        float error = this.error;

        // China car plate size: 440mm*140mm��aspect 3.142857      
        float aspect = this.aspect;
        int min = 44 * 14 * verifyMin; // minimum area
        int max = 44 * 14 * verifyMax; // maximum area
        
        // Get only patchs that match to a respect ratio.
        float rmin = aspect - aspect * error;
        float rmax = aspect + aspect * error;

        int area = (int) (mr.size().height() * mr.size().width());
        float r = mr.size().width() / mr.size().height();
        if (r < 1)
            r = mr.size().height() / mr.size().width();
        
        return area >= min && area <= max && r >= rmin && r <= rmax;
    }

    /**
     * ��ʾ�������ɵĳ���ͼ�񣬱����ж��Ƿ�ɹ���������ת��
     * 
     * @param src
     * @param rect_size
     * @param center
     * @param index
     * @return
     */
    private Mat showResultMat(Mat src, Size rect_size, Point2f center, int index) {
        Mat img_crop = new Mat();
        getRectSubPix(src, rect_size, center, img_crop);

        if (debug) {
            imwrite("tmp/debug_crop_" + index + ".jpg", img_crop);
        }

        Mat resultResized = new Mat();
        resultResized.create(HEIGHT, WIDTH, TYPE);
        resize(img_crop, resultResized, resultResized.size(), 0, 0, INTER_CUBIC);
        if (debug) {
            imwrite("tmp/debug_resize_" + index + ".jpg", resultResized);
        }
        return resultResized;
    }

    // PlateLocate���ó���
    public static final int DEFAULT_GAUSSIANBLUR_SIZE = 5;
    public static final int SOBEL_SCALE = 1;
    public static final int SOBEL_DELTA = 0;
    public static final int SOBEL_DDEPTH = CV_16S;
    public static final int SOBEL_X_WEIGHT = 1;
    public static final int SOBEL_Y_WEIGHT = 0;
    public static final int DEFAULT_MORPH_SIZE_WIDTH = 17;
    public static final int DEFAULT_MORPH_SIZE_HEIGHT = 3;

    // showResultMat���ó���
    public static final int WIDTH = 136;
    public static final int HEIGHT = 36;
    public static final int TYPE = CV_8UC3;

    // verifySize���ó���
    public static final int DEFAULT_VERIFY_MIN = 3;
    public static final int DEFAULT_VERIFY_MAX = 20;

    final float DEFAULT_ERROR = 0.6f;
    final float DEFAULT_ASPECT = 3.75f;
    // �Ƕ��ж����ó���
    public static final int DEFAULT_ANGLE = 30;

    // �Ƿ�������ģʽ����
    public static final boolean DEFAULT_DEBUG = true;

    // ��˹ģ�����ñ���
    protected int gaussianBlurSize = DEFAULT_GAUSSIANBLUR_SIZE;

    // ���Ӳ������ñ���
    protected int morphSizeWidth = DEFAULT_MORPH_SIZE_WIDTH;
    protected int morphSizeHeight = DEFAULT_MORPH_SIZE_HEIGHT;

    // verifySize���ñ���
    protected float error = DEFAULT_ERROR;
    protected float aspect = DEFAULT_ASPECT;
    protected int verifyMin = DEFAULT_VERIFY_MIN;
    protected int verifyMax = DEFAULT_VERIFY_MAX;

    // �Ƕ��ж����ñ���
    protected int angle = DEFAULT_ANGLE;

    // �Ƿ�������ģʽ��0�رգ���0����
    protected boolean debug = DEFAULT_DEBUG;
}
