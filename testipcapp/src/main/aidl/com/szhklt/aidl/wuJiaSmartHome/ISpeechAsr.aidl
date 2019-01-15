package com.szhklt.aidl.wuJiaSmartHome;
import com.szhklt.aidl.wuJiaSmartHome.ISpeechAsrListener;
   
interface ISpeechAsr{
	int startListening(ISpeechAsrListener listener);
	void startSpeaking(java.lang.String text);
}
