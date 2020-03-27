package com.cyp.carpark.core;



import static org.bytedeco.javacpp.opencv_core.CV_32FC1;

import java.util.HashMap;
import java.util.Map;

import com.cyp.carpark.utils.Convert;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ml.CvANN_MLP;


public class CharsIdentify {

    public CharsIdentify() {
        loadModel();

        if (this.map.isEmpty()) {
            map.put("zh_cuan", "��");
            map.put("zh_e", "��");
            map.put("zh_gan", "��");
            map.put("zh_gan1", "��");
            map.put("zh_gui", "��");
            map.put("zh_gui1", "��");
            map.put("zh_hei", "��");
            map.put("zh_hu", "��");
            map.put("zh_ji", "��");
            map.put("zh_jin", "��");
            map.put("zh_jing", "��");
            map.put("zh_jl", "��");
            map.put("zh_liao", "��");
            map.put("zh_lu", "³");
            map.put("zh_meng", "��");
            map.put("zh_min", "��");
            map.put("zh_ning", "��");
            map.put("zh_qing", "��");
            map.put("zh_qiong", "��");
            map.put("zh_shan", "��");
            map.put("zh_su", "��");
            map.put("zh_sx", "��");
            map.put("zh_wan", "��");
            map.put("zh_xiang", "��");
            map.put("zh_xin", "��");
            map.put("zh_yu", "ԥ");
            map.put("zh_yu1", "��");
            map.put("zh_yue", "��");
            map.put("zh_yun", "��");
            map.put("zh_zang", "��");
            map.put("zh_zhe", "��");
        }
    }


    /**
     * 
     * @param input
     * @param isChinese
     * @param isSpeci
     * @return result
     */
    public String charsIdentify(final Mat input, final Boolean isChinese, final Boolean isSpeci) {
        String result = "";

        Mat f = CoreFunc.features(input, this.predictSize);

        int index = classify(f, isChinese, isSpeci);

        if (!isChinese) {
            result = String.valueOf(strCharacters[index]);
        } else {
            String s = strChinese[index - numCharacter];
            result = map.get(s);
        }
        return result;
    }

    /**
     * 
     * @param f
     * @param isChinses
     * @param isSpeci
     * @return result
     */
    private int classify(final Mat f, final Boolean isChinses, final Boolean isSpeci) {
        int result = -1;
        Mat output = new Mat(1, numAll, CV_32FC1);

        ann.predict(f, output);

        int ann_min = (!isChinses) ? ((isSpeci) ? 10 : 0) : numCharacter;
        int ann_max = (!isChinses) ? numCharacter : numAll;

        float maxVal = -2;

        for (int j = ann_min; j < ann_max; j++) {
            float val = Convert.toFloat(output.ptr(0, j));

            if (val > maxVal) {
                maxVal = val;
                result = j;
            }
        }

        return result;
    }

    private void loadModel() {
        loadModel(this.path);
    }

    public void loadModel(String s) {
        this.ann.clear();
        this.ann.load(s, "ann");
    }

    static boolean hasPrint = false;

    public final void setModelPath(String path) {
        this.path = path;
    }

    public final String getModelPath() {
        return this.path;
    }

    private CvANN_MLP ann = new CvANN_MLP();

    private String path = "res/model/ann.xml";

    private int predictSize = 10;

    private Map<String, String> map = new HashMap<String, String>();

    private final char strCharacters[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', /* û��I */'J', 'K', 'L', 'M', 'N', /* û��O */'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z' };
    private final static int numCharacter = 34; // û��I��0,10��������24��Ӣ���ַ�֮��

    private final String strChinese[] = { "zh_cuan" /* �� */, "zh_e" /* �� */, "zh_gan" /* �� */, "zh_gan1"/* �� */,
            "zh_gui"/* �� */, "zh_gui1"/* �� */, "zh_hei" /* �� */, "zh_hu" /* �� */, "zh_ji" /* �� */, "zh_jin" /* �� */,
            "zh_jing" /* �� */, "zh_jl" /* �� */, "zh_liao" /* �� */, "zh_lu" /* ³ */, "zh_meng" /* �� */,
            "zh_min" /* �� */, "zh_ning" /* �� */, "zh_qing" /* �� */, "zh_qiong" /* �� */, "zh_shan" /* �� */,
            "zh_su" /* �� */, "zh_sx" /* �� */, "zh_wan" /* �� */, "zh_xiang" /* �� */, "zh_xin" /* �� */, "zh_yu" /* ԥ */,
            "zh_yu1" /* �� */, "zh_yue" /* �� */, "zh_yun" /* �� */, "zh_zang" /* �� */, "zh_zhe" /* �� */};
    @SuppressWarnings("unused")
    private final static int numChinese = 31;

    private final static int numAll = 65; /* 34+31=65 */
}
