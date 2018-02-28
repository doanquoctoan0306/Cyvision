package com.example.tuan_dong.map.Utils;

import android.app.Activity;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by QuangTran on 1/26/2018.
 */

public class TextToSpeechUtils {
    public static final String TEXT_GO_AHEAD = "Đi thẳng";
    public static final String TEXT_TURN_LEFT = "Rẽ trái";
    public static final String TEXT_TURN_RIGHT = "Rẽ phải";
    public static final String TEXT_TURN_SLIGHT_RIGHT = "Rẽ nhẹ bên phải";
    public static final String TEXT_TURN_SLIGHT_LEFT = "Rẽ nhẹ bên trái";
    public static final String TEXT_TURN_AT_ROUNDABOUT = "Đến bùng binh ";
    public static final String TEXT_ARRIVED = "Bạn đã đến nơi";
    public static final String TEXT_REQ_DIRECTION_NO_RESULT = "Không có đường đi thích hợp.";
    public static final String TEXT_REQ_DIRECTION = "Bạn đang yêu cầu đi đến ";
    public static final String TEXT_REQ_DIRECTION_DONE = "Chỉ đường đã sẵn sàng";
    public static final String TEXT_REQ_DIRECTION_DONE_FAR_FROM_START = "Bạn đang cách khá xa điểm xuất phát";
    public static final String TEXT_REQ_DIRECTION_MISSING = "Bạn chưa có địa điểm trước đó";
    public static final String TEXT_REQ_DIRECTION_FAIL = "Tìm đường không thành công";
    public static final String TEXT_INFO_SETUP_PLACE = "Bạn đã thành công cài đặt địa điểm ";
    public static final String TEXT_REQ_DIRECTION_COMEBACK = "Bạn đã yêu cầu quay trở lại";
    public static final String TEXT_REQ_DIRECTION_FACING = "Bạn đang ở hướng ";
    public static final String TEXT_REQ_CALL = "Bạn đang gọi cho ";
    public static final String TEXT_REQ_CALL_FAIL = "Bạn chưa xác định người gọi";
    public static final String TEXT_REQ_SMS = "Bạn đã gửi tin nhắn cho ";
    public static final String TEXT_REQ_SMS_FAIL = "Bạn chưa xác nhận người gửi tin nhắn ";
    public static final String TEXT_REQ_POSITION = "Bạn đang ở ";
    public static final String TEXT_REQ_CANCEL = "Bạn đã huỷ yêu cầu chỉ đường";
    public static final String TEXT_REQ_CANCEL_CALL = "Bạn đã huỷ cuộc gọi";
    public static final String TEXT_INS_CHOOSE_LOCATION = "Bạn hãy chọn địa điểm đến.";
    public static final String TEXT_INS_CANCEL_OR_BACK = "Bấm phím 1 để huỷ yêu cầu chỉ đường. Bấm phím 2 để quay trở lại điểm xuất phát";
    public static final String TEXT_INS_SET_LOCATION = "Bạn đang lựa chọn chức năng cài đặt địa điểm đến. Hãy lựa chọn các nút địa điểm cần lưu vị trí tương ứng.";
    public static final String TEXT_INS_IN_CONSTRUCTION = "Chức năng đang trong quá trình phát triển.";
    public static final String TEXT_INS_PHONE = "Bạn hãy bấm phím 1 để gọi điện khẩn cấp, phím 2 để nhắn tin khẩn cấp.";
    public static final String TEXT_INS_PRE_CALL = "Bạn đã chọn gọi điện khẩn cấp. Hãy chọn người cần gọi";
    public static final String TEXT_INS_PRE_SMS = "Bạn đã chọn gửi tin nhắn khẩn cấp. Hãy chọn người để gửi tin nhắn";

    private static TextToSpeech m_tts;

    private static void init(Activity activity) {
        m_tts = new TextToSpeech(activity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.ERROR)
                    return;
                m_tts.setLanguage(new Locale("vi"));
            }
        });
    }

    public static void speak(Activity activity, String text) {
        if (m_tts == null) {
            TextToSpeechUtils.init(activity);
        }

        while (m_tts.isSpeaking()) {
        }

        if (Build.VERSION.SDK_INT < 21) {
            m_tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            m_tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public static boolean isSpeaking() {
        if (m_tts == null)
            return false;

        return m_tts.isSpeaking();
    }
}
