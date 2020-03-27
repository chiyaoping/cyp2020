package com.cyp.carpark.core;



import java.util.Vector;

import org.bytedeco.javacpp.opencv_core.Mat;
import com.cyp.carpark.core.CoreFunc.Color;


public class CharsRecognise {

    public void loadANN(final String s) {
        charsIdentify.loadModel(s);
    }

    /**
     * Chars segment and identify �ַ��ָ���ʶ��
     * 
     * @param plate
     *            the input plate
     * @return the result of plate recognition
     */
    public String charsRecognise(final Mat plate) {

        // the set of plate character after segment �����ַ����鼯��
        Vector<Mat> matVec = new Vector<Mat>();
        // the result of plate recognition
        String plateIdentify = "";

        int result = charsSegment.charsSegment(plate, matVec);
        if (0 == result) {
            for (int j = 0; j < matVec.size(); j++) {
                Mat charMat = matVec.get(j);
                // the first is Chinese char as default Ĭ���׸��ַ����������ַ�
                String charcater = charsIdentify.charsIdentify(charMat, (0 == j), (1 == j));
                plateIdentify = plateIdentify + charcater;
            }
        }

        return plateIdentify;
    }

    /**
     * �Ƿ�������ģʽ
     * 
     * @param isDebug
     */
    public void setCRDebug(final boolean isDebug) {
        charsSegment.setDebug(isDebug);
    }

    /**
     * ��ȡ����ģʽ״̬
     * 
     * @return
     */
    public boolean getCRDebug() {
        return charsSegment.getDebug();
    }

    /**
     * ��ó�����ɫ
     * 
     * @param input
     * @return
     */
    public final String getPlateType(final Mat input) {
        String color = "δ֪";
        Color result = CoreFunc.getPlateType(input, true);
        if (Color.BLUE == result)
            color = "����";
        if (Color.YELLOW == result)
            color = "����";
        return color;
    }

    /**
     * ����������С����
     * 
     * @param param
     */
    public void setLiuDingSize(final int param) {
        charsSegment.setLiuDingSize(param);
    }

    /**
     * ������ɫ��ֵ
     * 
     * @param param
     */
    public void setColorThreshold(final int param) {
        charsSegment.setColorThreshold(param);
    }

    /**
     * ������ɫ�ٷֱ�
     * 
     * @param param
     */
    public void setBluePercent(final float param) {
        charsSegment.setBluePercent(param);
    }

    /**
     * �õ���ɫ�ٷֱ�
     * 
     * @param param
     */
    public final float getBluePercent() {
        return charsSegment.getBluePercent();
    }

    /**
     * ���ð�ɫ�ٷֱ�
     * 
     * @param param
     */
    public void setWhitePercent(final float param) {
        charsSegment.setWhitePercent(param);
    }

    /**
     * �õ���ɫ�ٷֱ�
     * 
     * @param param
     */
    public final float getWhitePercent() {
        return charsSegment.getWhitePercent();
    }

    private CharsSegment charsSegment = new CharsSegment();

    private CharsIdentify charsIdentify = new CharsIdentify();
}
