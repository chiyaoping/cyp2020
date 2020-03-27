package com.cyp.carpark.core;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.util.Vector;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_ml.CvSVM;


public class PlateJudge {

    public PlateJudge() {
        loadModel();
    }

    public void loadModel() {
        loadModel(path);
    }

    public void loadModel(String s) {
        svm.clear();
        svm.load(s, "svm");
    }
    
    /**
     * �Ե���ͼ�����SVM�ж�
     * 
     * @param inMat
     * @return
     */
    public int plateJudge(final Mat inMat) {
        Mat features = this.features.getHistogramFeatures(inMat);
        
        // ͨ��ֱ��ͼ���⻯��Ĳ�ɫͼ����Ԥ��
        Mat p = features.reshape(1, 1);
        p.convertTo(p, CV_32FC1);
        float ret = svm.predict(features);
       
        return (int) ret;
    }

    /**
     * �Զ��ͼ�����SVM�ж�
     * 
     * @param inVec
     * @param resultVec
     * @return
     */
    public int plateJudge(Vector<Mat> inVec, Vector<Mat> resultVec) {

        for (int j = 0; j < inVec.size(); j++) {
            Mat inMat = inVec.get(j);

            if (1 == plateJudge(inMat)) {
                resultVec.add(inMat);
            } else { // ��ȡ�м䲿���ж�һ��
                int w = inMat.cols();
                int h = inMat.rows();

                Mat tmpDes = inMat.clone();
                Mat tmpMat = new Mat(inMat, new Rect((int) (w * 0.05), (int) (h * 0.1), (int) (w * 0.9),
                        (int) (h * 0.8)));
                resize(tmpMat, tmpDes, new Size(inMat.size()));

                if (plateJudge(tmpDes) == 1) {
                    resultVec.add(inMat);
                }
            }
        }

        return 0;
    }

    public void setModelPath(String path) {
        this.path = path;
    }

    public final String getModelPath() {
        return path;
    }

    private CvSVM svm = new CvSVM();

    /**
     * EasyPR��getFeatures�ص�����, ���ڴӳ��Ƶ�image����svm��ѵ������features
     */
    private SVMCallback features = new Features();

    /**
     * ģ�ʹ洢·��
     */
    private String path = "res/model/svm.xml";
}
