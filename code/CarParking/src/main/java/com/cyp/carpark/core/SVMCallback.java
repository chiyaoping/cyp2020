package com.cyp.carpark.core;



import org.bytedeco.javacpp.opencv_core.Mat;


/**
 * @author Created by junhao.fu
 *
 */
public interface SVMCallback {

    /***
     * EasyPR��getFeatures�ص�����,������������ֱ��ͼ���������Ļص�����
     * 
     * @param image
     * @return
     */
    public abstract Mat getHisteqFeatures(final Mat image);

    /**
     * EasyPR��getFeatures�ص�����, �������ǻ�ȡ��ֱ��ˮƽ��ֱ��ͼͼֵ
     * 
     * @param image
     * @return
     */
    public abstract Mat getHistogramFeatures(final Mat image);

    /**
     * �������ǻ�ȡSITF�����ӵĻص�����
     * 
     * @param image
     * @return
     */
    public abstract Mat getSIFTFeatures(final Mat image);

    /**
     * �������ǻ�ȡHOG�����ӵĻص�����
     * 
     * @param image
     * @return
     */
    public abstract Mat getHOGFeatures(final Mat image);
}
