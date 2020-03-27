package com.cyp.carpark.core;



import static org.bytedeco.javacpp.opencv_highgui.imwrite;

import java.util.Vector;

import org.bytedeco.javacpp.opencv_core.Mat;


public class PlateDetect {

    /**
     * @param src
     * @param resultVec
     *            �����ǳ��Ƶ�ͼ�鼯��
     * @return the error number
     *         <ul>
     *         <li>0: plate detected successfully;
     *         <li>-1: source Mat is empty;
     *         <li>-2: plate not detected.
     *         </ul>
     */
    public int plateDetect(final Mat src, Vector<Mat> resultVec) {
        Vector<Mat> matVec = plateLocate.plateLocate(src);

        if (0 == matVec.size()) {
            return -1;
        }

        if (0 != plateJudge.plateJudge(matVec, resultVec)) {
            return -2;
        }

        if (getPDDebug()) {
            int size = (int) resultVec.size();
            for (int i = 0; i < size; i++) {
                Mat img = resultVec.get(i);
                String str = "tmp/plate_judge_result_" + Integer.valueOf(i).toString() + ".jpg";
                imwrite(str, img);
            }
        }

        return 0;
    }

    /**
     * ����ģʽ�빤ҵģʽ�л�
     * 
     * @param pdLifemode
     */
    public void setPDLifemode(boolean pdLifemode) {
        plateLocate.setLifemode(pdLifemode);
    }

    /**
     * �Ƿ�������ģʽ
     * 
     * @param pdDebug
     */
    public void setPDDebug(boolean pdDebug) {
        plateLocate.setDebug(pdDebug);
    }

    /**
     * ��ȡ����ģʽ״̬
     * 
     * @return
     */
    public boolean getPDDebug() {
        return plateLocate.getDebug();
    }

    public void setGaussianBlurSize(int gaussianBlurSize) {
        plateLocate.setGaussianBlurSize(gaussianBlurSize);
    }

    public final int getGaussianBlurSize() {
        return plateLocate.getGaussianBlurSize();
    }

    public void setMorphSizeWidth(int morphSizeWidth) {
        plateLocate.setMorphSizeWidth(morphSizeWidth);
    }

    public final int getMorphSizeWidth() {
        return plateLocate.getMorphSizeWidth();
    }

    public void setMorphSizeHeight(int morphSizeHeight) {
        plateLocate.setMorphSizeHeight(morphSizeHeight);
    }

    public final int getMorphSizeHeight() {
        return plateLocate.getMorphSizeHeight();
    }

    public void setVerifyError(float verifyError) {
        plateLocate.setVerifyError(verifyError);
    }

    public final float getVerifyError() {
        return plateLocate.getVerifyError();
    }

    public void setVerifyAspect(float verifyAspect) {
        plateLocate.setVerifyAspect(verifyAspect);
    }

    public final float getVerifyAspect() {
        return plateLocate.getVerifyAspect();
    }

    public void setVerifyMin(int verifyMin) {
        plateLocate.setVerifyMin(verifyMin);
    }

    public void setVerifyMax(int verifyMax) {
        plateLocate.setVerifyMax(verifyMax);
    }

    public void setJudgeAngle(int judgeAngle) {
        plateLocate.setJudgeAngle(judgeAngle);
    }

    private PlateLocate plateLocate = new PlateLocate();

    private PlateJudge plateJudge = new PlateJudge();

}
