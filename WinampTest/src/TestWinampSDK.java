import com.qotsa.jni.controller.*;
import com.qotsa.exception.*;

public class TestWinampSDK {

	public static void main(String[] args) {
		WinampController wc = new WinampController();
		try {
			System.out.println(wc.getFileNamePlaying());
			for (int i = 0; i < wc.getPlayListLength(); i++){
				System.out.println(wc.getFileNameInList(i));
			}
			
			wc.appendToPlayList("C:\\Users\\Pitchapong\\Music\\Flure\\Flure - Vanilla\\Flure - Vanilla-Honeymoon feat. Nadia.mp3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
