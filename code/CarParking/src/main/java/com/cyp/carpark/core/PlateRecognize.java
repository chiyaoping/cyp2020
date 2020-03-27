package com.cyp.carpark.core;



import org.bytedeco.javacpp.opencv_core.*;

import java.util.Vector;



public class PlateRecognize {

    public int plateRecognize(Mat src, Vector<String> licenseVec) {
        //���Ʒ��鼯��
        Vector<Mat> plateVec = new Vector<Mat>();
        int resultPD = plateDetect.plateDetect(src, plateVec);
        if (resultPD == 0) {
            int num = (int) plateVec.size();
            for (int j = 0; j < num; j++) {
                Mat plate = plateVec.get(j);
                //��ȡ������ɫ
                String plateType = charsRecognise.getPlateType(plate);
                //��ȡ���ƺ�
                String plateIdentify = charsRecognise.charsRecognise(plate);
                String license = plateType + ":" + plateIdentify;
                licenseVec.add(license);
            }
        }
        return resultPD;
    }

    /**
     * �����Ƿ�������ģʽ
     * @param lifemode
     */
    public void setLifemode(boolean lifemode) {
        plateDetect.setPDLifemode(lifemode);
    }

    /**
     * �Ƿ�������ģʽ
     * @param debug
     */
    public void setDebug(boolean debug) {
        plateDetect.setPDDebug(debug);
        charsRecognise.setCRDebug(debug);
    }

    private PlateDetect plateDetect = new PlateDetect();
    private CharsRecognise charsRecognise = new CharsRecognise();
}
